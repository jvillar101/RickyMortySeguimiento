package dam.pmpd.rickymortyseguimiento

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa a un personaje del universo de Rick y Morty.
 *
 * Esta clase actúa como un DTO (Data Transfer Object) para mapear la respuesta JSON
 * de la API. Se utiliza en el detalle del episodio para mostrar
 * la lista de personajes que aparecen en él.
 *
 * @property id Identificador único del personaje en la base de datos de la API.
 * @property name Nombre del personaje. Este campo se muestra en el RecyclerView.
 * @property image URL absoluta de la imagen del personaje. Se utiliza con librerías de carga de imágenes, Glide, para visualizar al personaje.
 */
data class Character(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String
)