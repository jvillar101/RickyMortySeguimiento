package dam.pmpd.rickymortyseguimiento

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad Principal que actúa como contenedor global de la aplicación.
 *
 * Implementa el patrón de arquitectura **Single Activity** utilizando el componente de navegación
 * de Android Jetpack (Navigation Component). Esta actividad contiene:
 * 1. La **Toolbar** superior compartida.
 * 2. El **DrawerLayout** (Menú lateral deslizante).
 * 3. El **NavHostFragment**, que es el contenedor donde se intercambian los distintos fragmentos (Pantallas).
 *
 * Además, se encarga de personalizar la cabecera del menú lateral con los datos del usuario
 * autenticado en Firebase.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Configuración de la barra de aplicación. Define qué destinos se consideran "de nivel superior"
     * (donde se muestra el icono de hamburguesa en lugar de la flecha de retroceso).
     */
    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Inicializa la actividad, configura la navegación y vincula los componentes UI.
     *
     * Pasos principales:
     * 1. Configura la `Toolbar` como la ActionBar de la actividad.
     * 2. Inicializa el `NavController` recuperándolo del `NavHostFragment`.
     * 3. Define los IDs de los fragmentos principales (`nav_episodes`, `nav_stats`, etc.) para que
     * el botón de "Atrás" no aparezca en ellos, sino el icono del menú lateral.
     * 4. Vincula el menú lateral (`NavigationView`) con el controlador para que la navegación sea automática.
     * 5. **Lógica de Usuario:** Accede a la vista de cabecera (`HeaderView`) del menú para mostrar
     * el nombre y correo del usuario actual obtenido de [FirebaseAuth].
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configurar la Toolbar (Barra superior)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Referencias al diseño (Drawer y NavigationView)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // 3. Configurar el NavHost
        // Usamos supportFragmentManager para encontrar el fragmento contenedor
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 4. Configurar la barra superior con el menú lateral
        // Definimos qué pantallas son "Inicio" (Top Level Destinations)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_episodes, // Lista Episodios
                R.id.nav_stats,    // Estadísticas
                R.id.nav_settings, // Ajustes
                R.id.nav_about     // Acerca de
            ), drawerLayout
        )

        // 5. Unir todo: Toolbar + Controlador de Navegación + Configuración
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 6. Unir el Menú Lateral con el Controlador
        navView.setupWithNavController(navController)

        // --- LÓGICA DE CABECERA DINÁMICA ---
        // Accedemos a la vista inflada dentro del NavigationView (índice 0 es el header)
        val headerView = navView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvNavHeaderName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvNavHeaderEmail)
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Si hay usuario logueado, actualizamos la UI del menú
        currentUser?.let { user ->
            // Ponemos el nombre (o "Usuario" si el campo displayName estuviera vacío)
            tvName.text = user.displayName ?: "Rick Sanchez"
            // Ponemos el email
            tvEmail.text = user.email
        }
    }

    /**
     * Gestiona la navegación "hacia arriba" (Up navigation) en la jerarquía de la app.
     *
     * Este método se llama cuando el usuario pulsa el icono de navegación en la Toolbar
     * (ya sea la flecha de atrás o el icono de hamburguesa). Delega la acción al
     * `NavController` utilizando la configuración definida en `appBarConfiguration`.
     *
     * @return `true` si la navegación fue manejada por el controlador o el Drawer, `false` en caso contrario.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}