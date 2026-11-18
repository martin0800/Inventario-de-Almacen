package com.example.stockcontrol

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EntradaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spProducto: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var etObservacion: EditText

    private var listaProductos = listOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada)

        dbHelper = DatabaseHelper(this)

        spProducto = findViewById(R.id.spProductoEntrada)
        etCantidad = findViewById(R.id.etCantidadEntrada)
        etObservacion = findViewById(R.id.etObservacionEntrada)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarEntrada)
        val btnVolver = findViewById<Button>(R.id.btnVolverEntrada)

        cargarProductos()

        btnGuardar.setOnClickListener {
            registrarEntrada()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarProductos() {

        listaProductos = dbHelper.obtenerProductos()

        if (listaProductos.isEmpty()) {
            Toast.makeText(
                this,
                "No hay productos registrados",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val nombresProductos = listaProductos.map {
            "${it.codigo} - ${it.nombre} (Stock: ${it.cantidad})"
        }

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresProductos
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spProducto.adapter = adaptador
    }

    private fun registrarEntrada() {

        if (listaProductos.isEmpty()) {
            Toast.makeText(
                this,
                "Primero debes registrar un producto",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val cantidadTexto = etCantidad.text.toString().trim()
        val observacion = etObservacion.text.toString().trim()

        if (cantidadTexto.isEmpty()) {
            etCantidad.error = "Ingresa una cantidad"
            etCantidad.requestFocus()
            return
        }

        val cantidad = cantidadTexto.toIntOrNull()

        if (cantidad == null || cantidad <= 0) {
            etCantidad.error = "Ingresa una cantidad mayor que cero"
            etCantidad.requestFocus()
            return
        }

        val productoSeleccionado = listaProductos[spProducto.selectedItemPosition]

        val resultado = dbHelper.registrarEntrada(
            productoSeleccionado.codigo,
            cantidad,
            observacion
        )

        if (resultado) {

            Toast.makeText(
                this,
                "Entrada registrada correctamente. Stock actualizado.",
                Toast.LENGTH_LONG
            ).show()

            etCantidad.text.clear()
            etObservacion.text.clear()

            cargarProductos()

        } else {

            Toast.makeText(
                this,
                "No se pudo registrar la entrada",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}