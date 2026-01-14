package dam.pmpd.rickymortyseguimiento

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragmento de "Gamificación" que muestra el progreso del usuario en la serie.
 *
 * Su objetivo es motivar al usuario mostrando visualmente (mediante una barra de progreso)
 * qué porcentaje de la serie ha completado.
 *
 * Realiza un cálculo matemático simple:
 * `(Episodios Vistos / Total Episodios API) * 100`
 *
 * @see FirestoreManager
 * @see RetrofitClient
 */
class StatsFragment : Fragment(R.layout.fragment_stats) {

    /**
     * Inicializa la vista y ejecuta la lógica de cálculo de estadísticas.
     *
     * Implementa una **carga de datos secuencial (en cadena)**:
     * 1. **Paso 1 (Firestore):** Obtiene la lista de IDs que el usuario ha marcado como vistos.
     * 2. **Paso 2 (API):** Una vez tiene los vistos, llama a la API para obtener el número total de episodios existentes.
     * 3. **Paso 3 (Cálculo y UI):** Realiza una regla de tres para obtener el porcentaje y actualiza
     * la `ProgressBar` y los textos informativos.
     *
     * @param view La vista inflada del fragmento.
     * @param savedInstanceState Estado guardado previo.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pb = view.findViewById<ProgressBar>(R.id.progressBarStats)
        val tvPercent = view.findViewById<TextView>(R.id.tvPercentage)
        val tvCount = view.findViewById<TextView>(R.id.tvCountStats)

        // cargar datos para calcular
        FirestoreManager.getSeenEpisodes(
            onSuccess = { seenIds ->
                // Una vez tenemos los vistos, pedimos el total
                RetrofitClient.instance.getEpisodes().enqueue(object : Callback<EpisodeResponse> {
                    override fun onResponse(call: Call<EpisodeResponse>, response: Response<EpisodeResponse>) {
                        val total = response.body()?.results?.size ?: 0

                        // Calculamos cuántos de los IDs vistos están en la API actual
                        val vistos = seenIds.size

                        if (total > 0) {
                            // Cálculo de porcentaje (Integer math)
                            val percentage = (vistos * 100) / total

                            // Actualizar UI
                            pb.progress = percentage
                            tvPercent.text = "$percentage%"
                            tvCount.text = "Has visto $vistos de $total episodios"
                        }
                    }
                    override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) {}
                })
            },
            onFailure = {}
        )
    }
}