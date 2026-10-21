package cl.duoc.pagoflex


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.pagoflex.ui.screens.SeleccionRolScreen
import cl.duoc.pagoflex.ui.theme.PagoFlexTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Tema principal de PagoFlex
            PagoFlexTheme {

                // Guarda temporalmente el rol seleccionado
                var rolSeleccionado by rememberSaveable {
                    mutableStateOf("")
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->

                    if (rolSeleccionado.isEmpty()) {

                        // Pantalla de selección de usuario
                        SeleccionRolScreen(
                            modifier = Modifier.padding(padding),
                            onSeleccionarRol = { rol ->
                                rolSeleccionado = rol
                            }
                        )

                    } else {

                        // Pantalla temporal según el rol
                        PantallaTemporal(
                            rol = rolSeleccionado,
                            modifier = Modifier.padding(padding),
                            onVolver = {
                                rolSeleccionado = ""
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaTemporal(
    rol: String,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.bienvenido),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = stringResource(R.string.ingreso_rol, rol),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedButton(
            onClick = onVolver
        ) {
            Text(
                text = stringResource(R.string.volver)
            )
        }
    }
}
