
package cl.duoc.pagoflex.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.UUID

// Guardar una fotografía en el almacenamiento privado
fun guardarEvidencia(
    context: Context,
    imagen: Bitmap
): String? {

    val nombreArchivo =
        "evidencia_${UUID.randomUUID()}.jpg"

    return try {

        val guardado = context.openFileOutput(
            nombreArchivo,
            Context.MODE_PRIVATE
        ).use { archivo ->

            imagen.compress(
                Bitmap.CompressFormat.JPEG,
                85,
                archivo
            )
        }

        if (guardado) {
            nombreArchivo
        } else {
            context.deleteFile(nombreArchivo)
            null
        }

    } catch (e: Exception) {
        context.deleteFile(nombreArchivo)
        null
    }
}

// Recuperar una fotografía guardada
fun cargarEvidencia(
    context: Context,
    nombreArchivo: String
): Bitmap? {

    // Evitar nombres de archivo no válidos
    if (!nombreArchivo.matches(
            Regex("""evidencia_[a-fA-F0-9-]+\.jpg""")
        )
    ) {
        return null
    }

    return try {

        context.openFileInput(nombreArchivo).use { archivo ->
            BitmapFactory.decodeStream(archivo)
        }

    } catch (e: Exception) {
        null
    }
}
