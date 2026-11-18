package com.example.stockcontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private val preferencias by lazy {
        getSharedPreferences("StockControlPreferencias", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        aplicarTemaGuardado()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnProductos = findViewById<Button>(R.id.btnProductos)
        val btnInventario = findViewById<Button>(R.id.btnInventario)
        val btnEntradas = findViewById<Button>(R.id.btnEntradas)
        val btnSalidas = findViewById<Button>(R.id.btnSalidas)
        val btnAlertas = findViewById<Button>(R.id.btnAlertas)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnTema = findViewById<Button>(R.id.btnTema)

        btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        btnInventario.setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
        }

        btnEntradas.setOnClickListener {
            startActivity(Intent(this, EntradaActivity::class.java))
        }

        btnSalidas.setOnClickListener {
            startActivity(Intent(this, SalidaActivity::class.java))
        }

        btnAlertas.setOnClickListener {
            startActivity(Intent(this, AlertasActivity::class.java))
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        btnTema.setOnClickListener {
            mostrarSelectorTema()
        }

        // El botón de cerrar sesión se conectará al agregarlo al XML.
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            confirmarCierreSesion()
        }
    }

    private fun confirmarCierreSesion() {

        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar sesión") { _, _ ->

                val intent = Intent(this, LoginActivity::class.java)

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun mostrarSelectorTema() {

        val opciones = arrayOf(
            "☀️ Modo claro",
            "🌙 Modo oscuro",
            "⚙️ Seguir configuración del dispositivo"
        )

        AlertDialog.Builder(this)
            .setTitle("Seleccionar apariencia")
            .setItems(opciones) { _, seleccion ->

                val tema = when (seleccion) {
                    0 -> "claro"
                    1 -> "oscuro"
                    else -> "sistema"
                }

                preferencias.edit()
                    .putString("tema", tema)
                    .apply()

                aplicarTema(tema)

                Toast.makeText(
                    this,
                    "Apariencia actualizada",
                    Toast.LENGTH_SHORT
                ).show()

                recreate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarTemaGuardado() {

        val tema = preferencias.getString("tema", "sistema") ?: "sistema"

        aplicarTema(tema)
    }

    private fun aplicarTema(tema: String) {

        when (tema) {

            "claro" -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }

            "oscuro" -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }

            else -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}