package cl.duoc.pagoflex.ui.styles


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import cl.duoc.pagoflex.ui.theme.Dimens

// Importar Dimens desde ui.theme

object EstilosCampo {

    val forma = RoundedCornerShape(Dimens.radioCampo)

    @Composable
    fun colores(): TextFieldColors {
        return OutlinedTextFieldDefaults.colors(
            focusedBorderColor =
                MaterialTheme.colorScheme.primary,

            unfocusedBorderColor =
                MaterialTheme.colorScheme.outline,

            focusedLabelColor =
                MaterialTheme.colorScheme.primary,

            focusedTrailingIconColor =
                MaterialTheme.colorScheme.primary,

            errorBorderColor =
                MaterialTheme.colorScheme.error,

            cursorColor =
                MaterialTheme.colorScheme.primary
        )
    }
}
