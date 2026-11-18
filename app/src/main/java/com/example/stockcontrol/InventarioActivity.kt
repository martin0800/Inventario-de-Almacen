package com.example.stockcontrol

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity

class InventarioActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var contenedor: LinearLayout
    private lateinit var total: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        database = DatabaseHelper(this)

        contenedor = findViewById(R.id.contenedorProductos)
        total = findViewById(R.id.tvTotalProductos)

        val btnVolver = findViewById<Button>(R.id.btnVolverInventario)

        btnVolver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized) {
            mostrarProductos()
        }
    }

    private fun mostrarProductos() {

        contenedor.removeAllViews()

        val productos = database.obtenerProductos()

        total.text = "Total de productos: ${productos.size}"

        val colorTexto = getColor(R.color.color_texto)
        val colorSecundario = getColor(R.color.color_texto_secundario)
        val colorSuperficie = getColor(R.color.color_superficie)

        if (productos.isEmpty()) {

            val mensaje = TextView(this).apply {
                text = "No hay productos registrados"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(colorTexto)
                setPadding(15, 30, 15, 30)
            }

            contenedor.addView(mensaje)
            return
        }

        for (producto in productos) {

            val tarjeta = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
                setBackgroundColor(colorSuperficie)
                elevation = 4f
            }

            val informacion = TextView(this).apply {
                text = """
                    Código: ${producto.codigo}
                    Nombre: ${producto.nombre}
                    Descripción: ${producto.descripcion}
                    Precio: ${producto.precio}
                    Cantidad: ${producto.cantidad}
                    Stock mínimo: ${producto.stockMinimo}
                    Estado: ${if (producto.cantidad <= producto.stockMinimo) "STOCK BAJO" else "DISPONIBLE"}
                """.trimIndent()

                textSize = 14f
                setTextColor(colorTexto)
            }

            tarjeta.addView(informacion)

            val botones = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val btnModificar = Button(this).apply {
                text = "MODIFICAR"
                backgroundTintList = ColorStateList.valueOf(
                    Color.rgb(23, 107, 67)
                )
                setTextColor(Color.WHITE)
            }

            val btnEliminar = Button(this).apply {
                text = "ELIMINAR"
                backgroundTintList = ColorStateList.valueOf(
                    Color.rgb(211, 47, 47)
                )
                setTextColor(Color.WHITE)
            }

            val parametros = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )

            parametros.setMargins(5, 10, 5, 0)

            botones.addView(btnModificar, parametros)
            botones.addView(btnEliminar, parametros)

            tarjeta.addView(botones)

            val parametrosTarjeta = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            parametrosTarjeta.setMargins(0, 0, 0, 15)

            contenedor.addView(tarjeta, parametrosTarjeta)

            btnModificar.setOnClickListener {
                mostrarFormularioEdicion(producto)
            }

            btnEliminar.setOnClickListener {
                confirmarEliminacion(producto.codigo)
            }
        }
    }

    private fun mostrarFormularioEdicion(producto: Producto) {

        val formulario = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 10, 30, 10)
        }

        fun crearCampo(
            etiqueta: String,
            valor: String,
            tipo: Int = InputType.TYPE_CLASS_TEXT
        ): EditText {

            val contenedorCampo = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val titulo = TextView(this).apply {
                text = etiqueta
                textSize = 14f
                setTextColor(getColor(R.color.color_texto))
                setPadding(0, 10, 0, 2)
            }

            val campo = EditText(this).apply {
                setText(valor)
                textSize = 15f
                setTextColor(getColor(R.color.color_texto))
                inputType = tipo
                setSingleLine(true)
                setPadding(8, 5, 8, 5)
            }

            contenedorCampo.addView(titulo)
            contenedorCampo.addView(campo)
            formulario.addView(contenedorCampo)

            return campo
        }

        val codigo = crearCampo("Código", producto.codigo)
        val nombre = crearCampo("Nombre", producto.nombre)
        val descripcion = crearCampo("Descripción", producto.descripcion)

        val precio = crearCampo(
            "Precio",
            producto.precio.toString(),
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )

        val cantidad = crearCampo(
            "Cantidad",
            producto.cantidad.toString(),
            InputType.TYPE_CLASS_NUMBER
        )

        val stockMinimo = crearCampo(
            "Stock mínimo",
            producto.stockMinimo.toString(),
            InputType.TYPE_CLASS_NUMBER
        )

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Modificar producto")
            .setView(formulario)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar cambios", null)
            .create()

        dialogo.setOnShowListener {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val codigoTexto = codigo.text.toString().trim()
                    val nombreTexto = nombre.text.toString().trim()
                    val descripcionTexto = descripcion.text.toString().trim()
                    val precioTexto = precio.text.toString().trim()
                    val cantidadTexto = cantidad.text.toString().trim()
                    val stockTexto = stockMinimo.text.toString().trim()

                    val precioNumero = precioTexto.toDoubleOrNull()
                    val cantidadNumero = cantidadTexto.toIntOrNull()
                    val stockNumero = stockTexto.toIntOrNull()

                    when {

                        codigoTexto.isEmpty() ||
                                nombreTexto.isEmpty() ||
                                precioTexto.isEmpty() ||
                                cantidadTexto.isEmpty() ||
                                stockTexto.isEmpty() -> {
                            mensaje("Completa los campos obligatorios")
                        }

                        precioNumero == null || precioNumero < 0 -> {
                            mensaje("Ingresa un precio válido")
                        }

                        cantidadNumero == null || cantidadNumero < 0 -> {
                            mensaje("Ingresa una cantidad válida")
                        }

                        stockNumero == null || stockNumero < 0 -> {
                            mensaje("Ingresa un stock mínimo válido")
                        }

                        else -> {

                            val actualizado = database.actualizarProducto(
                                producto.codigo,
                                codigoTexto,
                                nombreTexto,
                                descripcionTexto,
                                precioNumero,
                                cantidadNumero,
                                stockNumero
                            )

                            if (actualizado) {
                                mensaje("Producto actualizado correctamente")
                                dialogo.dismiss()
                                mostrarProductos()
                            } else {
                                mensaje("No se pudo actualizar. Verifica que el código no esté repetido.")
                            }
                        }
                    }
                }
        }

        dialogo.show()
    }

    private fun confirmarEliminacion(codigo: String) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que deseas eliminar el producto $codigo?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->

                val eliminado = database.eliminarProducto(codigo)

                if (eliminado) {
                    mensaje("Producto eliminado correctamente")
                    mostrarProductos()
                } else {
                    mensaje("No se pudo eliminar el producto")
                }
            }
            .show()
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