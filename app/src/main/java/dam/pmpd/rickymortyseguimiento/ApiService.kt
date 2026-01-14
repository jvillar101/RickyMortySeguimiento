package dam.pmpd.rickymortyseguimiento

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz que define los endpoints para la comunicación con la API pública de Rick y Morty.
 *
 * Utiliza la librería **Retrofit** para mapear las llamadas HTTP a métodos de Kotlin.
 * Esta interfaz actúa se utiliza para realizar las peticiones de red necesarias
 * para obtener episodios y personajes.
 *
 * @see [Rick and Morty API Documentation](https://rickandmortyapi.com/documentation)
 */
interface ApiService {

    /**
     * Realiza una petición HTTP GET al endpoint "episode".
     *
     * Este método recupera la lista paginada de episodios disponibles en la API.
     * Por defecto, la API devuelve la primera página de resultados.
     *
     * @return Un objeto [Call] que envuelve la respuesta [EpisodeResponse], la cual contiene
     * la lista de episodios.
     */
    @GET("episode")
    fun getEpisodes(): Call<EpisodeResponse>

    /**
     * Realiza una petición HTTP GET al endpoint "character/{ids}".
     *
     * Este método recupera la información detallada de uno o varios personajes específicos
     * basándose en sus identificadores únicos.
     *
     * @param ids Cadena de texto que contiene los IDs de los personajes deseados.
     * Si se solicitan varios, deben ir separados por comas (ej: "1,2,3").
     * Este valor se inyecta dinámicamente en la URL mediante la anotación [@Path].
     * @return Un objeto [Call] que envuelve una lista de objetos [Character] con los datos
     * de los personajes solicitados.
     */
    @GET("character/{ids}")
    fun getMultipleCharacters(@Path("ids") ids: String): Call<List<Character>>
}