package dam.pmpd.rickymortyseguimiento

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Actividad encargada del registro de nuevos usuarios en la plataforma.
 *
 * Utiliza **Firebase Authentication** para crear la cuenta. El proceso de registro
 * implementa un flujo secuencial de dos pasos críticos para la UX de la app:
 * 1. **Creación de Credenciales:** Se crea el usuario con correo y contraseña.
 * 2. **Actualización de Perfil:** Inmediatamente después, se asigna el "Nombre" introducido
 * al perfil del usuario (`displayName`), ya que Firebase no permite establecerlo
 * en el primer paso de creación.
 *
 * @see LoginActivity
 */
class RegisterActivity : AppCompatActivity() {

    /** Instancia del servicio de autenticación de Firebase. */
    private lateinit var auth: FirebaseAuth

    /**
     * Inicializa la actividad y configura la lógica de registro.
     *
     * El listener del botón de registro realiza las siguientes operaciones anidadas:
     * - **Validación:** Comprueba que Nombre, Email y Contraseña no estén vacíos.
     * - **createUserWithEmailAndPassword:** Crea la cuenta en el servidor.
     * - **updateProfile:** Si la cuenta se crea con éxito, se construye un [UserProfileChangeRequest]
     * para guardar el nombre del usuario.
     * - **Navegación:** Solo si ambos pasos tienen éxito, se redirige a [MainActivity].
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Inicializamos Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 2. Referencias a los controles de la pantalla
        val etName = findViewById<EditText>(R.id.etNameRegister)
        val etEmail = findViewById<EditText>(R.id.etEmailRegister)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegisterAction)
        val btnBack = findViewById<Button>(R.id.btnBackToLogin)

        // 3. Acción del botón Registrar
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Validamos que no estén vacíos
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                // PASO 1: Crear el usuario en Firebase
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // PASO 2: Guardar el nombre del usuario (DisplayName)
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            // Actualizamos el perfil y, si va bien, entramos a la app
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener {

                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() // Cerramos registro para que "Atrás" no vuelva aquí
                                }
                        } else {
                            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Botón para volver al Login si ya tienes cuenta
        btnBack.setOnClickListener {
            finish()
        }
    }
}