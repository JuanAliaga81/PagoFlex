
package cl.duoc.pagoflex.viewmodel

import cl.duoc.pagoflex.model.MotivoBloqueoPago
import cl.duoc.pagoflex.model.ComprobantePago
import java.util.UUID
import java.util.Locale
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.EstadoCompromiso
import cl.duoc.pagoflex.model.ReporteProblema
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class InicioUsuarioViewModel : ViewModel() {

    // Datos ficticios para probar la aplicación
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

    // Total de compromisos pendientes y vencidos
    val totalPorPagar: Int
        get() = compromisos
            .filter {
                it.estado == EstadoCompromiso.PENDIENTE ||
                        it.estado == EstadoCompromiso.VENCIDO
            }
            .sumOf { it.monto + it.recargo }

    // Cantidad de compromisos vencidos
    val cantidadVencidos: Int
        get() = compromisos.count {
            it.estado == EstadoCompromiso.VENCIDO
        }

    // Fecha del próximo compromiso pendiente
    val proximoVencimiento: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val formato = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            return compromisos
                .filter {
                    it.estado == EstadoCompromiso.PENDIENTE
                }
                .minByOrNull {
                    LocalDate.parse(it.fechaVencimiento, formato)
                }
                ?.fechaVencimiento ?: ""
        }

    fun obtenerCompromiso(folio: String): Compromiso? {
        return compromisos.find {
            it.folio == folio
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun validarPago(folio: String): MotivoBloqueoPago? {

        // Buscar el compromiso
        val compromiso = obtenerCompromiso(folio)
            ?: return MotivoBloqueoPago.NO_ENCONTRADO

        // Revisar el estado
        when (compromiso.estado) {

            EstadoCompromiso.PAGADO ->
                return MotivoBloqueoPago.YA_PAGADO

            EstadoCompromiso.EN_REVISION ->
                return MotivoBloqueoPago.EN_REVISION

            EstadoCompromiso.ANULADO ->
                return MotivoBloqueoPago.ANULADO

            else -> {}
        }

        // Revisar compromisos anteriores de la misma empresa
        val formato = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val fechaActual = LocalDate.parse(
            compromiso.fechaVencimiento,
            formato
        )

        val existeAnterior = compromisos.any { otro ->

            val estadoSinResolver =
                otro.estado == EstadoCompromiso.PENDIENTE ||
                        otro.estado == EstadoCompromiso.VENCIDO ||
                        otro.estado == EstadoCompromiso.EN_REVISION

            otro.folio != compromiso.folio &&
                    otro.empresa == compromiso.empresa &&
                    estadoSinResolver &&
                    LocalDate.parse(
                        otro.fechaVencimiento,
                        formato
                    ).isBefore(fechaActual)
        }

        if (existeAnterior) {
            return MotivoBloqueoPago.COMPROMISO_ANTERIOR
        }

        // Sin bloqueos: puede continuar al pago simulado
        return null
    }


    // Comprobantes generados durante la sesión
    var comprobantes by mutableStateOf(
        listOf<ComprobantePago>()
    )
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun simularPago(folio: String): ComprobantePago? {

        // Validar nuevamente antes de registrar
        if (validarPago(folio) != null) {
            return null
        }

        val compromiso = obtenerCompromiso(folio)
            ?: return null

        // Crear el comprobante
        val comprobante = ComprobantePago(
            codigo = "SIM-" + UUID.randomUUID()
                .toString()
                .take(8)
                .uppercase(Locale.ROOT),
            folioCompromiso = compromiso.folio,
            empresa = compromiso.empresa,
            concepto = compromiso.concepto,
            montoTotal = compromiso.monto + compromiso.recargo,
            fechaPago = LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            )
        )

        // Guardar comprobante
        comprobantes = comprobantes + comprobante

        // Actualizar el compromiso a PAGADO
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


    // Reportes registrados durante la sesión
    var reportes by mutableStateOf(
        listOf<ReporteProblema>()
    )
        private set

    fun registrarReporte(
        folio: String,
        motivo: String,
        descripcion: String
    ): Boolean {

        // Validar los datos del formulario
        if (motivo.isBlank() || descripcion.trim().length < 10) {
            return false
        }

        // Buscar el compromiso
        val compromiso = obtenerCompromiso(folio)
            ?: return false

        // No permitir reportes duplicados o anulados
        if (
            compromiso.estado == EstadoCompromiso.EN_REVISION ||
            compromiso.estado == EstadoCompromiso.ANULADO
        ) {
            return false
        }

        // Crear el reporte
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

        // Guardar el reporte
        reportes = reportes + reporte

        // Cambiar el compromiso a En revisión
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
}

