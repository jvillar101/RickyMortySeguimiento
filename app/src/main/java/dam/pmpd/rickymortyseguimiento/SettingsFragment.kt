package dam.pmpd.rickymortyseguimiento

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

/**
 * Fragmento encargado de la configuración de usuario y preferencias de la aplicación.
 *
 * Sus funciones principales son:
 * 1. **Gestión de Tema:** Permite alternar entre Modo Claro (Día) y Modo Oscuro (Noche), persistiendo la elección en el dispositivo.
 * 2. **Configuración Regional:** Control visual para el cambio de idioma (Simulado).
 * 3. **Gestión de Sesión:** Permite al usuario cerrar sesión en Firebase y volver a la pantalla de inicio.
 *
 * Utiliza [SharedPreferences] para guardar la configuración del tema de forma local en el dispositivo.
 */
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    /** Objeto para leer y escribir preferencias simples (clave-valor) en un fichero local xml. */
    private lateinit var prefs: SharedPreferences

    /**
     * Inicializa la lógica de los controles de configuración.
     *
     * **Flujo de Modo Oscuro:**
     * 1. Lee la preferencia "night_mode" guardada anteriormente.
     * 2. Si el switch cambia, guarda el nuevo valor y fuerza el cambio de tema usando [AppCompatDelegate].
     *
     * **Flujo de Logout:**
     * Al pulsar el botón de salir:
     * 1. Se desconecta de Firebase (`signOut`).
     * 2. Se navega al Login limpiando la pila de actividades (`FLAG_ACTIVITY_CLEAR_TASK`) para evitar que el usuario pueda volver atrás.
     *
     * @param view La vista inflada del fragmento.
     * @param savedInstanceState Estado guardado.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar SharedPreferences (Para guardar configuración)
        // "settings_prefs" es el nombre del fichero donde se guardarán los datos
        prefs = requireActivity().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

        // Referencias a los controles del XML
        val switchLanguage = view.findViewById<SwitchMaterial>(R.id.switchLanguage)
        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switchTheme)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // --- 1. LÓGICA DE TEMA (MODO OSCURO) ---
        // Comprobar si ya estaba guardado como oscuro
        val isNightMode = prefs.getBoolean("night_mode", false)
        switchTheme.isChecked = isNightMode

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // 1. Guardar preferencia
            prefs.edit().putBoolean("night_mode", isChecked).apply()

            // 2. Aplicar cambio visualmente
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // --- 2. LÓGICA DE IDIOMA ---
        switchLanguage.setOnCheckedChangeListener { _, isChecked ->

            val msg = if (isChecked) {
                getString(R.string.msg_lang_en)
            } else {
                getString(R.string.msg_lang_es)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        // --- 3. CERRAR SESIÓN  ---
        btnLogout.setOnClickListener {



            // Desconectar de Firebase
            FirebaseAuth.getInstance().signOut()

            // Volver a la pantalla de Login
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            // Limpiar la pila de actividades para que no pueda volver atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}