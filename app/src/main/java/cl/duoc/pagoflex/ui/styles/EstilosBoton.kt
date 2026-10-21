package cl.duoc.pagoflex.ui.styles


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cl.duoc.pagoflex.ui.theme.Dimens

object EstilosBoton {

    val forma = RoundedCornerShape(Dimens.radioBoton)

    val relleno = PaddingValues(
        horizontal = 20.dp,
        vertical = 12.dp
    )

    @Composable
    fun coloresPrincipal(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    @Composable
    fun bordeSecundario(): BorderStroke {
        return BorderStroke(
            Dimens.bordeBoton,
            MaterialTheme.colorScheme.secondary
        )
    }

    @Composable
    fun elevacion(): ButtonElevation {
        return ButtonDefaults.buttonElevation(
            defaultElevation = Dimens.elevacionBoton,
            pressedElevation = 1.dp
        )
    }
}
