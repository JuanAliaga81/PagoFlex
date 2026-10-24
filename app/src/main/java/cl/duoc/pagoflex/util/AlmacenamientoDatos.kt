
package cl.duoc.pagoflex.util

import android.content.Context
import cl.duoc.pagoflex.model.Compromiso
import cl.duoc.pagoflex.model.ComprobantePago
import cl.duoc.pagoflex.model.EstadoCompromiso
import cl.duoc.pagoflex.model.EstadoReporte
import cl.duoc.pagoflex.model.ReporteProblema
import org.json.JSONArray
import org.json.JSONObject

data class DatosPagoFlex(
    val compromisos: List<Compromiso>,
    val comprobantes: List<ComprobantePago>,
    val reportes: List<ReporteProblema>,
    val motivosAnulacion: Map<String, String>
)

object AlmacenamientoDatos {

    private const val ARCHIVO = "pagoflex_datos.json"

    fun guardarDatos(
        context: Context,
        compromisos: List<Compromiso>,
        comprobantes: List<ComprobantePago>,
        reportes: List<ReporteProblema>,
        motivosAnulacion: Map<String, String>
    ): Boolean {

        return try {

            val datos = JSONObject()

            val listaCompromisos = JSONArray()

            compromisos.forEach { compromiso ->

                val objeto = JSONObject()

                objeto.put("folio", compromiso.folio)
                objeto.put("empresa", compromiso.empresa)
                objeto.put("concepto", compromiso.concepto)
                objeto.put("monto", compromiso.monto)
                objeto.put(
                    "fechaVencimiento",
                    compromiso.fechaVencimiento
                )
                objeto.put("estado", compromiso.estado.name)
                objeto.put("recargo", compromiso.recargo)

                listaCompromisos.put(objeto)
            }

            datos.put("compromisos", listaCompromisos)

            val listaComprobantes = JSONArray()

            comprobantes.forEach { comprobante ->

                val objeto = JSONObject()

                objeto.put("codigo", comprobante.codigo)
                objeto.put(
                    "folioCompromiso",
                    comprobante.folioCompromiso
                )
                objeto.put("empresa", comprobante.empresa)
                objeto.put("concepto", comprobante.concepto)
                objeto.put("montoTotal", comprobante.montoTotal)
                objeto.put("fechaPago", comprobante.fechaPago)

                listaComprobantes.put(objeto)
            }

            datos.put("comprobantes", listaComprobantes)

            val listaReportes = JSONArray()

            reportes.forEach { reporte ->

                val objeto = JSONObject()

                objeto.put("codigo", reporte.codigo)
                objeto.put(
                    "folioCompromiso",
                    reporte.folioCompromiso
                )
                objeto.put("motivo", reporte.motivo)
                objeto.put("descripcion", reporte.descripcion)
                objeto.put(
                    "estadoAnterior",
                    reporte.estadoAnterior.name
                )
                objeto.put("estado", reporte.estado.name)
                objeto.put("respuesta", reporte.respuesta)
                objeto.put(
                    "archivoEvidencia",
                    reporte.archivoEvidencia ?: JSONObject.NULL
                )

                listaReportes.put(objeto)
            }

            datos.put("reportes", listaReportes)

            val anulaciones = JSONObject()

            motivosAnulacion.forEach { (folio, motivo) ->
                anulaciones.put(folio, motivo)
            }

            datos.put("motivosAnulacion", anulaciones)

            context.openFileOutput(
                ARCHIVO,
                Context.MODE_PRIVATE
            ).use { archivo ->

                archivo.write(
                    datos.toString().toByteArray(Charsets.UTF_8)
                )
            }

            true

        } catch (e: Exception) {
            false
        }
    }

    fun cargarDatos(context: Context): DatosPagoFlex? {

        if (!context.fileList().contains(ARCHIVO)) {
            return null
        }

        return try {

            val contenido = context.openFileInput(ARCHIVO)
                .bufferedReader()
                .use { it.readText() }

            val datos = JSONObject(contenido)

            val listaCompromisos = datos.getJSONArray("compromisos")

            val compromisos = List(listaCompromisos.length()) { i ->

                val objeto = listaCompromisos.getJSONObject(i)

                Compromiso(
                    folio = objeto.getString("folio"),
                    empresa = objeto.getString("empresa"),
                    concepto = objeto.getString("concepto"),
                    monto = objeto.getInt("monto"),
                    fechaVencimiento = objeto.getString(
                        "fechaVencimiento"
                    ),
                    estado = EstadoCompromiso.valueOf(
                        objeto.getString("estado")
                    ),
                    recargo = objeto.getInt("recargo")
                )
            }

            val listaComprobantes = datos.getJSONArray("comprobantes")

            val comprobantes = List(listaComprobantes.length()) { i ->

                val objeto = listaComprobantes.getJSONObject(i)

                ComprobantePago(
                    codigo = objeto.getString("codigo"),
                    folioCompromiso = objeto.getString(
                        "folioCompromiso"
                    ),
                    empresa = objeto.getString("empresa"),
                    concepto = objeto.getString("concepto"),
                    montoTotal = objeto.getInt("montoTotal"),
                    fechaPago = objeto.getString("fechaPago")
                )
            }

            val listaReportes = datos.getJSONArray("reportes")

            val reportes = List(listaReportes.length()) { i ->

                val objeto = listaReportes.getJSONObject(i)

                val evidencia = if (
                    objeto.isNull("archivoEvidencia")
                ) {
                    null
                } else {
                    objeto.getString("archivoEvidencia")
                }

                ReporteProblema(
                    codigo = objeto.getString("codigo"),
                    folioCompromiso = objeto.getString(
                        "folioCompromiso"
                    ),
                    motivo = objeto.getString("motivo"),
                    descripcion = objeto.getString("descripcion"),
                    estadoAnterior = EstadoCompromiso.valueOf(
                        objeto.getString("estadoAnterior")
                    ),
                    estado = EstadoReporte.valueOf(
                        objeto.getString("estado")
                    ),
                    respuesta = objeto.getString("respuesta"),
                    archivoEvidencia = evidencia
                )
            }

            val objetoAnulaciones = datos.getJSONObject(
                "motivosAnulacion"
            )

            val motivosAnulacion = mutableMapOf<String, String>()

            val claves = objetoAnulaciones.keys()

            while (claves.hasNext()) {

                val folio = claves.next()

                motivosAnulacion[folio] =
                    objetoAnulaciones.getString(folio)
            }

            DatosPagoFlex(
                compromisos = compromisos,
                comprobantes = comprobantes,
                reportes = reportes,
                motivosAnulacion = motivosAnulacion
            )

        } catch (e: Exception) {
            null
        }
    }
}
