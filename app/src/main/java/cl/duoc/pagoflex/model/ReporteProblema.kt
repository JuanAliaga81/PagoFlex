
package cl.duoc.pagoflex.model

enum class EstadoReporte {
    PENDIENTE,
    EN_PROCESO,
    RESUELTO
}

data class ReporteProblema(
    val codigo: String,
    val folioCompromiso: String,
    val motivo: String,
    val descripcion: String,
    val estadoAnterior: EstadoCompromiso,
    val estado: EstadoReporte = EstadoReporte.PENDIENTE,
    val respuesta: String = ""
)
