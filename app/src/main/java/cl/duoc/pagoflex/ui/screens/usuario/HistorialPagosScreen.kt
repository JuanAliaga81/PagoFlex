
package cl.duoc.pagoflex.ui.screens.usuario

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import cl.duoc.pagoflex.R
import cl.duoc.pagoflex.model.ComprobantePago
import cl.duoc.pagoflex.ui.styles.EstilosTarjeta
import cl.duoc.pagoflex.ui.theme.Dimens

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistorialPagosScreen(
    comprobantes: List<ComprobantePago>,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var recientesPrimero by rememberSaveable {
        mutableStateOf(true)
    }

    var comprobanteSeleccionado by remember {
        mutableStateOf<ComprobantePago?>(null)
    }

    val formatoMonto = remember {
        NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("es-CL")
        )
    }

    val formatoFecha = remember {
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }

    val comprobantesFiltrados = comprobantes.filter { pago ->

        pago.empresa.contains(busqueda.trim(), ignoreCase = true) ||
                pago.folioCompromiso.contains(busqueda.trim(), ignoreCase = true) ||
                pago.codigo.contains(busqueda.trim(), ignoreCase = true) ||
                pago.fechaPago.contains(busqueda.trim())
    }

    val comprobantesOrdenados = comprobantesFiltrados.sortedBy {
        LocalDate.parse(it.fechaPago, formatoFecha)
    }

    val listaFinal = if (recientesPrimero) {
        comprobantesOrdenados.reversed()
    } else {
        comprobantesOrdenados
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {

        OutlinedButton(
            onClick = onVolver
        ) {
            Text(stringResource(R.string.volver))
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        Text(
            text = stringResource(R.string.historial_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text(stringResource(R.string.historial_buscar))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(Dimens.espacioMedio)
        )

        OutlinedButton(
            onClick = {
                recientesPrimero = !recientesPrimero
            }
        ) {
            Text(
                if (recientesPrimero) {
                    stringResource(R.string.historial_recientes)
                } else {
                    stringResource(R.string.historial_antiguos)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(Dimens.espacioGrande)
        )

        if (comprobantes.isEmpty()) {

            Text(
                text = stringResource(R.string.historial_vacio),
                style = MaterialTheme.typography.bodyMedium
            )

        } else if (listaFinal.isEmpty()) {

            Text(
                text = stringResource(R.string.historial_sin_resultados),
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            Text(
                text = stringResource(
                    R.string.historial_ver_comprobante
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(Dimens.espacioMedio)
            )

            listaFinal.forEach { pago ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            comprobanteSeleccionado = pago
                        },
                    shape = EstilosTarjeta.forma,
                    colors = EstilosTarjeta.colores(),
                    elevation = EstilosTarjeta.elevacion()
                ) {

                    Column(
                        modifier = Modifier.padding(
                            Dimens.espacioMedio
                        )
                    ) {

                        Text(
                            text = pago.empresa,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(
                                Dimens.espacioChico
                            )
                        )

                        Text(
                            text = pago.folioCompromiso,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = pago.fechaPago,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = stringResource(
                                R.string.formato_monto,
                                formatoMonto.format(pago.montoTotal)
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        Dimens.espacioMedio
                    )
                )
            }
        }
    }

    comprobanteSeleccionado?.let { pago ->

        AlertDialog(
            onDismissRequest = {
                comprobanteSeleccionado = null
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
                            pago.codigo
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_folio,
                            pago.folioCompromiso
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_empresa,
                            pago.empresa
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_concepto,
                            pago.concepto
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_monto,
                            formatoMonto.format(pago.montoTotal)
                        )
                    )

                    Text(
                        stringResource(
                            R.string.comprobante_fecha,
                            pago.fechaPago
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
                        comprobanteSeleccionado = null
                    }
                ) {
                    Text(stringResource(R.string.cerrar))
                }
            }
        )
    }
}
