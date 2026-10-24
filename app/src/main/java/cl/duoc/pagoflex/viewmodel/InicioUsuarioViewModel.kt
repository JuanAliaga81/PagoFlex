
package cl.duoc.pagoflex.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.ComprobantePago
import cl.duoc.pagoflex.model.EstadoCompromiso
import cl.duoc.pagoflex.model.MotivoBloqueoPago
import cl.duoc.pagoflex.model.ReporteProblema
import cl.duoc.pagoflex.model.EstadoReporte
import cl.duoc.pagoflex.util.AlmacenamientoDatos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InicioUsuarioViewModel : ViewModel() {

    private var contextoAplicacion: Context? = null
    private var almacenamientoDisponible = true

    var compromisos by mutableStateOf(
        listOf(
            Compromiso(
                folio = "CP-001",
                empresa = "Crédito Andino",
                concepto = "Crédito de consumo",
                monto = 86400,
                fechaVencimiento = "25-10-2026",
                estado = EstadoCompromiso.PENDIENTE
            ),
            Compromiso(
                folio = "CP-002",
                empresa = "Inmobiliaria",
                concepto = "Arriendo mensual",
                monto = 520000,
                fechaVencimiento = "28-10-2026",
                estado = EstadoCompromiso.PENDIENTE
            ),
            Compromiso(
                folio = "CP-003",
                empresa = "Club Deportivo",
                concepto = "Cuota mensual",
                monto = 23600,
                fechaVencimiento = "20-10-2026",
                estado = EstadoCompromiso.VENCIDO
            )
        )
    )
        private set

    var comprobantes by mutableStateOf(
        listOf<ComprobantePago>()
    )
        private set

    var reportes by mutableStateOf(
        listOf<ReporteProblema>()
    )
        private set

    var motivosAnulacion by mutableStateOf(
        mapOf<String, String>()
    )
        private set

    fun iniciarAlmacenamiento(context: Context) {

        if (contextoAplicacion != null) {
            return
        }

        val contexto = context.applicationContext

        val existeArchivo = contexto.fileList().contains(
            "pagoflex_datos.json"
        )

        val datos = AlmacenamientoDatos.cargarDatos(contexto)

        if (datos != null) {
            compromisos = datos.compromisos
            comprobantes = datos.comprobantes
            reportes = datos.reportes
            motivosAnulacion = datos.motivosAnulacion
        }

        almacenamientoDisponible = !existeArchivo || datos != null
        contextoAplicacion = contexto
    }

    private fun guardarCambios() {

        if (!almacenamientoDisponible) {
            return
        }

        val contexto = contextoAplicacion ?: return

        AlmacenamientoDatos.guardarDatos(
            context = contexto,
            compromisos = compromisos,
            comprobantes = comprobantes,
            reportes = reportes,
            motivosAnulacion = motivosAnulacion
        )
    }

    private fun convertirFecha(fecha: String): Long {

        val formato = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.forLanguageTag("es-CL")
        )

        formato.isLenient = false

        return try {
            formato.parse(fecha)?.time ?: Long.MAX_VALUE
        } catch (e: java.text.ParseException) {
            Long.MAX_VALUE
        }
    }

    val totalPorPagar: Int
        get() = compromisos
            .filter {
                it.estado == EstadoCompromiso.PENDIENTE ||
                        it.estado == EstadoCompromiso.VENCIDO
            }
            .sumOf {
                it.monto + it.recargo
            }

    val cantidadVencidos: Int
        get() = compromisos.count {
            it.estado == EstadoCompromiso.VENCIDO
        }

    val proximoVencimiento: String
        get() {
            return compromisos
                .filter {
                    it.estado == EstadoCompromiso.PENDIENTE &&
                            convertirFecha(it.fechaVencimiento) != Long.MAX_VALUE
                }
                .minByOrNull {
                    convertirFecha(it.fechaVencimiento)
                }
                ?.fechaVencimiento ?: ""
        }

    fun obtenerCompromiso(folio: String): Compromiso? {

        return compromisos.find {
            it.folio == folio
        }
    }

    fun validarPago(folio: String): MotivoBloqueoPago? {

        val compromiso = obtenerCompromiso(folio)
            ?: return MotivoBloqueoPago.NO_ENCONTRADO

        when (compromiso.estado) {

            EstadoCompromiso.PAGADO ->
                return MotivoBloqueoPago.YA_PAGADO

            EstadoCompromiso.EN_REVISION ->
                return MotivoBloqueoPago.EN_REVISION

            EstadoCompromiso.ANULADO ->
                return MotivoBloqueoPago.ANULADO

            else -> {}
        }

        val fechaActual = convertirFecha(
            compromiso.fechaVencimiento
        )

        if (fechaActual == Long.MAX_VALUE) {
            return MotivoBloqueoPago.NO_ENCONTRADO
        }

        val existeAnterior = compromisos.any { otro ->

            val estadoSinResolver =
                otro.estado == EstadoCompromiso.PENDIENTE ||
                        otro.estado == EstadoCompromiso.VENCIDO ||
                        otro.estado == EstadoCompromiso.EN_REVISION

            otro.folio != compromiso.folio &&
                    otro.empresa == compromiso.empresa &&
                    estadoSinResolver &&
                    convertirFecha(otro.fechaVencimiento) < fechaActual
        }

        if (existeAnterior) {
            return MotivoBloqueoPago.COMPROMISO_ANTERIOR
        }

        return null
    }

    fun simularPago(folio: String): ComprobantePago? {

        if (validarPago(folio) != null) {
            return null
        }

        val compromiso = obtenerCompromiso(folio)
            ?: return null

        val formato = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.forLanguageTag("es-CL")
        )

        val comprobante = ComprobantePago(
            codigo = "SIM-" + UUID.randomUUID()
                .toString()
                .take(8)
                .uppercase(Locale.ROOT),
            folioCompromiso = compromiso.folio,
            empresa = compromiso.empresa,
            concepto = compromiso.concepto,
            montoTotal = compromiso.monto + compromiso.recargo,
            fechaPago = formato.format(Date())
        )

        comprobantes = comprobantes + comprobante

        compromisos = compromisos.map { actual ->

            if (actual.folio == folio) {
                actual.copy(
                    estado = EstadoCompromiso.PAGADO
                )
            } else {
                actual
            }
        }

        guardarCambios()

        return comprobante
    }

    fun registrarReporte(
        folio: String,
        motivo: String,
        descripcion: String,
        archivoEvidencia: String? = null
    ): Boolean {

        if (
            motivo.isBlank() ||
            descripcion.trim().length < 10
        ) {
            return false
        }

        val compromiso = obtenerCompromiso(folio)
            ?: return false

        if (
            compromiso.estado == EstadoCompromiso.EN_REVISION ||
            compromiso.estado == EstadoCompromiso.ANULADO
        ) {
            return false
        }

        val reporte = ReporteProblema(
            codigo = "REP-" + UUID.randomUUID()
                .toString()
                .take(8)
                .uppercase(Locale.ROOT),
            folioCompromiso = folio,
            motivo = motivo,
            descripcion = descripcion.trim(),
            estadoAnterior = compromiso.estado,
            archivoEvidencia = archivoEvidencia
        )

        reportes = reportes + reporte

        compromisos = compromisos.map { actual ->

            if (actual.folio == folio) {
                actual.copy(
                    estado = EstadoCompromiso.EN_REVISION
                )
            } else {
                actual
            }
        }

        guardarCambios()

        return true
    }

    fun iniciarGestionReporte(codigo: String): Boolean {

        val reporte = reportes.find {
            it.codigo == codigo
        } ?: return false

        if (reporte.estado != EstadoReporte.PENDIENTE) {
            return false
        }

        reportes = reportes.map { actual ->

            if (actual.codigo == codigo) {
                actual.copy(
                    estado = EstadoReporte.EN_PROCESO
                )
            } else {
                actual
            }
        }

        guardarCambios()

        return true
    }

    fun responderReporte(
        codigo: String,
        respuesta: String
    ): Boolean {

        val texto = respuesta.trim()

        if (texto.length < 10) {
            return false
        }

        val reporte = reportes.find {
            it.codigo == codigo
        } ?: return false

        if (reporte.estado != EstadoReporte.EN_PROCESO) {
            return false
        }

        reportes = reportes.map { actual ->

            if (actual.codigo == codigo) {
                actual.copy(
                    respuesta = texto
                )
            } else {
                actual
            }
        }

        guardarCambios()

        return true
    }

    fun resolverReporte(codigo: String): Boolean {

        val reporte = reportes.find {
            it.codigo == codigo
        } ?: return false

        if (
            reporte.estado != EstadoReporte.EN_PROCESO ||
            reporte.respuesta.trim().length < 10
        ) {
            return false
        }

        reportes = reportes.map { actual ->

            if (actual.codigo == codigo) {
                actual.copy(
                    estado = EstadoReporte.RESUELTO
                )
            } else {
                actual
            }
        }

        compromisos = compromisos.map { compromiso ->

            if (
                compromiso.folio == reporte.folioCompromiso &&
                compromiso.estado == EstadoCompromiso.EN_REVISION
            ) {
                compromiso.copy(
                    estado = reporte.estadoAnterior
                )
            } else {
                compromiso
            }
        }

        guardarCambios()

        return true
    }

    fun registrarCompromiso(
        empresa: String,
        concepto: String,
        monto: Int,
        fechaVencimiento: String
    ): Boolean {

        if (
            empresa.isBlank() ||
            concepto.trim().length < 3 ||
            monto <= 0
        ) {
            return false
        }

        val fechaTexto = fechaVencimiento.trim()

        if (!Regex("""\d{2}-\d{2}-\d{4}""").matches(fechaTexto)) {
            return false
        }

        val formato = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.forLanguageTag("es-CL")
        )

        formato.isLenient = false

        val fecha = try {
            formato.parse(fechaTexto)
        } catch (e: java.text.ParseException) {
            null
        } ?: return false

        val hoy = formato.parse(
            formato.format(Date())
        ) ?: return false

        if (fecha.before(hoy)) {
            return false
        }

        val ultimoNumero = compromisos.mapNotNull {

            if (it.folio.startsWith("CP-")) {
                it.folio.removePrefix("CP-").toIntOrNull()
            } else {
                null
            }

        }.maxOrNull() ?: 0

        val nuevoFolio = "CP-" +
                (ultimoNumero + 1).toString().padStart(3, '0')

        val nuevoCompromiso = Compromiso(
            folio = nuevoFolio,
            empresa = empresa,
            concepto = concepto.trim(),
            monto = monto,
            fechaVencimiento = fechaTexto,
            estado = EstadoCompromiso.PENDIENTE
        )

        compromisos = compromisos + nuevoCompromiso

        guardarCambios()

        return true
    }

    fun anularCompromiso(
        folio: String,
        motivo: String
    ): Boolean {

        val texto = motivo.trim()

        if (texto.length < 10) {
            return false
        }

        val compromiso = obtenerCompromiso(folio)
            ?: return false

        if (
            compromiso.estado != EstadoCompromiso.PENDIENTE &&
            compromiso.estado != EstadoCompromiso.VENCIDO
        ) {
            return false
        }

        motivosAnulacion = motivosAnulacion + (folio to texto)

        compromisos = compromisos.map { actual ->

            if (actual.folio == folio) {
                actual.copy(
                    estado = EstadoCompromiso.ANULADO
                )
            } else {
                actual
            }
        }

        guardarCambios()

        return true
    }
}
