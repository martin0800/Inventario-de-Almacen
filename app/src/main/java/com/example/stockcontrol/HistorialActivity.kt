package com.example.stockcontrol

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistorialActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contenedorMovimientos: LinearLayout
    private lateinit var tvTotalMovimientos: TextView
    private lateinit var tvSinMovimientos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        dbHelper = DatabaseHelper(this)

        contenedorMovimientos =
            findViewById(R.id.contenedorMovimientos)

        tvTotalMovimientos =
            findViewById(R.id.tvTotalMovimientos)

        tvSinMovimientos =
            findViewById(R.id.tvSinMovimientos)

        val btnVolver = findViewById<Button>(
            R.id.btnVolverHistorial
        )

        btnVolver.setOnClickListener {
            finish()
        }

        cargarMovimientos()
    }

    private fun cargarMovimientos() {

        contenedorMovimientos.removeAllViews()

        val movimientos = dbHelper.obtenerMovimientos()

        tvTotalMovimientos.text =
            "Total de movimientos: ${movimientos.size}"

        if (movimientos.isEmpty()) {
            tvSinMovimientos.visibility = View.VISIBLE
            return
        }

        tvSinMovimientos.visibility = View.GONE

        movimientos.forEach { movimiento ->
            crearTarjetaMovimiento(movimiento)
        }
    }

    private fun crearTarjetaMovimiento(
        movimiento: Movimiento
    ) {

        val colorSuperficie = getColor(R.color.color_superficie)
        val colorTexto = getColor(R.color.color_texto)
        val colorTextoSecundario =
            getColor(R.color.color_texto_secundario)

        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
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

        val esEntrada = movimiento.tipo == "ENTRADA"

        val colorMovimiento = if (esEntrada) {
            Color.rgb(46, 125, 50)
        } else {
            Color.rgb(198, 40, 40)
        }

        agregarTexto(
            tarjeta,
            movimiento.tipo,
            20,
            colorMovimiento,
            true
        )

        agregarTexto(
            tarjeta,
            "Producto: ${movimiento.nombreProducto}",
            17,
            colorTexto,
            true
        )

        agregarTexto(
            tarjeta,
            "Código: ${movimiento.codigoProducto}",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Cantidad: ${movimiento.cantidad} unidades",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Stock anterior: ${movimiento.stockAnterior}",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Stock nuevo: ${movimiento.stockNuevo}",
            15,
            colorTextoSecundario
        )

        agregarTexto(
            tarjeta,
            "Fecha: ${movimiento.fecha}",
            14,
            colorTextoSecundario
        )

        if (movimiento.observacion.isNotBlank()) {
            agregarTexto(
                tarjeta,
                "Observación: ${movimiento.observacion}",
                14,
                colorTextoSecundario
            )
        }

        contenedorMovimientos.addView(tarjeta)
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
            cargarMovimientos()
        }
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }

        super.onDestroy()
    }
}