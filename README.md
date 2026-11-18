# 📦 StockControl
### Sistema de Gestión de Inventario de Almacén

## 📋 Descripción

StockControl es una aplicación móvil Android desarrollada para facilitar la administración y el control de inventarios de un almacén.

El sistema permite registrar productos, controlar entradas y salidas de mercadería, consultar existencias y recibir alertas cuando los productos alcanzan su nivel mínimo de stock.

La aplicación cuenta con una interfaz intuitiva, almacenamiento local y opciones de personalización visual.

## 🎯 Objetivo del proyecto

Desarrollar una aplicación móvil que permita gestionar eficientemente los productos y movimientos de inventario, manteniendo organizada y actualizada la información del almacén.

## 🚀 Funcionalidades principales

### 🔐 Usuarios
- Registro de usuarios.
- Inicio de sesión.
- Validación de credenciales.
- Cierre de sesión.

### 📦 Gestión de productos
- Registrar productos.
- Consultar inventario.
- Modificar productos.
- Eliminar productos.
- Consultar cantidades disponibles.
- Configurar stock mínimo.

### 📥 Entradas de mercadería
- Registrar entradas de productos.
- Actualizar automáticamente las existencias.
- Guardar el movimiento en el historial.

### 📤 Salidas de mercadería
- Registrar salidas de productos.
- Validar que exista stock suficiente.
- Actualizar automáticamente las existencias.
- Guardar el movimiento en el historial.

### ⚠️ Alertas de inventario
- Identificar productos con stock bajo.
- Detectar productos que alcanzan el stock mínimo.
- Mostrar alertas de stock crítico.

### 📊 Historial de movimientos
- Consultar entradas y salidas.
- Visualizar cantidades anteriores y nuevas.
- Consultar observaciones y fechas.

### 🎨 Apariencia
- Modo claro.
- Modo oscuro.
- Opción para seguir la configuración del dispositivo.

## 🛠️ Tecnologías utilizadas

- Android Studio.
- Kotlin.
- XML.
- SQLite.
- Git.
- GitHub.

## 🗄️ Almacenamiento de datos

La aplicación utiliza SQLite para almacenar localmente:

- Usuarios.
- Productos.
- Movimientos de inventario.

Los datos permanecen almacenados aunque se cierre la aplicación.

## 📱 Requisitos para ejecutar el proyecto

- Android Studio.
- JDK compatible con la versión de Gradle del proyecto.
- Android SDK requerido por el proyecto.
- Emulador Android o dispositivo físico compatible.

## ⚙️ Instalación

1. Clonar el repositorio desde GitHub.
2. Abrir Android Studio.
3. Seleccionar Open.
4. Buscar la carpeta del proyecto StockControl.
5. Esperar la sincronización de Gradle.
6. Ejecutar la aplicación en un emulador o dispositivo Android.

## 📂 Estructura general

```text
StockControl/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/stockcontrol/
│           └── res/
│               ├── layout/
│               ├── values/
│               └── values-night/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🔗 Repositorio

GitHub:

https://github.com/martin0800/StockControl

## 👨‍💻 Autor

Proyecto académico desarrollado como aplicación Android de gestión de inventario.

**Nombre del proyecto:** StockControl  
**Lenguaje principal:** Kotlin  
**Base de datos:** SQLite  
**Plataforma:** Android

---
StockControl | Gestión inteligente de almacén