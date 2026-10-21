package cl.duoc.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.ui.styles.EstilosBoton
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens

@Composable
fun SeleccionRolScreen(
    modifier: Modifier = Modifier,
    onSeleccionarRol: (String) -> Unit
) {
    val rolUsuario = stringResource(R.string.rol_usuario)
    val rolAgente = stringResource(R.string.rol_agente)
    val rolEjecutivo = stringResource(R.string.rol_ejecutivo)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.espacioPantalla),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.titulo_app),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens.espacioChico))

        Text(
            text = stringResource(R.string.descripcion_app),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.espacioGrande))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion()
        ) {
            Column(
                modifier = Modifier.padding(Dimens.espacioPantalla),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.bienvenido),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioChico)
                )

                Text(
                    text = stringResource(R.string.seleccionar_rol),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioGrande)
                )

                BotonRol(
                    texto = rolUsuario,
                    onClick = { onSeleccionarRol(rolUsuario) }
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                BotonRol(
                    texto = rolAgente,
                    onClick = { onSeleccionarRol(rolAgente) }
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                BotonRol(
                    texto = rolEjecutivo,
                    onClick = { onSeleccionarRol(rolEjecutivo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.espacioGrande))

        Text(
            text = stringResource(R.string.proyecto_academico),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.6f)
        )
    }
}

@Composable
fun BotonRol(
    texto: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.alturaBoton),

        shape = EstilosBoton.forma,
        colors = EstilosBoton.coloresPrincipal()
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
