
package cl.duoc.pagoflex

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider

import cl.duoc.pagoflex.ui.screens.SeleccionRolScreen
import cl.duoc.pagoflex.ui.screens.InicioEjecutivoScreen
import cl.duoc.pagoflex.ui.screens.RegistrarCompromisoScreen
import cl.duoc.pagoflex.ui.screens.agente.InicioAgenteScreen
import cl.duoc.pagoflex.ui.screens.usuario.InicioUsuarioScreen
import cl.duoc.pagoflex.ui.screens.usuario.DetalleCompromisoScreen
import cl.duoc.pagoflex.ui.screens.usuario.HistorialPagosScreen
import cl.duoc.pagoflex.ui.screens.usuario.ReportarProblemaScreen
import cl.duoc.pagoflex.ui.theme.PagoFlexTheme
import cl.duoc.pagoflex.viewmodel.InicioUsuarioViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val inicioUsuarioViewModel = ViewModelProvider(this)[
            InicioUsuarioViewModel::class.java
        ]

        setContent {

            PagoFlexTheme {

                // Variables para controlar la navegación
                var rolSeleccionado by rememberSaveable {
                    mutableStateOf("")
                }

                var folioSeleccionado by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var mostrarHistorial by rememberSaveable {
                    mutableStateOf(false)
                }

                var mostrarReporte by rememberSaveable {
                    mutableStateOf(false)
                }

                // Navegación del Ejecutivo
                var mostrarRegistroCompromiso by rememberSaveable {
                    mutableStateOf(false)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->

                    // Animación al seleccionar o cambiar de rol
                    Crossfade(
                        targetState = rolSeleccionado,
                        animationSpec = tween(
                            durationMillis = 300
                        ),
                        label = "transicionRoles"
                    ) { rolActual ->

                        // PANTALLA DE SELECCIÓN DE ROL
                        if (rolActual.isEmpty()) {

                            SeleccionRolScreen(
                                modifier = Modifier.padding(padding),
                                onSeleccionarRol = { rol ->
                                    rolSeleccionado = rol
                                }
                            )

                            // R-01: USUARIO FINAL
                        } else if (
                            rolActual == stringResource(R.string.rol_usuario)
                        ) {

                            val compromisoSeleccionado =
                                folioSeleccionado?.let { folio ->
                                    inicioUsuarioViewModel.obtenerCompromiso(folio)
                                }

                            // Formulario para reportar problema
                            if (
                                mostrarReporte &&
                                compromisoSeleccionado != null
                            ) {

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

                                // Detalle del compromiso
                            } else if (compromisoSeleccionado != null) {

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

                                // Historial de pagos
                            } else if (mostrarHistorial) {

                                HistorialPagosScreen(
                                    comprobantes =
                                        inicioUsuarioViewModel.comprobantes,

                                    modifier = Modifier.padding(padding),

                                    onVolver = {
                                        mostrarHistorial = false
                                    }
                                )

                                // Panel principal del Usuario
                            } else {

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

                            // R-02: AGENTE PAGOFLEX
                        } else if (
                            rolActual == stringResource(R.string.rol_agente)
                        ) {

                            InicioAgenteScreen(
                                reportes = inicioUsuarioViewModel.reportes,
                                modifier = Modifier.padding(padding),

                                onVolver = {
                                    rolSeleccionado = ""
                                },

                                onIniciarGestion = { codigo ->
                                    inicioUsuarioViewModel.iniciarGestionReporte(
                                        codigo
                                    )
                                },

                                onResponderReporte = { codigo, respuesta ->
                                    inicioUsuarioViewModel.responderReporte(
                                        codigo,
                                        respuesta
                                    )
                                },

                                onResolverReporte = { codigo ->
                                    inicioUsuarioViewModel.resolverReporte(
                                        codigo
                                    )
                                }
                            )

                            // R-03: EJECUTIVO DE EMPRESA
                        } else {

                            // Formulario de nuevo compromiso
                            if (mostrarRegistroCompromiso) {

                                RegistrarCompromisoScreen(
                                    empresa = "Crédito Andino",
                                    modifier = Modifier.padding(padding),

                                    onVolver = {
                                        mostrarRegistroCompromiso = false
                                    },

                                    onRegistrar = { concepto, monto, fecha ->
                                        inicioUsuarioViewModel.registrarCompromiso(
                                            empresa = "Crédito Andino",
                                            concepto = concepto,
                                            monto = monto,
                                            fechaVencimiento = fecha
                                        )
                                    },

                                    onGuardado = {
                                        mostrarRegistroCompromiso = false
                                    }
                                )

                                // Panel principal del Ejecutivo
                            } else {

                                InicioEjecutivoScreen(
                                    compromisos = inicioUsuarioViewModel.compromisos,
                                    empresa = "Crédito Andino",
                                    motivosAnulacion = inicioUsuarioViewModel.motivosAnulacion,
                                    modifier = Modifier.padding(padding),

                                    onVolver = {
                                        mostrarRegistroCompromiso = false
                                        rolSeleccionado = ""
                                    },

                                    onNuevoCompromiso = {
                                        mostrarRegistroCompromiso = true
                                    },

                                    onAnularCompromiso = { folio, motivo ->
                                        inicioUsuarioViewModel.anularCompromiso(
                                            folio = folio,
                                            motivo = motivo
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Pantalla temporal para futuras funcionalidades
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
            text = stringResource(R.string.ingreso_rol, rol),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedButton(
            onClick = onVolver
        ) {
            Text(
                text = stringResource(R.string.volver)
            )
        }
    }
}
