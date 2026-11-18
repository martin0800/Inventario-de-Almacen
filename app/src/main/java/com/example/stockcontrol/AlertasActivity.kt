package com.example.stockcontrol

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlertasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contenedorAlertas: LinearLayout
    private lateinit var tvTotalAlertas: TextView
    private lateinit var tvSinAlertas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alertas)

        dbHelper = DatabaseHelper(this)

        contenedorAlertas = findViewById(R.id.contenedorAlertas)
        tvTotalAlertas = findViewById(R.id.tvTotalAlertas)
        tvSinAlertas = findViewById(R.id.tvSinAlertas)

        val btnVolver = findViewById<Button>(R.id.btnVolverAlertas)

        btnVolver.setOnClickListener {
            finish()
        }

        cargarAlertas()
    }

    private fun cargarAlertas() {

        contenedorAlertas.removeAllViews()

        val productos = dbHelper.obtenerProductos()

        val productosConAlerta = productos.filter {
            it.cantidad <= it.stockMinimo
        }

        tvTotalAlertas.text =
            "Productos con alertas: ${productosConAlerta.size}"

        if (productosConAlerta.isEmpty()) {
            tvSinAlertas.visibility = View.VISIBLE
            return
        }

        tvSinAlertas.visibility = View.GONE

        productosConAlerta.forEach { producto ->
            crearTarjetaAlerta(producto)
        }
    }

    private fun crearTarjetaAlerta(producto: Producto) {

        val esCritico = producto.cantidad < producto.stockMinimo

        val colorAlerta = if (esCritico) {
            Color.rgb(198, 40, 40)
        } else {
            Color.rgb(245, 124, 0)
        }

        val estado = if (esCritico) {
            "STOCK CRÍTICO"
        } else {
            "STOCK BAJO"
        }

        val colorSuperficie = getColor(R.color.color_superficie)
        val colorTexto = getColor(R.color.color_texto)
        val colorTextoSecundario =
            getColor(R.color.color_texto_secundario)

        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(colorSuperficie)
            elevation = 4f
        }

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 16
        }

        tarjeta.layoutParams = parametros

        agregarTexto(
            tarjeta,
            estado,
            20,
            colorAlerta,
            true
        )

        agregarTexto(
            tarjeta,
            "Producto: ${producto.nombre}",
            17,
            colorTexto,
            true
        )

        agregarTexto(
            tarjeta,
            "Código: ${producto.codigo}",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Stock disponible: ${producto.cantidad} unidades",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Stock mínimo: ${producto.stockMinimo} unidades",
            15,
            colorTextoSecundario
        )

        contenedorAlertas.addView(tarjeta)
    }

    private fun agregarTexto(
        contenedor: LinearLayout,
        texto: String,
        tamano: Int,
        color: Int,
        negrita: Boolean = false
    ) {

        val textoVista = TextView(this).apply {
            text = texto
            textSize = tamano.toFloat()
            setTextColor(color)
            setPadding(0, 5, 0, 5)

            if (negrita) {
                setTypeface(null, Typeface.BOLD)
            }
        }

        contenedor.addView(textoVista)
    }

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized) {
            cargarAlertas()
        }
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }

        super.onDestroy()
    }
}