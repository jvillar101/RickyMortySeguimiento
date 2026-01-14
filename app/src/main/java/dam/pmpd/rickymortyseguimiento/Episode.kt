package dam.pmpd.rickymortyseguimiento

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Modelo de datos central que representa un Episodio de la serie.
 *
 * Esta clase cumple una **doble función** en la arquitectura de la aplicación:
 * 1. **Respuesta de API:** Mapea automáticamente el JSON recibido de la API de Rick y Morty gracias a las anotaciones [@SerializedName].
 * 2. **Persistencia:** Se utiliza para guardar el episodio en **Firestore** cuando el usuario lo marca como visto.
 *
 * Implementa [Serializable] para permitir pasar objetos completos de este tipo entre Fragmentos
 * a través de un `Bundle`.
 *
 * @property id Identificador único del episodio (proviene de la API).
 * @property name Título del episodio (ej: "Pilot").
 * @property episode Código de temporada y episodio (ej: "S01E01").
 * @property air_date Fecha de emisión original en formato texto (ej: "December 2, 2013").
 * @property characters Lista de URLs (Strings) que apuntan a los datos de los personajes que aparecen en el episodio.
 * @property viewed Campo local (no viene de la API). Indica si el usuario ha marcado el episodio como visto/favorito.
 * Por defecto es `false`.
 */
data class Episode(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("episode") val episode: String = "",
    @SerializedName("air_date") val air_date: String = "",
    @SerializedName("characters") val characters: List<String> = emptyList(),
    var viewed: Boolean = false
): Serializable