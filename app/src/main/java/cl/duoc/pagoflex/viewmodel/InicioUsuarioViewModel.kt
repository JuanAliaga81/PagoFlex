
package cl.duoc.pagoflex.viewmodel

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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InicioUsuarioViewModel : ViewModel() {

    // Datos ficticios de los compromisos
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

    // Listas de pagos y reportes
    var comprobantes by mutableStateOf(
        listOf<ComprobantePago>()
    )
        private set

    var reportes by mutableStateOf(
        listOf<ReporteProblema>()
    )
        private set

    // Convertir una fecha a milisegundos para compararla
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

    // Total pendiente y vencido
    val totalPorPagar: Int
        get() = compromisos
            .filter {
                it.estado == EstadoCompromiso.PENDIENTE ||
                        it.estado == EstadoCompromiso.VENCIDO
            }
            .sumOf {
                it.monto + it.recargo
            }

    // Cantidad de compromisos vencidos
    val cantidadVencidos: Int
        get() = compromisos.count {
            it.estado == EstadoCompromiso.VENCIDO
        }

    // Próximo vencimiento pendiente
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

    // Buscar compromiso por folio
    fun obtenerCompromiso(folio: String): Compromiso? {
        return compromisos.find {
            it.folio == folio
        }
    }

    // Validaciones antes de pagar
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

        // No continuar si la fecha no es válida
        if (fechaActual == Long.MAX_VALUE) {
            return MotivoBloqueoPago.NO_ENCONTRADO
        }

        // Verificar compromisos anteriores de la misma empresa
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

    // Registrar pago simulado
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

        // Guardar el comprobante
        comprobantes = comprobantes + comprobante

        // Actualizar el compromiso a Pagado
        compromisos = compromisos.map { actual ->

            if (actual.folio == folio) {
                actual.copy(
                    estado = EstadoCompromiso.PAGADO
                )
            } else {
                actual
            }
        }

        return comprobante
    }

    // Registrar reporte del usuario
    fun registrarReporte(
        folio: String,
        motivo: String,
        descripcion: String
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
            estadoAnterior = compromiso.estado
        )

        reportes = reportes + reporte

        // El compromiso queda En revisión
        compromisos = compromisos.map { actual ->

            if (actual.folio == folio) {
                actual.copy(
                    estado = EstadoCompromiso.EN_REVISION
                )
            } else {
                actual
            }
        }

        return true
    }

    // AGENTE: Marcar reporte como En proceso
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

        return true
    }

    // AGENTE: Guardar respuesta
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

        return true
    }

    // AGENTE: Resolver el reporte
    fun resolverReporte(codigo: String): Boolean {

        val reporte = reportes.find {
            it.codigo == codigo
        } ?: return false

        // Verificar estado y respuesta
        if (
            reporte.estado != EstadoReporte.EN_PROCESO ||
            reporte.respuesta.trim().length < 10
        ) {
            return false
        }

        // Actualizar el reporte
        reportes = reportes.map { actual ->

            if (actual.codigo == codigo) {
                actual.copy(
                    estado = EstadoReporte.RESUELTO
                )
            } else {
                actual
            }
        }

        // Restaurar el estado anterior del compromiso
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

        return true
    }
}
