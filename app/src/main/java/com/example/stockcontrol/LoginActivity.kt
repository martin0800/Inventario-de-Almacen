package com.example.stockcontrol

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class LoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        // Recuperar y aplicar el tema guardado antes de mostrar la pantalla
        val preferencias = getSharedPreferences(
            "StockControlPreferencias",
            MODE_PRIVATE
        )

        when (preferencias.getString("tema", "sistema")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

            "oscuro" -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            else -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = DatabaseHelper(this)

        val correo = findViewById<EditText>(R.id.etCorreo)
        val contrasena = findViewById<EditText>(R.id.etContrasena)

        val btnIniciar = findViewById<Button>(R.id.btnIniciarSesion)
        val btnCrear = findViewById<Button>(R.id.btnCrearCuenta)

        btnCrear.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnIniciar.setOnClickListener {

            val email = correo.text.toString().trim()
            val password = contrasena.text.toString()

            when {

                email.isEmpty() || password.isEmpty() -> {
                    mensaje("Completa todos los campos")
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    mensaje("Ingresa un correo válido")
                }

                database.verificarUsuario(email, password) -> {

                    Toast.makeText(
                        this,
                        "¡Bienvenido a StockControl!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    mensaje("Correo o contraseña incorrectos")
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