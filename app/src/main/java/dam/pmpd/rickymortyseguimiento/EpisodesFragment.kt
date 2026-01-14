package dam.pmpd.rickymortyseguimiento

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragmento principal que muestra el listado completo de episodios.
 *
 * Esta clase actúa como el "Controlador" principal de la vista de lista. Sus responsabilidades son:
 * 1. **Gestión de UI:** Configura el RecyclerView en modo cuadrícula (Grid) y el Spinner de filtrado.
 * 2. **Sincronización de Datos:** Coordina la obtención de datos de dos fuentes:
 * - **API (Retrofit):** Para obtener la información de los episodios (nombre, fecha, código).
 * - **Base de Datos (Firestore):** Para saber qué episodios ha marcado el usuario como "Vistos".
 * 3. **Navegación:** Gestiona el click en un elemento para navegar al [DetailFragment].
 * 4. **Edición Masiva:** Gestiona el modo selección para guardar múltiples episodios como vistos a la vez.
 *
 * @see EpisodesAdapter
 * @see FirestoreManager
 */
class EpisodesFragment : Fragment(R.layout.fragment_episodes) {

    private lateinit var adapter: EpisodesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var fabSave: FloatingActionButton

    /** Lista maestra que contiene todos los episodios descargados de la API. */
    private var allEpisodes = listOf<Episode>()

    /** Lista auxiliar con los IDs de los episodios que el usuario ya ha visto (traída de Firestore). */
    private var seenIds = listOf<String>()

    /**
     * Inicializa la vista, configura el adaptador y los listeners.
     *
     * Define dos callbacks críticos para el Adaptador:
     * - **onClick:** Crea un Bundle con el objeto [Episode] serializado y navega al detalle.
     * - **onSelectionChanged:** Controla la visibilidad del botón flotante (FAB) de guardar.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvEpisodes)
        spinner = view.findViewById(R.id.spinnerFilter)
        fabSave = view.findViewById(R.id.fabSaveSelection)

        recyclerView.layoutManager = GridLayoutManager(context, 2) // LinearLayout

        // Configurar Adaptador
        adapter = EpisodesAdapter(
            episodes = emptyList(), // 1. Lista inicial vacía

            onClick = { episode ->
                // Bundle
                val bundle = Bundle().apply {
                    putSerializable("episode_data", episode)

                }


                findNavController().navigate(R.id.action_episodes_to_detail, bundle)
            },

            onSelectionChanged = { isSelecting -> // 3. Acción Cambio Selección
                // Mostrar botón de guardar si estamos seleccionando
                if (isSelecting) {
                    fabSave.visibility = View.VISIBLE
                } else {
                    fabSave.visibility = View.GONE
                }
            }
        )

        recyclerView.adapter = adapter

        // Configurar Spinner Filtro
        val filterOptions = listOf("Todos", "Vistos")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilter()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Botón Guardado Masivo
        fabSave.setOnClickListener {
            saveMultiSelection()
        }

        loadData()
    }

    /**
     * Gestion de carga de datos.
     *
     * Implementa una estrategia de **carga secuencial**:
     * 1. Primero carga los "Vistos" de Firestore.
     * 2. Una vez obtenidos (o fallado), carga los episodios de la API.
     *
     * Esto es necesario para poder marcar correctamente el check de "Visto" antes de pintar la lista.
     */
    private fun loadData() {
        // 1. Cargar Firestore (Vistos)
        FirestoreManager.getSeenEpisodes(
            onSuccess = { ids ->
                seenIds = ids
                // 2. Cargar API (Todos)
                fetchApiEpisodes()
            },
            onFailure = {
                fetchApiEpisodes() // Cargamos la API aunque falle Firestore
            }
        )
    }

    /**
     * Realiza la petición a la API y realiza el **Cruce de Datos (Data Merging)**.
     *
     * Cuando llega la lista de la API, iteramos sobre ella y comparamos cada ID
     * con la lista `seenIds` obtenida de Firestore. Si hay coincidencia,
     * establecemos `episode.viewed = true`.
     */
    private fun fetchApiEpisodes() {
        RetrofitClient.instance.getEpisodes().enqueue(object : Callback<EpisodeResponse> {
            override fun onResponse(call: Call<EpisodeResponse>, response: Response<EpisodeResponse>) {
                if (response.isSuccessful) {
                    val apiList = response.body()?.results ?: emptyList()

                    // CRUCE DE DATOS: Marcar viewed=true si el ID está en Firestore
                    apiList.forEach { ep ->
                        // La API devuelve ID int, Firestore guarda String. Convertimos para comparar.
                        if (seenIds.contains(ep.id.toString())) {
                            ep.viewed = true
                        }
                    }

                    allEpisodes = apiList
                    applyFilter() // Mostrar lista inicial
                }
            }
            override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) {
                Toast.makeText(context, "Error red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Filtra la lista mostrada en el RecyclerView basándose en la selección del Spinner.
     *
     * - Posición 0: Muestra todos.
     * - Posición 1: Muestra solo los que tienen `viewed == true`.
     */
    private fun applyFilter() {
        val showOnlySeen = spinner.selectedItemPosition == 1 // 1 es "Vistos"

        val listToShow = if (showOnlySeen) {
            allEpisodes.filter { it.viewed }
        } else {
            allEpisodes
        }
        adapter.updateList(listToShow)
    }

    /**
     * Guarda en Firestore todos los episodios seleccionados actualmente en el Adaptador.
     *
     * Itera sobre la lista `selectedEpisodes` y realiza una petición de guardado por cada uno.
     * Al finalizar, limpia la selección y recarga los datos para asegurar la consistencia.
     */
    private fun saveMultiSelection() {
        val selected = adapter.selectedEpisodes
        var savedCount = 0

        selected.forEach { ep ->
            ep.viewed = true // Actualizar localmente
            FirestoreManager.saveEpisode(ep,
                onSuccess = {
                    savedCount++
                    if (savedCount == selected.size) {
                        Toast.makeText(context, "Guardados ${selected.size} episodios", Toast.LENGTH_SHORT).show()
                        adapter.clearSelection()
                        loadData() // Recargar para asegurar consistencia
                    }
                },
                onFailure = {}
            )
        }
    }

    /**
     * Método del ciclo de vida.
     * Recarga los datos al volver a esta pantalla (por ejemplo, al volver del Detalle)
     * para asegurar que si el usuario marcó un episodio como visto en el detalle,
     * la lista lo refleje correctamente.
     */
    override fun onResume() {
        super.onResume()
        if(allEpisodes.isNotEmpty()) loadData()
    }
}