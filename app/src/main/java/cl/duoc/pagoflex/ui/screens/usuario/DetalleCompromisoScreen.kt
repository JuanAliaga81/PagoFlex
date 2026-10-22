
package cl.duoc.pagoflex.ui.screens.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.EstadoCompromiso
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetalleCompromisoScreen(
    compromiso: Compromiso,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {
    val formato = NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    )

    val total = compromiso.monto + compromiso.recargo

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
