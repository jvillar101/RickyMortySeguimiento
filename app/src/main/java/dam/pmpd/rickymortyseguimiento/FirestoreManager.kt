package dam.pmpd.rickymortyseguimiento

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Singleton (Objeto único) encargado de toda la interacción con la base de datos **Cloud Firestore**.
 *
 * Implementa el patrón **DAO (Data Access Object)** para centralizar las operaciones de lectura,
 * escritura y borrado. Su función principal es gestionar la lista de favoritos/vistos de cada usuario
 * de forma aislada.
 *
 * **Estructura de la Base de Datos NoSQL:**
 * ```
 * users (Colección Raíz)
 * └── {uid_del_usuario} (Documento)
 * └── seen_episodes (Subcolección)
 * ├── "1" (Documento con datos del Ep 1)
 * ├── "24" (Documento con datos del Ep 24)
 * └── ...
 * ```
 */
object FirestoreManager {

    /** Instancia de la base de datos Firestore. */
    private val db = FirebaseFirestore.getInstance()

    /** Instancia de autenticación para obtener el ID del usuario actual. */
    private val auth = FirebaseAuth.getInstance()

    /**
     * Guarda un episodio en la lista de "Vistos" del usuario actual.
     *
     * Utiliza [SetOptions.merge()] para crear el documento si no existe, o actualizarlo si ya existe,
     * evitando sobrescribir accidentalmente otros campos futuros.
     *
     * @param episode Objeto [Episode] que se va a serializar y guardar.
     * @param onSuccess Lambda que se ejecuta si la operación se completa correctamente.
     * @param onFailure Lambda que recibe la [Exception] si ocurre un error en la red o base de datos.
     */
    fun saveEpisode(episode: Episode, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("seen_episodes")
                .document(episode.id.toString())
                .set(episode, SetOptions.merge())
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }

    /**
     * Elimina un episodio de la lista de "Vistos" del usuario actual.
     *
     * **Nota:** Es fundamental usar la misma ruta (`users -> uid -> seen_episodes`) que en el guardado
     * para que el borrado sea efectivo.
     *
     * @param episodeId El ID del episodio (como String) que se desea eliminar.
     * @param onSuccess Callback de éxito.
     * @param onFailure Callback de error.
     */
    fun removeEpisode(episodeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {

            db.collection("users")
                .document(userId)
                .collection("seen_episodes")
                .document(episodeId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }

    /**
     * Recupera todos los IDs de los episodios que el usuario ha marcado como vistos.
     *
     * Este método es vital para la sincronización. Se llama al iniciar la app para pintar
     * los "ticks" o "checks" en la lista general.
     *
     * @param onSuccess Devuelve una [List]<[String>] con los IDs encontrados (ej: `["1", "5", "10"]`).
     * @param onFailure Callback de error.
     */
    fun getSeenEpisodes(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("seen_episodes")
                .get()
                .addOnSuccessListener { result ->

                    val seenIds = result.map { it.id }
                    onSuccess(seenIds)
                }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }
}