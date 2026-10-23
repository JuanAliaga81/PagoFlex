
package cl.duoc.pagoflex

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider

import cl.duoc.pagoflex.ui.screens.SeleccionRolScreen
import cl.duoc.pagoflex.ui.screens.usuario.InicioUsuarioScreen
import cl.duoc.pagoflex.ui.screens.usuario.DetalleCompromisoScreen
import cl.duoc.pagoflex.ui.screens.usuario.HistorialPagosScreen
import cl.duoc.pagoflex.ui.screens.usuario.ReportarProblemaScreen
import cl.duoc.pagoflex.ui.screens.agente.InicioAgenteScreen
import cl.duoc.pagoflex.ui.theme.PagoFlexTheme
import cl.duoc.pagoflex.viewmodel.InicioUsuarioViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iconos oscuros en las barras del sistema
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        // ViewModel compartido durante esta etapa
        val inicioUsuarioViewModel =
            ViewModelProvider(this)[InicioUsuarioViewModel::class.java]

        setContent {
            PagoFlexTheme {

                // Rol seleccionado
                var rolSeleccionado by rememberSaveable {
                    mutableStateOf("")
                }

                // Compromiso seleccionado
                var folioSeleccionado by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                // Navegación del usuario final
                var mostrarHistorial by rememberSaveable {
                    mutableStateOf(false)
                }

                var mostrarReporte by rememberSaveable {
                    mutableStateOf(false)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->

                    if (rolSeleccionado.isEmpty()) {

                        // Selección de roles
                        SeleccionRolScreen(
                            modifier = Modifier.padding(padding),
                            onSeleccionarRol = { rol ->
                                rolSeleccionado = rol
                            }
                        )

                    } else if (
                        rolSeleccionado ==
                        stringResource(R.string.rol_usuario)
                    ) {

                        // Buscar compromiso seleccionado
                        val compromisoSeleccionado =
                            folioSeleccionado?.let { folio ->
                                inicioUsuarioViewModel.obtenerCompromiso(folio)
                            }

                        if (
                            mostrarReporte &&
                            compromisoSeleccionado != null
                        ) {

                            // Formulario de reporte
                            ReportarProblemaScreen(
                                compromiso = compromisoSeleccionado,
                                modifier = Modifier.padding(padding),
                                onVolver = {
                                    mostrarReporte = false
                                },
                                onEnviarReporte = { motivo, descripcion ->
                                    inicioUsuarioViewModel.registrarReporte(
                                        compromisoSeleccionado.folio,
                                        motivo,
                                        descripcion
                                    )
                                }
                            )

                        } else if (
                            compromisoSeleccionado != null
                        ) {

                            // Detalle del compromiso
                            DetalleCompromisoScreen(
                                compromiso = compromisoSeleccionado,
                                motivoBloqueo =
                                    inicioUsuarioViewModel.validarPago(
                                        compromisoSeleccionado.folio
                                    ),
                                modifier = Modifier.padding(padding),
                                onVolver = {
                                    folioSeleccionado = null
                                    mostrarReporte = false
                                },
                                onConfirmarPago = { folio ->
                                    inicioUsuarioViewModel.simularPago(folio)
                                },
                                onReportarProblema = {
                                    mostrarReporte = true
                                }
                            )

                        } else if (mostrarHistorial) {

                            // Historial de pagos
                            HistorialPagosScreen(
                                comprobantes =
                                    inicioUsuarioViewModel.comprobantes,
                                modifier = Modifier.padding(padding),
                                onVolver = {
                                    mostrarHistorial = false
                                }
                            )

                        } else {

                            // Inicio del usuario final
                            InicioUsuarioScreen(
                                viewModel = inicioUsuarioViewModel,
                                modifier = Modifier.padding(padding),
                                onVolver = {
                                    folioSeleccionado = null
                                    mostrarHistorial = false
                                    mostrarReporte = false
                                    rolSeleccionado = ""
                                },
                                onSeleccionarCompromiso = { folio ->
                                    folioSeleccionado = folio
                                    mostrarHistorial = false
                                },
                                onVerHistorial = {
                                    folioSeleccionado = null
                                    mostrarHistorial = true
                                }
                            )
                        }

                    } else if (
                        rolSeleccionado ==
                        stringResource(R.string.rol_agente)
                    ) {

                        // Panel del Agente PagoFlex

                        InicioAgenteScreen(
                            reportes = inicioUsuarioViewModel.reportes,
                            modifier = Modifier.padding(padding),
                            onVolver = {
                                rolSeleccionado = ""
                            },
                            onIniciarGestion = { codigo ->
                                inicioUsuarioViewModel.iniciarGestionReporte(codigo)
                            },
                            onResponderReporte = { codigo, respuesta ->
                                inicioUsuarioViewModel.responderReporte(
                                    codigo,
                                    respuesta
                                )
                            },
                            onResolverReporte = { codigo ->
                                inicioUsuarioViewModel.resolverReporte(codigo)
                            }
                        )


                    } else {

                        // Pantalla temporal del Ejecutivo
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

// Pantalla temporal para el Ejecutivo
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
            text = stringResource(
                R.string.ingreso_rol,
                rol
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedButton(onClick = onVolver) {
            Text(stringResource(R.string.volver))
        }
    }
}
