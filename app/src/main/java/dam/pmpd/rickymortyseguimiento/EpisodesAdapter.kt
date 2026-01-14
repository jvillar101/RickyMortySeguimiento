package dam.pmpd.rickymortyseguimiento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adaptador principal para el RecyclerView que muestra el listado de episodios.
 *
 * Esta clase gestiona una lógica de interacción con dos modos de funcionamiento:
 * 1. **Modo Navegación (Default):** Un click corto abre el detalle del episodio.
 * 2. **Modo Selección Múltiple:** Se activa con un click largo. Permite seleccionar varios episodios
 * para marcarlos como "Vistos/No Vistos" en lote.
 *
 * Además, implementa un cambio visual dinámico (Inversión de colores) para resaltar
 * los elementos seleccionados.
 *
 * @property episodes Lista mutable de episodios que se mostrarán.
 * @property onClick Se ejecuta al hacer click en modo normal (Navegar a detalle).
 * @property onSelectionChanged Se comunica con el Fragmento para indicar si el modo selección está activo.
 */
class EpisodesAdapter(
    private var episodes: List<Episode>,
    private val onClick: (Episode) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    /** Almacena temporalmente los objetos [Episode] que el usuario ha marcado en el modo selección. */
    val selectedEpisodes = mutableListOf<Episode>()

    /** Bandera que indica si la interfaz está actualmente en modo de selección múltiple. */
    var isMultiSelectMode = false

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada tarjeta de episodio.
     */
    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardEpisode)
        val tvCode: TextView = view.findViewById(R.id.tvEpisodeCode)
        val tvName: TextView = view.findViewById(R.id.tvEpisodeName)
        val tvDate: TextView = view.findViewById(R.id.tvEpisodeDate)
        val ivSeen: ImageView = view.findViewById(R.id.ivSeenCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    /**
     * Vincula los datos del episodio con la vista y gestiona la lógica de colores y clicks.
     *
     * **Lógica de Clicks:**
     * - **Click Largo:** Activa/Desactiva selección del ítem actual.
     * - **Click Corto:**
     * - Si `isMultiSelectMode` es true -> Actúa como selector.
     * - Si `isMultiSelectMode` es false -> Navega al detalle.
     */
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        val context = holder.itemView.context

        holder.tvCode.text = episode.episode
        holder.tvName.text = episode.name
        holder.tvDate.text = episode.air_date

        // Icono visto: Solo visible si el episodio ya estaba marcado como visto en la BBDD
        holder.ivSeen.visibility = if (episode.viewed) View.VISIBLE else View.GONE

        // --- GESTIÓN DE COLORES ---
        if (selectedEpisodes.contains(episode)) {
            // ESTADO: SELECCIONADO (Invertido)
            holder.card.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_blue_dark))

            holder.tvCode.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_green_neon))
            holder.tvName.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_green_neon))
            holder.tvDate.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_green_neon))
            holder.ivSeen.setColorFilter(androidx.core.content.ContextCompat.getColor(context, R.color.rick_green_neon))

        } else {
            // ESTADO: NORMAL (Por defecto)
            holder.card.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_green_neon))

            holder.tvCode.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_blue_dark))
            holder.tvName.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_blue_dark))
            holder.tvDate.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.rick_blue_dark))
            holder.ivSeen.setColorFilter(androidx.core.content.ContextCompat.getColor(context, R.color.rick_yellow))
        }



        // Click Largo: Iniciar selección
        holder.itemView.setOnLongClickListener {
            toggleSelection(episode)
            true
        }

        // Click Normal: Depende del modo actual
        holder.itemView.setOnClickListener {
            if (isMultiSelectMode) {
                toggleSelection(episode)
            } else {
                onClick(episode)
            }
        }
    }

    /**
     * Añade o elimina un episodio de la lista de seleccionados y actualiza la UI.
     *
     * Llama a [notifyDataSetChanged] para forzar el repintado de colores.
     * Notifica al Fragmento mediante [onSelectionChanged] si debe mostrar el FAB de guardar.
     */
    private fun toggleSelection(episode: Episode) {
        if (selectedEpisodes.contains(episode)) {
            selectedEpisodes.remove(episode)
        } else {
            selectedEpisodes.add(episode)
        }

        // Activar modo selección si hay al menos uno seleccionado
        isMultiSelectMode = selectedEpisodes.isNotEmpty()
        onSelectionChanged(isMultiSelectMode)
        notifyDataSetChanged() // Refrescar para aplicar los cambios de color
    }

    override fun getItemCount() = episodes.size

    /**
     * Actualiza la lista de datos del adaptador (usado por el filtro o la carga inicial).
     */
    fun updateList(newList: List<Episode>) {
        episodes = newList
        notifyDataSetChanged()
    }

    /**
     * Resetea el estado de selección (limpia la lista temporal y desactiva el modo multi-selección).
     * Se debe llamar después de guardar los cambios en Firestore.
     */
    fun clearSelection() {
        selectedEpisodes.clear()
        isMultiSelectMode = false
        onSelectionChanged(false)
        notifyDataSetChanged()
    }
}