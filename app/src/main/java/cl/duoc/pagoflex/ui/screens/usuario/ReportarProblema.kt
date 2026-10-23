
package cl.duoc.pagoflex.ui.screens.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.res.painterResource

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.ui.theme.Dimens

@Composable
fun ReportarProblemaScreen(
    compromiso: Compromiso,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onEnviarReporte: (String, String) -> Boolean
) {

    var reporteEnviado by rememberSaveable {
        mutableStateOf(false)
    }


    var motivo by rememberSaveable {
        mutableStateOf("")
    }

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var intentoEnviar by rememberSaveable {
        mutableStateOf(false)
    }

    var errorGuardar by remember {
        mutableStateOf(false)
    }

    val motivos = listOf(
        stringResource(R.string.reporte_motivo_monto),
        stringResource(R.string.reporte_motivo_pago),
        stringResource(R.string.reporte_motivo_otro)
    )

    val errorMotivo = intentoEnviar && motivo.isEmpty()

    val errorDescripcion =
        intentoEnviar && descripcion.trim().length < 10

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {

        OutlinedButton(onClick = onVolver) {
            Text(stringResource(R.string.volver))
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        Text(
            text = stringResource(R.string.reporte_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        Text(
            text = compromiso.empresa,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = compromiso.folio,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        // Seleccionar el motivo del problema
        Text(
            text = stringResource(R.string.reporte_motivo),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioChico)
        )

        motivos.forEach { opcion ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        motivo = opcion
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = motivo == opcion,
                    onClick = {
                        motivo = opcion
                    }
                )

                Text(
                    text = opcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (errorMotivo) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        id = android.R.drawable.ic_dialog_alert
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(Dimens.espacioChico))

                Text(
                    text = stringResource(R.string.reporte_error_motivo),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        // Campo de descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { nuevoTexto ->
                if (nuevoTexto.length <= 250) {
                    descripcion = nuevoTexto
                }
            },
            label = {
                Text(stringResource(R.string.reporte_descripcion))
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            isError = errorDescripcion,
            supportingText = {
                Text(
                    if (errorDescripcion) {
                        stringResource(R.string.reporte_error_descripcion)
                    } else {
                        stringResource(R.string.reporte_descripcion_ayuda)
                    }
                )
            },
            trailingIcon = {
                if (errorDescripcion) {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.ic_dialog_alert
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )

                }
            }
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        // Enviar el formulario
        Button(
            onClick = {

                intentoEnviar = true
                errorGuardar = false

                if (motivo.isNotEmpty() &&
                    descripcion.trim().length >= 10
                ) {
                    val registrado = onEnviarReporte(
                        motivo,
                        descripcion.trim()
                    )


                    if (registrado) {
                        reporteEnviado = true
                    } else {
                        errorGuardar = true
                    }
                }

            },
            enabled = !reporteEnviado,
            modifier = Modifier.fillMaxWidth()

        ) {
            Text(stringResource(R.string.reporte_guardar))
        }

        if (errorGuardar) {

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            Text(
                text = stringResource(R.string.reporte_error_guardar),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (reporteEnviado) {

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            Text(
                text = stringResource(R.string.reporte_exito),
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}
