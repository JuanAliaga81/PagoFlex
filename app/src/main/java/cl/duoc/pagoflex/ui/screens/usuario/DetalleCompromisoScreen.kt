
package cl.duoc.pagoflex.ui.screens.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.ComprobantePago
import cl.duoc.pagoflex.model.EstadoCompromiso
import cl.duoc.pagoflex.model.MotivoBloqueoPago
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens

import java.text.NumberFormat
import java.util.Locale


@Composable
fun DetalleCompromisoScreen(
    compromiso: Compromiso,
    motivoBloqueo: MotivoBloqueoPago?,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onConfirmarPago: (String) -> ComprobantePago?,
    onReportarProblema: () -> Unit
) {


    val formato = NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    )

    val total = compromiso.monto + compromiso.recargo

    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    var comprobanteCreado by remember {
        mutableStateOf<ComprobantePago?>(null)
    }

    var mostrarError by remember {
        mutableStateOf(false)
    }

    val estadoTexto = when (compromiso.estado) {

        EstadoCompromiso.PENDIENTE ->
            stringResource(R.string.estado_pendiente)

        EstadoCompromiso.VENCIDO ->
            stringResource(R.string.estado_vencido)

        EstadoCompromiso.PAGADO ->
            stringResource(R.string.estado_pagado)

        EstadoCompromiso.EN_REVISION ->
            stringResource(R.string.estado_revision)

        EstadoCompromiso.ANULADO ->
            stringResource(R.string.estado_anulado)
    }

    val mensajeBloqueo = when (motivoBloqueo) {

        MotivoBloqueoPago.NO_ENCONTRADO ->
            stringResource(R.string.pago_no_encontrado)

        MotivoBloqueoPago.YA_PAGADO ->
            stringResource(R.string.pago_ya_pagado)

        MotivoBloqueoPago.EN_REVISION ->
            stringResource(R.string.pago_en_revision)

        MotivoBloqueoPago.ANULADO ->
            stringResource(R.string.pago_anulado)

        MotivoBloqueoPago.COMPROMISO_ANTERIOR ->
            stringResource(R.string.pago_orden)

        null -> null
    }

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
            text = stringResource(R.string.detalle_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion()
        ) {

            Column(
                modifier = Modifier.padding(
                    Dimens.espacioPantalla
                )
            ) {

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_folio),
                    valor = compromiso.folio
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_empresa),
                    valor = compromiso.empresa
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_concepto),
                    valor = compromiso.concepto
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_monto),
                    valor = stringResource(
                        R.string.formato_monto,
                        formato.format(compromiso.monto)
                    )
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_recargo),
                    valor = stringResource(
                        R.string.formato_monto,
                        formato.format(compromiso.recargo)
                    )
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_vencimiento),
                    valor = compromiso.fechaVencimiento
                )

                DatoDetalle(
                    titulo = stringResource(R.string.detalle_estado),
                    valor = estadoTexto
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                Text(
                    text = stringResource(R.string.detalle_total),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(
                        R.string.formato_monto,
                        formato.format(total)
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        Button(
            onClick = {
                mostrarConfirmacion = true
            },
            enabled = motivoBloqueo == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.pagar_compromiso)
            )
        }


        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        OutlinedButton(
            onClick = onReportarProblema,
            enabled = compromiso.estado != EstadoCompromiso.EN_REVISION &&
                    compromiso.estado != EstadoCompromiso.ANULADO,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.reporte_boton)
            )
        }


        if (mensajeBloqueo != null) {

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            Text(
                text = mensajeBloqueo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (mostrarConfirmacion) {

        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },
            title = {
                Text(
                    stringResource(R.string.pago_confirmar_titulo)
                )
            },
            text = {
                Text(
                    stringResource(R.string.pago_confirmar_mensaje)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        val comprobante = onConfirmarPago(
                            compromiso.folio
                        )

                        mostrarConfirmacion = false

                        if (comprobante != null) {
                            comprobanteCreado = comprobante
                        } else {
                            mostrarError = true
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.pago_confirmar)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text(
                        stringResource(R.string.cancelar)
                    )
                }
            }
        )
    }

    comprobanteCreado?.let { comprobante ->

        AlertDialog(
            onDismissRequest = {
                comprobanteCreado = null
            },
            title = {
                Text(
                    stringResource(R.string.comprobante_titulo)
                )
            },
            text = {

                Column {

                    Text(
                        stringResource(
                            R.string.comprobante_codigo,
                            comprobante.codigo
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_folio,
                            comprobante.folioCompromiso
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_empresa,
                            comprobante.empresa
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_monto,
                            formato.format(comprobante.montoTotal)
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_fecha,
                            comprobante.fechaPago
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(
                            Dimens.espacioMedio
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        comprobanteCreado = null
                    }
                ) {
                    Text(
                        stringResource(R.string.cerrar)
                    )
                }
            }
        )
    }

    if (mostrarError) {

        AlertDialog(
            onDismissRequest = {
                mostrarError = false
            },
            title = {
                Text(
                    stringResource(R.string.pago_error_titulo)
                )
            },
            text = {
                Text(
                    stringResource(R.string.pago_error_mensaje)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarError = false
                    }
                ) {
                    Text(
                        stringResource(R.string.entendido)
                    )
                }
            }
        )
    }
}

@Composable
private fun DatoDetalle(
    titulo: String,
    valor: String
) {

    Text(
        text = titulo,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(
        modifier = Modifier.height(Dimens.espacioChico)
    )

    Text(
        text = valor,
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(
        modifier = Modifier.height(Dimens.espacioMedio)
    )
}
