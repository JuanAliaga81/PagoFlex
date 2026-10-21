package cl.duoc.pagoflex.ui.styles


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import cl.duoc.pagoflex.ui.theme.Dimens

// Importar Dimens desde ui.theme

object EstilosTarjeta {

    val forma = RoundedCornerShape(Dimens.radioTarjeta)

    @Composable
    fun colores(): CardColors {
        return CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    @Composable
    fun elevacion(): CardElevation {
        return CardDefaults.cardElevation(
            defaultElevation = Dimens.elevacionTarjeta
        )
    }
}
