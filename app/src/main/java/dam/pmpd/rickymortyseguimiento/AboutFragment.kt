package dam.pmpd.rickymortyseguimiento

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Fragmento de diálogo que muestra la información "Acerca de" de la aplicación.
 *
 * Esta clase hereda de [DialogFragment] para presentarse como una ventana flotante
 * sobre la interfaz de usuario actual. Su propósito es informar al usuario
 * sobre los créditos, la versión de la app o información del desarrollador.
 *
 * @author JA Villalar
 * @version 1.0
 * @see DialogFragment
 */
class AboutFragment : DialogFragment() {

    /**
     * Construye y configura el diálogo que se mostrará en pantalla.
     *
     * Este método utiliza [AlertDialog.Builder] los siguientes elementos:
     * - **Título:** Definido en `R.string.about_dialog_title`.
     * - **Mensaje:** Definido en `R.string.about_dialog_msg`.
     * - **Icono:** Usa el icono de la aplicación (`ic_launcher_round`).
     * - **Botón Positivo:** Un botón con el texto definido en `R.string.btn_cool`
     * que cierra el diálogo al ser pulsado.
     *
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado
     * guardado anterior, este es el estado. De lo contrario, es nulo.
     * @return Una instancia de [Dialog] (específicamente un [AlertDialog]) lista para ser mostrada.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.about_dialog_title)
            .setMessage(R.string.about_dialog_msg)
            .setIcon(R.mipmap.ic_launcher_round)
            .setPositiveButton(R.string.btn_cool) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}