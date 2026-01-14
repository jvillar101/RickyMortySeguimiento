package dam.pmpd.rickymortyseguimiento

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragmento encargado de mostrar el detalle completo de un episodio seleccionado.
 *
 * Sus responsabilidades principales son:
 * 1. Recibir y mostrar los datos básicos del episodio (Nombre, Código, Fecha).
 * 2. Permitir al usuario marcar/desmarcar el episodio como "Visto", persistiendo este estado en **Firestore**.
 * 3. Cargar y mostrar Grid con los personajes que aparecen en dicho episodio obteniendo sus datos de la API.
 *
 * @see Episode
 * @see FirestoreManager
 */
class DetailFragment : Fragment(R.layout.fragment_detail) {

    /** Objeto con los datos del episodio recibido desde la pantalla anterior. */
    private lateinit var episode: Episode

    /** Referencia al RecyclerView donde se pintará la cuadrícula de personajes. */
    private lateinit var rvCharacters: RecyclerView

    /**
     * Método del ciclo de vida donde se inicializa la lógica de la vista.
     *
     * Realiza las siguientes operaciones:
     * - Recupera el objeto [Episode] de los argumentos
     * - Vincula las vistas del layout.
     * - Configura el Switch "Visto" para guardar o borrar el episodio en Firebase.
     * - Inicia la carga asíncrona de los personajes.
     *
     * @param view La vista inflada del fragmento.
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. RECUPERAR DATOS
        arguments?.let { bundle ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                episode = bundle.getSerializable("episode_data", Episode::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                episode = bundle.getSerializable("episode_data") as Episode
            }
        }

        if (!::episode.isInitialized) return


        val tvName = view.findViewById<TextView>(R.id.tvDetailName)
        val tvInfo = view.findViewById<TextView>(R.id.tvDetailInfo)
        val switchSeen = view.findViewById<SwitchMaterial>(R.id.switchSeen)
        rvCharacters = view.findViewById(R.id.rvCharacters) // <--- NUEVO

        // Pintar datos básicos
        tvName.text = episode.name
        tvInfo.text = "${episode.episode} - ${episode.air_date}"
        switchSeen.isChecked = episode.viewed

        // Listener del Switch
        switchSeen.setOnCheckedChangeListener { _, isChecked ->
            episode.viewed = isChecked
            if (isChecked) FirestoreManager.saveEpisode(episode, {}, {})
            else FirestoreManager.removeEpisode(episode.id.toString(), {}, {})
        }

        // ---  CARGAR PERSONAJES ---
        setupCharactersList()
    }

    /**
     * Configura el RecyclerView y descarga los datos de los personajes.
     *
     * **Lógica de extracción:**
     * El objeto [Episode] solo contiene una lista de URLs de personajes (ej: "https.../character/1").
     * Este método:
     * 1. Extrae el ID numérico de cada URL (el número final).
     * 2. Concatena los IDs en un String separado por comas (ej: "1,2,35").
     * 3. Realiza una única llamada a la API (`getMultipleCharacters`) para obtener los nombres e imágenes.
     * 4. Configura el adaptador con la lista de objetos [Character] recibidos.
     */
    private fun setupCharactersList() {
        // Configuramos el RecyclerView como una cuadrícula de 3 columnas
        rvCharacters.layoutManager = GridLayoutManager(context, 3)

        // 1. Extraer los IDs de las URLs

        val ids = episode.characters.map { url ->
            url.substringAfterLast("/")
        }.joinToString(",") // Crea un string tipo "1,2,35,40"

        if (ids.isEmpty()) return

        // 2. Llamar a la API
        RetrofitClient.instance.getMultipleCharacters(ids).enqueue(object : Callback<List<Character>> {
            override fun onResponse(call: Call<List<Character>>, response: Response<List<Character>>) {
                if (response.isSuccessful) {
                    val characterList = response.body() ?: emptyList()
                    // 3. Poner el adaptador
                    rvCharacters.adapter = CharactersAdapter(characterList)
                }
            }

            override fun onFailure(call: Call<List<Character>>, t: Throwable) {
                Toast.makeText(context, "Error cargando personajes", Toast.LENGTH_SHORT).show()
            }
        })
    }
}