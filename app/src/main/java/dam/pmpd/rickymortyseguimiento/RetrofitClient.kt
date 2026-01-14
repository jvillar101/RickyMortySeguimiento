package dam.pmpd.rickymortyseguimiento

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente de red Singleton encargado de la configuración y creación de la instancia de Retrofit.
 *
 * Este objeto asegura que solo exista una única conexión con la API durante el ciclo de vida
 * de la aplicación, optimizando recursos.
 *
 * Utiliza el patrón de inicialización perezosa (`by lazy`) para que el cliente HTTP
 * solo se construya en el momento exacto en que se necesita por primera vez, no antes.
 */
object RetrofitClient {

    /** URL raíz de la API pública de Rick y Morty. */
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    /**
     * Instancia única e inmutable de la interfaz [ApiService].
     *
     * Al acceder a esta variable:
     * 1. Se crea el constructor de Retrofit con la URL base.
     * 2. Se añade **GsonConverterFactory** para transformar automáticamente el JSON de la respuesta en Data Classes de Kotlin.
     * 3. Se genera la implementación del servicio lista para ser usada.
     */
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Traductor Gson: JSON -> Objetos
            .build()

        retrofit.create(ApiService::class.java)
    }
}