package dam.pmpd.rickymortyseguimiento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Adaptador para el [RecyclerView] que muestra la lista de personajes en la pantalla de detalle.
 *
 * Esta clase se encarga de vincular los datos de la lista de objetos [Character] con las vistas
 * definidas en el layout `item_character.xml`. Utiliza la librería **Glide** para la carga
 * de las imágenes de los personajes desde sus URLs.
 *
 * @property characters Lista inmutable de personajes recuperados de la API que se mostrarán en la rejilla.
 */
class CharactersAdapter(
    private val characters: List<Character>
) : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {

    /**
     * ViewHolder para el RecyclerView.
     *
     *
     * @param view La vista raíz del elemento inflado (`item_character`).
     */
    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /** Referencia al ImageView donde se cargará la foto del personaje. */
        val ivImage: ImageView = view.findViewById(R.id.ivCharacter)
        /** Referencia al TextView donde se mostrará el nombre del personaje. */
        val tvName: TextView = view.findViewById(R.id.tvCharacterName)
    }

    /**
     * Crea una nueva instancia del ViewHolder inflando el diseño XML correspondiente.
     *
     * @param parent El ViewGroup al que se añadirá la nueva vista.
     * @param viewType El tipo de vista.
     * @return Una nueva instancia de [CharacterViewHolder] conteniendo la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    /**
     * Vincula los datos de un personaje específico con las vistas del ViewHolder.
     *
     * Aquí se asigna el nombre del personaje y se utiliza **Glide** para:
     * 1. Descargar la imagen desde la URL (`character.image`).
     * 2. Gestionar la caché automáticamente.
     * 3. Insertar la imagen en el [ImageView].
     *
     * @param holder El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento dentro de la lista de datos.
     */
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]

        holder.tvName.text = character.name

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(character.image)
            .into(holder.ivImage)
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return El tamaño de la lista de personajes.
     */
    override fun getItemCount() = characters.size
}