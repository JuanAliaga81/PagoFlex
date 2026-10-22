package cl.duoc.pagoflex.model

data class ComprobantePago(
    val codigo: String,
    val folioCompromiso: String,
    val empresa: String,
    val concepto: String,
    val montoTotal: Int,
    val fechaPago: String
)
