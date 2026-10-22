package cl.duoc.pagoflex.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cl.duoc.pagoflex.R

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun PagoFlexTheme(
    content: @Composable () -> Unit
) {
    val colores = lightColorScheme(
        primary = colorResource(R.color.primario),
        onPrimary = colorResource(R.color.blanco),

        primaryContainer = colorResource(
            R.color.primario_claro
        ),
        onPrimaryContainer = colorResource(
            R.color.primario_oscuro
        ),

        secondary = colorResource(R.color.secundario),
        tertiary = colorResource(R.color.acento),

        background = colorResource(R.color.fondo_claro),
        onBackground = colorResource(R.color.texto_oscuro),

        surface = colorResource(R.color.superficie_clara),
        onSurface = colorResource(R.color.texto_oscuro),

        error = colorResource(R.color.error)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = true
                insetsController.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colores,
        typography = Tipografia,
        shapes = Formas,
        content = content
    )
}
