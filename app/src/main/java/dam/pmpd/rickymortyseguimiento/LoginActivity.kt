package dam.pmpd.rickymortyseguimiento

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad que gestiona el proceso de autenticación de usuarios (Login).
 *
 * Esta clase sirve como puerta de entrada a la aplicación. Utiliza **Firebase Authentication**
 * para verificar las credenciales (correo y contraseña).
 *
 * Sus responsabilidades principales son:
 * 1. **Autenticación:** Validar credenciales contra el servidor de Firebase.
 * 2. **Persistencia:** Detectar si ya existe una sesión activa para saltar este paso (Auto-login).
 * 3. **Navegación:** Redirigir al usuario al Registro ([RegisterActivity]) o a la App Principal ([MainActivity]).
 *
 * @see RegisterActivity
 * @see MainActivity
 */
class LoginActivity : AppCompatActivity() {

    /** Instancia del servicio de autenticación de Firebase. */
    private lateinit var auth: FirebaseAuth

    /**
     * Inicializa la interfaz de usuario y define la lógica de los botones.
     *
     * Aquí se configura el listener del botón "Entrar", que realiza los siguientes pasos:
     * 1. Valida que los campos de texto no estén vacíos.
     * 2. Llama al método asíncrono [FirebaseAuth.signInWithEmailAndPassword].
     * 3. Gestiona la respuesta (Éxito -> ir a Main / Error -> mostrar Toast).
     *
     * @param savedInstanceState Estado guardado de la actividad (si existe).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializamos Firebase
        auth = FirebaseAuth.getInstance()

        // Referencias
        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etPassword = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLoginAction)
        val btnGoRegister = findViewById<Button>(R.id.btnGoToRegister)

        // Botón Entrar
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // LLAMADA A FIREBASE PARA ENTRAR
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login correcto
                            Toast.makeText(this, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()

                            // Vamos a la app principal
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Login fallido
                            Toast.makeText(this, "Error de acceso: Comprueba tus datos", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para ir a registrarse
        btnGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Método del ciclo de vida que se ejecuta antes de mostrar la actividad.
     *
     * **Función de Auto-Login:**
     * Verifica si existe un usuario actual (`currentUser`) en la caché de Firebase.
     * Si el usuario ya inició sesión anteriormente y no cerró sesión, se omite
     * la pantalla de Login y se navega directamente a [MainActivity].
     */
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            // Si ya hay usuario, saltamos directo al Main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}