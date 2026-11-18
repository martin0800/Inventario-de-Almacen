package com.example.stockcontrol

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteConstraintException
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Producto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int,
    val stockMinimo: Int
)

data class Movimiento(
    val id: Int,
    val codigoProducto: String,
    val nombreProducto: String,
    val tipo: String,
    val cantidad: Int,
    val stockAnterior: Int,
    val stockNuevo: Int,
    val observacion: String,
    val fecha: String
)

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "StockControl.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                usuario TEXT NOT NULL UNIQUE,
                password_salt TEXT NOT NULL,
                password_hash TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL UNIQUE,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                cantidad INTEGER NOT NULL,
                stock_minimo INTEGER NOT NULL
            )
        """.trimIndent())

        crearTablaMovimientos(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE productos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    codigo TEXT NOT NULL UNIQUE,
                    nombre TEXT NOT NULL,
                    descripcion TEXT,
                    precio REAL NOT NULL,
                    cantidad INTEGER NOT NULL,
                    stock_minimo INTEGER NOT NULL
                )
            """.trimIndent())
        }

        if (oldVersion < 3) {
            crearTablaMovimientos(db)
        }
    }

    private fun crearTablaMovimientos(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE movimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo_producto TEXT NOT NULL,
                nombre_producto TEXT NOT NULL,
                tipo TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                stock_anterior INTEGER NOT NULL,
                stock_nuevo INTEGER NOT NULL,
                observacion TEXT,
                fecha TEXT NOT NULL
            )
        """.trimIndent())
    }

    // GENERAR HASH DE CONTRASEÑA
    private fun generarHash(
        password: String,
        salt: ByteArray
    ): ByteArray {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            120000,
            256
        )

        return SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(spec)
            .encoded
    }

    // REGISTRAR USUARIO
    fun registrarUsuario(
        nombre: String,
        correo: String,
        usuario: String,
        password: String
    ): Boolean {

        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val hash = generarHash(password, salt)

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo.trim().lowercase())
            put("usuario", usuario)
            put("password_salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            put("password_hash", Base64.encodeToString(hash, Base64.NO_WRAP))
        }

        return try {
            writableDatabase.insertOrThrow("usuarios", null, valores) != -1L
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // VERIFICAR USUARIO
    fun verificarUsuario(
        correo: String,
        password: String
    ): Boolean {

        val cursor = readableDatabase.query(
            "usuarios",
            arrayOf("password_salt", "password_hash"),
            "correo = ?",
            arrayOf(correo.trim().lowercase()),
            null,
            null,
            null
        )

        cursor.use {

            if (!it.moveToFirst()) return false

            val salt = Base64.decode(it.getString(0), Base64.NO_WRAP)
            val hashGuardado = Base64.decode(it.getString(1), Base64.NO_WRAP)
            val hashIngresado = generarHash(password, salt)

            return MessageDigest.isEqual(hashGuardado, hashIngresado)
        }
    }

    // REGISTRAR PRODUCTO
    fun registrarProducto(
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double,
        cantidad: Int,
        stockMinimo: Int
    ): Boolean {

        val valores = ContentValues().apply {
            put("codigo", codigo.trim().uppercase())
            put("nombre", nombre.trim())
            put("descripcion", descripcion.trim())
            put("precio", precio)
            put("cantidad", cantidad)
            put("stock_minimo", stockMinimo)
        }

        return try {
            writableDatabase.insertOrThrow("productos", null, valores) != -1L
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // CONSULTAR PRODUCTOS
    fun consultarProductos(): List<String> {

        val lista = mutableListOf<String>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT codigo, nombre, descripcion, precio, cantidad, stock_minimo
            FROM productos
            ORDER BY nombre ASC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val codigo = it.getString(0)
                val nombre = it.getString(1)
                val descripcion = it.getString(2)
                val precio = it.getDouble(3)
                val cantidad = it.getInt(4)
                val stockMinimo = it.getInt(5)

                lista.add("""
                    Código: $codigo
                    Nombre: $nombre
                    Descripción: $descripcion
                    Precio: $precio
                    Cantidad: $cantidad
                    Stock mínimo: $stockMinimo
                """.trimIndent())
            }
        }

        return lista
    }

    // OBTENER PRODUCTOS
    fun obtenerProductos(): List<Producto> {

        val lista = mutableListOf<Producto>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT codigo, nombre, descripcion, precio, cantidad, stock_minimo
            FROM productos
            ORDER BY nombre ASC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                lista.add(
                    Producto(
                        codigo = it.getString(0),
                        nombre = it.getString(1),
                        descripcion = it.getString(2) ?: "",
                        precio = it.getDouble(3),
                        cantidad = it.getInt(4),
                        stockMinimo = it.getInt(5)
                    )
                )
            }
        }

        return lista
    }

    // MODIFICAR PRODUCTO
    fun actualizarProducto(
        codigoOriginal: String,
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double,
        cantidad: Int,
        stockMinimo: Int
    ): Boolean {

        val valores = ContentValues().apply {
            put("codigo", codigo.trim().uppercase())
            put("nombre", nombre.trim())
            put("descripcion", descripcion.trim())
            put("precio", precio)
            put("cantidad", cantidad)
            put("stock_minimo", stockMinimo)
        }

        return try {

            writableDatabase.update(
                "productos",
                valores,
                "codigo = ?",
                arrayOf(codigoOriginal)
            ) > 0

        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // ELIMINAR PRODUCTO
    fun eliminarProducto(codigo: String): Boolean {

        return writableDatabase.delete(
            "productos",
            "codigo = ?",
            arrayOf(codigo)
        ) > 0
    }

    // REGISTRAR ENTRADA
    fun registrarEntrada(
        codigo: String,
        cantidad: Int,
        observacion: String
    ): Boolean {

        if (cantidad <= 0) return false

        val db = writableDatabase
        db.beginTransaction()

        try {

            val cursor = db.query(
                "productos",
                arrayOf("nombre", "cantidad"),
                "codigo = ?",
                arrayOf(codigo),
                null,
                null,
                null
            )

            var nombreProducto = ""
            var stockAnterior = 0

            cursor.use {

                if (!it.moveToFirst()) return false

                nombreProducto = it.getString(0)
                stockAnterior = it.getInt(1)
            }

            val stockNuevo = stockAnterior + cantidad

            val valoresProducto = ContentValues().apply {
                put("cantidad", stockNuevo)
            }

            if (db.update(
                    "productos",
                    valoresProducto,
                    "codigo = ?",
                    arrayOf(codigo)
                ) <= 0
            ) return false

            val fecha = SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

            val valoresMovimiento = ContentValues().apply {
                put("codigo_producto", codigo)
                put("nombre_producto", nombreProducto)
                put("tipo", "ENTRADA")
                put("cantidad", cantidad)
                put("stock_anterior", stockAnterior)
                put("stock_nuevo", stockNuevo)
                put("observacion", observacion.trim())
                put("fecha", fecha)
            }

            if (db.insert("movimientos", null, valoresMovimiento) == -1L) {
                return false
            }

            db.setTransactionSuccessful()
            return true

        } catch (e: Exception) {
            return false

        } finally {
            db.endTransaction()
        }
    }

    // REGISTRAR SALIDA
    fun registrarSalida(
        codigo: String,
        cantidad: Int,
        observacion: String
    ): Boolean {

        if (cantidad <= 0) return false

        val db = writableDatabase
        db.beginTransaction()

        try {

            val cursor = db.query(
                "productos",
                arrayOf("nombre", "cantidad"),
                "codigo = ?",
                arrayOf(codigo),
                null,
                null,
                null
            )

            var nombreProducto = ""
            var stockAnterior = 0

            cursor.use {

                if (!it.moveToFirst()) return false

                nombreProducto = it.getString(0)
                stockAnterior = it.getInt(1)
            }

            if (cantidad > stockAnterior) return false

            val stockNuevo = stockAnterior - cantidad

            val valoresProducto = ContentValues().apply {
                put("cantidad", stockNuevo)
            }

            if (db.update(
                    "productos",
                    valoresProducto,
                    "codigo = ?",
                    arrayOf(codigo)
                ) <= 0
            ) return false

            val fecha = SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

            val valoresMovimiento = ContentValues().apply {
                put("codigo_producto", codigo)
                put("nombre_producto", nombreProducto)
                put("tipo", "SALIDA")
                put("cantidad", cantidad)
                put("stock_anterior", stockAnterior)
                put("stock_nuevo", stockNuevo)
                put("observacion", observacion.trim())
                put("fecha", fecha)
            }

            if (db.insert("movimientos", null, valoresMovimiento) == -1L) {
                return false
            }

            db.setTransactionSuccessful()
            return true

        } catch (e: Exception) {
            return false

        } finally {
            db.endTransaction()
        }
    }

    // CONSULTAR HISTORIAL DE MOVIMIENTOS
    fun obtenerMovimientos(): List<Movimiento> {

        val lista = mutableListOf<Movimiento>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT id, codigo_producto, nombre_producto, tipo,
                   cantidad, stock_anterior, stock_nuevo,
                   observacion, fecha
            FROM movimientos
            ORDER BY id DESC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                lista.add(
                    Movimiento(
                        id = it.getInt(0),
                        codigoProducto = it.getString(1),
                        nombreProducto = it.getString(2),
                        tipo = it.getString(3),
                        cantidad = it.getInt(4),
                        stockAnterior = it.getInt(5),
                        stockNuevo = it.getInt(6),
                        observacion = it.getString(7) ?: "",
                        fecha = it.getString(8)
                    )
                )
            }
        }

        return lista
    }
}