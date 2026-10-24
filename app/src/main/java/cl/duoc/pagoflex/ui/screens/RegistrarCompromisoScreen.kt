
package cl.duoc.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RegistrarCompromisoScreen(
    empresa: String,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    onRegistrar: (String, Int, String) -> Boolean,
    onGuardado: () -> Unit
) {

    var concepto by rememberSaveable {
        mutableStateOf("")
    }

    var monto by rememberSaveable {
        mutableStateOf("")
    }

    var fechaVencimiento by rememberSaveable {
        mutableStateOf("")
    }

    var mensajeError by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Registrar compromiso",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Empresa: $empresa",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = concepto,
            onValueChange = {
                concepto = it
                mensajeError = ""
            },
            label = {
                Text("Concepto")
            },
            placeholder = {
                Text("Ej: Crédito de consumo")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = monto,
            onValueChange = {
                monto = it
                mensajeError = ""
            },
            label = {
                Text("Monto en pesos")
            },
            placeholder = {
                Text("Ej: 85000")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fechaVencimiento,
            onValueChange = {
                fechaVencimiento = it
                mensajeError = ""
            },
            label = {
                Text("Fecha de vencimiento")
            },
            placeholder = {
                Text("DD-MM-AAAA")
            },
            singleLine = true,
            supportingText = {
                Text("La fecha debe ser hoy o una fecha futura.")
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {

                val montoNumero = monto.trim().toIntOrNull()

                when {

                    concepto.trim().length < 3 -> {
                        mensajeError =
                            "Ingresa un concepto de al menos 3 caracteres."
                    }

                    montoNumero == null || montoNumero <= 0 -> {
                        mensajeError =
                            "Ingresa un monto válido mayor a cero."
                    }

                    fechaVencimiento.isBlank() -> {
                        mensajeError =
                            "Ingresa la fecha de vencimiento."
                    }

                    else -> {

                        val registrado = onRegistrar(
                            concepto.trim(),
                            montoNumero,
                            fechaVencimiento.trim()
                        )

                        if (registrado) {
                            onGuardado()
                        } else {
                            mensajeError =
                                "No se pudo registrar. Revisa los datos y la fecha."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar compromiso")
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
