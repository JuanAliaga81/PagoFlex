
package cl.duoc.pagoflex.model

enum class EstadoCompromiso {
    PENDIENTE,
    VENCIDO,
    PAGADO,
    EN_REVISION,
    ANULADO
}

data class Compromiso(
    val folio: String,
    val empresa: String,
    val concepto: String,
    val monto: Int,
    val fechaVencimiento: String,
    val estado: EstadoCompromiso,
    val recargo: Int = 0
)

enum class MotivoBloqueoPago {
    NO_ENCONTRADO,
    YA_PAGADO,
    EN_REVISION,
    ANULADO,
    COMPROMISO_ANTERIOR
}
