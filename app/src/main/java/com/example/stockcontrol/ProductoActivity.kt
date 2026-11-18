package com.example.stockcontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProductoActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        database = DatabaseHelper(this)

        val codigo = findViewById<EditText>(R.id.etCodigoProducto)
        val nombre = findViewById<EditText>(R.id.etNombreProducto)
        val descripcion = findViewById<EditText>(R.id.etDescripcionProducto)
        val precio = findViewById<EditText>(R.id.etPrecioProducto)
        val cantidad = findViewById<EditText>(R.id.etCantidadProducto)
        val stockMinimo = findViewById<EditText>(R.id.etStockMinimo)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarProducto)
        val btnVolver = findViewById<Button>(R.id.btnVolverProducto)

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {

            val codigoTexto = codigo.text.toString().trim()
            val nombreTexto = nombre.text.toString().trim()
            val descripcionTexto = descripcion.text.toString().trim()
            val precioTexto = precio.text.toString().trim()
            val cantidadTexto = cantidad.text.toString().trim()
            val stockTexto = stockMinimo.text.toString().trim()

            when {

                codigoTexto.isEmpty() ||
                        nombreTexto.isEmpty() ||
                        precioTexto.isEmpty() ||
                        cantidadTexto.isEmpty() ||
                        stockTexto.isEmpty() -> {

                    mensaje("Completa todos los campos obligatorios")
                }

                precioTexto.toDoubleOrNull() == null ||
                        precioTexto.toDouble() < 0 -> {

                    mensaje("Ingresa un precio válido")
                }

                cantidadTexto.toIntOrNull() == null ||
                        cantidadTexto.toInt() < 0 -> {

                    mensaje("Ingresa una cantidad válida")
                }

                stockTexto.toIntOrNull() == null ||
                        stockTexto.toInt() < 0 -> {

                    mensaje("Ingresa un stock mínimo válido")
                }

                else -> {

                    val registrado = database.registrarProducto(
                        codigoTexto,
                        nombreTexto,
                        descripcionTexto,
                        precioTexto.toDouble(),
                        cantidadTexto.toInt(),
                        stockTexto.toInt()
                    )

                    if (registrado) {

                        Toast.makeText(
                            this,
                            "¡PRODUCTO REGISTRADO CORRECTAMENTE!",
                            Toast.LENGTH_LONG
                        ).show()

                        codigo.text.clear()
                        nombre.text.clear()
                        descripcion.text.clear()
                        precio.text.clear()
                        cantidad.text.clear()
                        stockMinimo.text.clear()

                    } else {

                        mensaje("El código del producto ya está registrado")
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