
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
import cl.duoc.pagoflex.viewmodel.InicioUsuarioViewModel
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens
import java.text.NumberFormat
import java.util.Locale

// Formatea los montos con separador de miles
private fun formatoNumero(monto: Int): String {
    return NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(monto)
}

@Composable
fun InicioUsuarioScreen(
    viewModel: InicioUsuarioViewModel,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
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
            text = stringResource(R.string.inicio_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        // Tarjeta con el resumen financiero
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

                Text(
                    text = stringResource(R.string.inicio_total),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(
                        R.string.formato_monto,
                        formatoNumero(viewModel.totalPorPagar)
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                Text(
                    text = stringResource(R.string.inicio_proximo),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = viewModel.proximoVencimiento.ifEmpty {
                        stringResource(R.string.inicio_sin_vencimientos)
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(Dimens.espacioMedio)
                )

                Text(
                    text = stringResource(R.string.inicio_vencidos),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = viewModel.cantidadVencidos.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        Text(
            text = stringResource(R.string.inicio_compromisos),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        // Muestra los compromisos almacenados en el ViewModel
        viewModel.compromisos.forEach { compromiso ->

            TarjetaCompromiso(compromiso = compromiso)

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )
        }
    }
}

@Composable
fun TarjetaCompromiso(
    compromiso: Compromiso
) {
    // Texto correspondiente al estado
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

    // Color del estado
    val colorEstado = when (compromiso.estado) {
        EstadoCompromiso.VENCIDO ->
            MaterialTheme.colorScheme.error

        EstadoCompromiso.PAGADO ->
            MaterialTheme.colorScheme.secondary

        else -> MaterialTheme.colorScheme.primary
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
                text = compromiso.empresa,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(Dimens.espacioChico)
            )

            Text(
                text = compromiso.concepto,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(
                    R.string.formato_monto,
                    formatoNumero(
                        compromiso.monto + compromiso.recargo
                    )
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(
                    R.string.inicio_vence,
                    compromiso.fechaVencimiento
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = estadoTexto,
                style = MaterialTheme.typography.labelLarge,
                color = colorEstado
            )
        }
    }
}
