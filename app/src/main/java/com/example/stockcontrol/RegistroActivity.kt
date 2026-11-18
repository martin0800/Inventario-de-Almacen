package com.example.stockcontrol

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        database = DatabaseHelper(this)

        val nombre = findViewById<EditText>(R.id.etNombre)
        val correo = findViewById<EditText>(R.id.etCorreoRegistro)
        val usuario = findViewById<EditText>(R.id.etUsuario)
        val password = findViewById<EditText>(R.id.etPasswordRegistro)
        val confirmar = findViewById<EditText>(R.id.etConfirmarPassword)

        val terminos = findViewById<CheckBox>(R.id.cbTerminos)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Regresar al inicio de sesión
        btnVolver.setOnClickListener {
            finish()
        }

        // Registrar usuario
        btnRegistrar.setOnClickListener {

            val nombreTexto = nombre.text.toString().trim()
            val correoTexto = correo.text.toString().trim()
            val usuarioTexto = usuario.text.toString().trim()
            val passwordTexto = password.text.toString()
            val confirmarTexto = confirmar.text.toString()

            when {

                nombreTexto.isEmpty() ||
                        correoTexto.isEmpty() ||
                        usuarioTexto.isEmpty() ||
                        passwordTexto.isEmpty() ||
                        confirmarTexto.isEmpty() -> {

                    mensaje("Completa todos los campos")
                }

                !Patterns.EMAIL_ADDRESS.matcher(correoTexto).matches() -> {

                    mensaje("Ingresa un correo válido")
                }

                passwordTexto.length < 8 -> {

                    mensaje("La contraseña debe tener al menos 8 caracteres")
                }

                passwordTexto != confirmarTexto -> {

                    mensaje("Las contraseñas no coinciden")
                }

                !terminos.isChecked -> {

                    mensaje("Debes aceptar los términos y condiciones")
                }

                else -> {

                    val registrado = database.registrarUsuario(
                        nombreTexto,
                        correoTexto,
                        usuarioTexto,
                        passwordTexto
                    )

                    if (registrado) {

                        Toast.makeText(
                            this,
                            "¡Cuenta creada correctamente!",
                            Toast.LENGTH_LONG
                        ).show()

                        // Volver al inicio de sesión
                        val intent = Intent(this, LoginActivity::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP

                        startActivity(intent)
                        finish()

                    } else {

                        mensaje("El correo o usuario ya está registrado")
                    }
                }
            }
        }
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (::database.isInitialized) {
            database.close()
        }

        super.onDestroy()
    }
}