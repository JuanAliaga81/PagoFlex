
package cl.duoc.pagoflex.ui.screens.agente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.model.EstadoReporte
import cl.duoc.pagoflex.model.ReporteProblema
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens
import cl.duoc.pagoflex.util.cargarEvidencia

@Composable
fun InicioAgenteScreen(
    reportes: List<ReporteProblema>,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onIniciarGestion: (String) -> Boolean,
    onResponderReporte: (String, String) -> Boolean,
    onResolverReporte: (String) -> Boolean
) {

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
            text = stringResource(R.string.agente_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        Text(
            text = stringResource(R.string.agente_subtitulo),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion()
        ) {
            Text(
                text = stringResource(
                    R.string.agente_total,
                    reportes.size
                ),
                modifier = Modifier.padding(Dimens.espacioPantalla),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        if (reportes.isEmpty()) {

            Text(
                text = stringResource(R.string.agente_sin_reportes),
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            reportes.forEach { reporte ->

                TarjetaReporte(
                    reporte = reporte,
                    onIniciarGestion = onIniciarGestion,
                    onResponderReporte = onResponderReporte,
                    onResolverReporte = onResolverReporte
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )
            }
        }
    }
}

@Composable
fun TarjetaReporte(
    reporte: ReporteProblema,
    onIniciarGestion: (String) -> Boolean,
    onResponderReporte: (String, String) -> Boolean,
    onResolverReporte: (String) -> Boolean
) {

    var respuestaTexto by rememberSaveable(reporte.codigo) {
        mutableStateOf(reporte.respuesta)
    }

    var errorRespuesta by remember(reporte.codigo) {
        mutableStateOf(false)
    }

    var respuestaGuardada by remember(reporte.codigo) {
        mutableStateOf(false)
    }

    // Recuperar la fotografía adjunta al reporte
    val context = LocalContext.current

    val imagenEvidencia = remember(
        context,
        reporte.archivoEvidencia
    ) {
        reporte.archivoEvidencia?.let { nombreArchivo ->
            cargarEvidencia(context, nombreArchivo)
        }
    }

    // Texto del estado actual
    val estadoTexto = when (reporte.estado) {

        EstadoReporte.PENDIENTE ->
            stringResource(R.string.agente_pendiente)

        EstadoReporte.EN_PROCESO ->
            stringResource(R.string.agente_en_proceso)

        EstadoReporte.RESUELTO ->
            stringResource(R.string.agente_resuelto)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.colores(),
        elevation = EstilosTarjeta.elevacion()
    ) {

        Column(
            modifier = Modifier.padding(Dimens.espacioMedio)
        ) {

            Text(
                text = stringResource(
                    R.string.agente_codigo,
                    reporte.codigo
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(Dimens.espacioChico)
            )

            Text(
                text = stringResource(
                    R.string.agente_folio,
                    reporte.folioCompromiso
                )
            )

            Text(
                text = stringResource(
                    R.string.agente_motivo,
                    reporte.motivo
                )
            )

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            Text(
                text = stringResource(R.string.agente_descripcion),
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = reporte.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            // Mostrar la evidencia fotográfica si existe
            if (reporte.archivoEvidencia != null) {

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                Text(
                    text = stringResource(R.string.agente_evidencia),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioChico)
                )

                if (imagenEvidencia != null) {

                    Image(
                        bitmap = imagenEvidencia.asImageBitmap(),
                        contentDescription = stringResource(
                            R.string.reporte_vista_previa
                        ),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                } else {

                    Text(
                        text = stringResource(
                            R.string.agente_evidencia_no_disponible
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            Text(
                text = estadoTexto,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            // Acciones según el estado del reporte
            when (reporte.estado) {

                EstadoReporte.PENDIENTE -> {

                    Button(
                        onClick = {
                            onIniciarGestion(reporte.codigo)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.agente_iniciar)
                        )
                    }
                }

                EstadoReporte.EN_PROCESO -> {

                    // Respuesta del agente
                    OutlinedTextField(
                        value = respuestaTexto,
                        onValueChange = { texto ->

                            if (texto.length <= 500) {
                                respuestaTexto = texto
                                errorRespuesta = false
                                respuestaGuardada = false
                            }
                        },
                        label = {
                            Text(
                                stringResource(R.string.agente_responder)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        isError = errorRespuesta,
                        supportingText = {
                            Text(
                                if (errorRespuesta) {
                                    stringResource(
                                        R.string.agente_error_respuesta
                                    )
                                } else {
                                    stringResource(
                                        R.string.agente_respuesta_ayuda
                                    )
                                }
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(
                            Dimens.espacioMedio
                        )
                    )

                    // Guardar respuesta
                    OutlinedButton(
                        onClick = {

                            val guardado = onResponderReporte(
                                reporte.codigo,
                                respuestaTexto
                            )

                            if (guardado) {
                                respuestaGuardada = true
                                errorRespuesta = false
                            } else {
                                errorRespuesta = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(
                                R.string.agente_guardar_respuesta
                            )
                        )
                    }

                    if (respuestaGuardada) {
                        Text(
                            text = stringResource(
                                R.string.agente_respuesta_guardada
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(Dimens.espacioMedio)
                    )

                    // Solo resolver cuando la respuesta esté guardada
                    val puedeResolver =
                        reporte.respuesta.trim().length >= 10 &&
                                respuestaTexto.trim() == reporte.respuesta

                    Button(
                        onClick = {
                            onResolverReporte(reporte.codigo)
                        },
                        enabled = puedeResolver,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.agente_resolver)
                        )
                    }

                    if (!puedeResolver) {
                        Text(
                            text = stringResource(
                                R.string.agente_guardar_primero
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                EstadoReporte.RESUELTO -> {

                    // Mostrar la respuesta sin permitir editarla
                    Text(
                        text = stringResource(
                            R.string.agente_respuesta_final
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = reporte.respuesta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
