package cl.duoc.pagoflex.model

data class ReporteProblema(
    val codigo: String,
    val folioCompromiso: String,
    val motivo: String,
    val descripcion: String,
    val estadoAnterior: EstadoCompromiso
)
