
package cl.duoc.pagoflex.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.EstadoCompromiso
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InicioEjecutivoScreen(
    compromisos: List<Compromiso>,
    empresa: String,
    motivosAnulacion: Map<String, String>,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onNuevoCompromiso: () -> Unit,
    onAnularCompromiso: (String, String) -> Boolean
) {

    var filtroSeleccionado by rememberSaveable {
        mutableStateOf("Todos")
    }

    var folioEnAnulacion by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var motivo by rememberSaveable {
        mutableStateOf("")
    }

    var mensajeError by rememberSaveable {
        mutableStateOf("")
    }

    // Mostrar solamente compromisos de esta empresa
    val compromisosEmpresa = compromisos.filter {
        it.empresa == empresa
    }

    // Aplicar filtro seleccionado
    val compromisosFiltrados = compromisosEmpresa.filter {

        when (filtroSeleccionado) {

            "Pendientes" ->
                it.estado == EstadoCompromiso.PENDIENTE

            "Vencidos" ->
                it.estado == EstadoCompromiso.VENCIDO

            "Pagados" ->
                it.estado == EstadoCompromiso.PAGADO

            "En revisión" ->
                it.estado == EstadoCompromiso.EN_REVISION

            "Anulados" ->
                it.estado == EstadoCompromiso.ANULADO

            else -> true
        }
    }

    // Calcular el total pendiente
    val totalPendiente = compromisosEmpresa
        .filter {
            it.estado == EstadoCompromiso.PENDIENTE ||
                    it.estado == EstadoCompromiso.VENCIDO
        }
        .sumOf {
            it.monto + it.recargo
        }

    val formatoMoneda = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("es-CL")
    )

    val filtros = listOf(
        "Todos",
        "Pendientes",
        "Vencidos",
        "Pagados",
        "En revisión",
        "Anulados"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Panel del Ejecutivo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = empresa,
            style = MaterialTheme.typography.titleLarge
        )

        // Resumen de cobranza
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Resumen de cobranza",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Compromisos registrados: ${compromisosEmpresa.size}"
                )

                Text(
                    text = "Total pendiente: ${
                        formatoMoneda.format(totalPendiente)
                    }"
                )
            }
        }

        // Registrar nuevos compromisos
        Button(
            onClick = onNuevoCompromiso,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar nuevo compromiso")
        }

        Text(
            text = "Compromisos de la empresa",
            style = MaterialTheme.typography.titleLarge
        )

        // Filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),

            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            filtros.forEach { filtro ->

                FilterChip(
                    selected = filtroSeleccionado == filtro,

                    onClick = {
                        filtroSeleccionado = filtro
                        folioEnAnulacion = null
                        motivo = ""
                        mensajeError = ""
                    },

                    label = {
                        Text(filtro)
                    }
                )
            }
        }

        Text(
            text = "Compromisos encontrados: ${compromisosFiltrados.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (compromisosFiltrados.isEmpty()) {

            Text(
                text = "No existen compromisos para este filtro."
            )

        } else {

            compromisosFiltrados.forEach { compromiso ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = compromiso.folio,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = compromiso.concepto
                        )

                        Text(
                            text = "Monto: ${
                                formatoMoneda.format(
                                    compromiso.monto + compromiso.recargo
                                )
                            }"
                        )

                        Text(
                            text = "Vencimiento: ${compromiso.fechaVencimiento}"
                        )

                        Text(
                            text = "Estado: ${
                                compromiso.estado.name.replace("_", " ")
                            }"
                        )

                        // Mostrar motivo si fue anulado
                        if (
                            compromiso.estado == EstadoCompromiso.ANULADO
                        ) {

                            val motivoRegistrado =
                                motivosAnulacion[compromiso.folio]

                            if (!motivoRegistrado.isNullOrBlank()) {

                                Text(
                                    text = "Motivo de anulación: $motivoRegistrado",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Botón disponible solo en estados permitidos
                        val puedeAnular =
                            compromiso.estado == EstadoCompromiso.PENDIENTE ||
                                    compromiso.estado == EstadoCompromiso.VENCIDO

                        if (puedeAnular) {

                            if (
                                folioEnAnulacion == compromiso.folio
                            ) {

                                Text(
                                    text = "Anular compromiso",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                OutlinedTextField(
                                    value = motivo,

                                    onValueChange = {
                                        motivo = it
                                        mensajeError = ""
                                    },

                                    label = {
                                        Text("Motivo de anulación")
                                    },

                                    placeholder = {
                                        Text("Explica por qué se anula")
                                    },

                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (mensajeError.isNotEmpty()) {

                                    Text(
                                        text = mensajeError,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = {

                                        if (motivo.trim().length < 10) {

                                            mensajeError =
                                                "El motivo debe tener al menos 10 caracteres."

                                        } else {

                                            val anulado = onAnularCompromiso(
                                                compromiso.folio,
                                                motivo
                                            )

                                            if (anulado) {

                                                folioEnAnulacion = null
                                                motivo = ""
                                                mensajeError = ""

                                            } else {

                                                mensajeError =
                                                    "No se pudo anular el compromiso."
                                            }
                                        }
                                    },

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Confirmar anulación")
                                }

                                TextButton(
                                    onClick = {
                                        folioEnAnulacion = null
                                        motivo = ""
                                        mensajeError = ""
                                    },

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cancelar")
                                }

                            } else {

                                OutlinedButton(
                                    onClick = {
                                        folioEnAnulacion = compromiso.folio
                                        motivo = ""
                                        mensajeError = ""
                                    },

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Anular compromiso")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
