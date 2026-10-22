
package cl.duoc.pagoflex.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.EstadoCompromiso
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
}
