package dam.pmpd.rickymortyseguimiento

import com.google.gson.annotations.SerializedName

/**
 * Clase contenedora (Wrapper) que representa la estructura raíz de la respuesta JSON de la API.
 *
 * La API de Rick y Morty no devuelve una lista directa de episodios (`[...]`), sino un objeto JSON
 * paginado que contiene metadatos (clave "info") y el array de datos real (clave "results").
 *
 * Esta clase sirve para mapear esa estructura y extraer únicamente la lista de episodios, ignorando
 * de momento la información de paginación.
 *
 *
 * @property results Lista de objetos [Episode] obtenida del campo JSON "results".
 */
data class EpisodeResponse(
    @SerializedName("results") val results: List<Episode>
)