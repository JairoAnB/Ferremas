# FERREMAS - Sistema de Consulta RESTful para Red de Ferreterías

FERREMAS es un proyecto semestral desarrollado para la asignatura **Integración de Plataformas**. Este sistema simula la solución tecnológica de una red de ferreterías con múltiples sucursales físicas, enfocándose principalmente en la integración de plataformas mediante **APIs RESTful** construidas en **Java con Spring Boot**.

## :toolbox: Tecnologías utilizadas

| Herramienta | Descripción |
|-------------|-------------|
| ![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.x-brightgreen?logo=spring-boot) | Framework principal para el backend |
| ![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk) | Lenguaje utilizado |
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql) | Base de datos relacional |
| ![Postman](https://img.shields.io/badge/Postman-API%20Testing-orange?logo=postman) | Herramienta para probar las APIs |

# ✅ Guía de Instalación y Configuración del Sistema FERREMAS

> **Requisitos previos**:
- Tener instalado **IntelliJ IDEA** (fue el IDE utilizado durante todo el desarrollo).
- Tener instalado **PostgreSQL** como sistema de base de datos.

---

## 🛠 Instalación de PostgreSQL

1. Descarga PostgreSQL desde su [página oficial](https://www.postgresql.org/download/).
2. Durante la instalación, se te pedirá establecer una **contraseña** para el usuario `postgres`. **Guárdala**, la necesitarás más adelante.
3. Una vez instalado, abre **pgAdmin 4**.
4. Ingresa la contraseña creada anteriormente cuando se te solicite.
5. En la interfaz:
   - Ve a `Servers` → Clic derecho en `Databases` → `Create` → `Database`.
   - Crea **cuatro bases de datos** con los nombres que usarás para cada uno de los microservicios (ej: `ventas`, `usuarios`, `productos`, `inventario`).

> ⚠️ **Recomendación**: No cambies el nombre de usuario (`postgres`) por defecto, ya que puede generar errores.

---

## ⚙ Configuración de los Microservicios

1. Abre el proyecto en **IntelliJ IDEA**.
2. Dirígete a cada microservicio:
   - Ruta: `src/main/resources/application.properties`
3. Ajusta los siguientes parámetros:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/{NOMBRE_DE_TU_BASE}
spring.datasource.username=postgres
spring.datasource.password={TU_CONTRASEÑA_POSTGRES}
server.port=808x
```

> Reemplaza `{NOMBRE_DE_TU_BASE}` con el nombre de la base de datos que creaste para ese microservicio, y `{TU_CONTRASEÑA_POSTGRES}` con la contraseña que definiste al instalar PostgreSQL.

> ⚠️ **IMPORTANTE**: No cambies los puertos (`server.port`) de los microservicios. Si lo haces, deberás modificar las URLs donde se consumen las APIs en todos los demás servicios.

---

## 🔒 Advertencia Final

**NO CAMBIES** ni modifiques:
- Las carpetas
- Variables
- Nombres de paquetes
- Configuraciones del proyecto

Si te ocurre un error o tienes dudas, **háblame por interno** para ayudarte a solucionarlo correctamente.

---
# 📦 Microservicio de Categorías

Este microservicio forma parte de un sistema distribuido basado en microservicios para la gestión de productos. Su responsabilidad principal es manejar la información relacionada a las **categorías**, las cuales son indispensables para la creación y clasificación de productos dentro del sistema.

Este proyecto sigue los principios de **separación de responsabilidades**, **escalabilidad** y **autonomía de servicios**, permitiendo que cada microservicio opere de forma independiente y con su propia lógica de negocio.

---

## 🌐 Endpoint base

```
http://localhost:808x/api/v1/categories
```

---

## 📌 Endpoints disponibles

### 🔹 GET /  
Obtiene todas las categorías registradas.

- **Sin categorías existentes**  
  - Respuesta: `[]`  
  - Código HTTP: `204 No Content`

- **Con categorías registradas**  
  - Ejemplo de respuesta:
    ```json
    [
      {
        "id": 1,
        "nombre": "Electronicos",
        "descripcion": "productos Electronicos varios, taladros, etc",
        "imagen": "http://imagen.png"
      }
    ]
    ```
  - Código HTTP: `200 OK`

---

### 🔹 GET /{id}  
Obtiene una categoría específica por ID.

- **Si no se encuentra la categoría**:
  ```json
  {
    "message": "La categoria con la id 1 no existe",
    "error": "La categoria no fue encontrada, revise la id e intente nuevamente"
  }
  ```
  - Código HTTP: `404 Not Found`

- **Si se encuentra la categoría**:
  ```json
  {
    "id": 1,
    "nombre": "Electronicos",
    "descripcion": "productos Electronicos varios, taladros, etc",
    "imagen": "1"
  }
  ```
  - Código HTTP: `200 OK`

---

### 🔹 POST /create  
Crea una nueva categoría.

- **Request Body**:
  ```json
  {
    "nombre": "Electronicos",
    "descripcion": "productos varios",
    "imagen": "http://imagen.png" // opcional
  }
  ```

- **Respuesta esperada**:
  - Mensaje: `"Categoria creada correctamente con id: X"`
  - Código HTTP: `201 Created`

- **Errores posibles**:
  - Nombre duplicado:  
    - Código: `409 Conflict`
  - Campos faltantes o inválidos:  
    - Mensaje: `"El campo ... no puede ser nulo"`  
    - Código: `400 Bad Request`

---

### 🔹 PUT /update/{id}  
Actualiza una categoría existente.

- **Request Body**:
  ```json
  {
    "nombre": "Nuevo nombre",
    "descripcion": "Nueva descripción",
    "imagen": "http://nueva-imagen.png" // opcional
  }
  ```

- **Errores comunes**:
  ```json
  {
    "descripcion": "La descripcion no puede estar vacía",
    "nombre": "El nombre no puede estar vacío"
  }
  ```
  - Código HTTP: `400 Bad Request`

- **Respuesta exitosa**:
  - Mensaje: `"Categoria actualizada correctamente con id: X"`
  - Código HTTP: `200 OK`

---

### 🔹 DELETE /delete/{id}  
Elimina una categoría por su ID.

- **Si la ID no existe**:
  ```json
  {
    "message": "La categoria con la id 4 no existe",
    "error": "La categoria no fue encontrada, revise la id e intente nuevamente"
  }
  ```
  - Código HTTP: `404 Not Found`

- **Si la categoría se elimina correctamente**:
  - Mensaje: `"La categoria con la id 1 fue eliminada correctamente"`
  - Código HTTP: `200 OK`

---

## ⚠️ Consideraciones

- No se permiten categorías con **nombres duplicados**.
- Los campos `nombre` y `descripcion` son obligatorios tanto al crear como al actualizar.
- El campo `imagen` es opcional.
- Cada microservicio debe tener su propia responsabilidad y control de errores.

---

# Microservicio de Usuarios

Este microservicio tiene como propósito principal la gestión básica de usuarios dentro del sistema. Su diseño contempla una mínima validación de datos y administración de roles, lo que permitirá futuras expansiones como autenticación o control de acceso por perfiles. Forma parte de un ecosistema de microservicios independientes, donde cada uno cumple una función específica.

## 📘 Documentación de Endpoints

### 🔍 GET

#### Obtener todos los usuarios

- **URL**: `http://localhost:808x/api/v1/users`
- **Body**: No requerido
- **Respuesta esperada si no hay usuarios**: `[]`
- **Código**: `204 No Content`

#### Obtener un usuario por ID

- **URL**: `http://localhost:808x/api/v1/users/{id}`
- **Header**: (Obligatorio) `Long id` válido
- **Respuesta exitosa**:

```json
{
  "id": 1,
  "nombre": "jairo",
  "apellido": "gonzalez",
  "email": "jair.gonzalez@duocuc.cl",
  "rol": "USUARIO"
}
```
- **Código**: `200 OK`

- **Respuesta con ID inválido**:

```json
{
  "message": "El usuario con la id 2 no existe",
  "error": "El usuario no fue encontrado, por favor revisa si la id es correcta"
}
```
- **Código**: `404 Not Found`

---

### ➕ POST

- **URL**: `http://localhost:808x/api/v1/users/create`
- **Body requerido**:
  - `nombre` (String)
  - `apellido` (String)
  - `email` (String)
  - `password` (String)
  - `rol` (String) → debe ser uno de: `ADMINISTRADOR`, `USUARIO`, `CLIENTE` (en mayúsculas)
- **Respuesta esperada**:

```json
{
  "id": 1,
  "nombre": "jairo",
  "apellido": "gonzalez",
  "email": "jair.gonzalez@duocuc.cl",
  "rol": "USUARIO"
}
```
- **Código**: `201 Created`

- **Errores posibles**:
  - Campos obligatorios nulos o inválidos
  - Contraseña con menos de 8 caracteres o sin mayúscula
- **Código**: `400 Bad Request`

---

### ✏️ PUT

- **URL**: `http://localhost:808x/api/v1/users/update/{id}`
- **Header**: `Long id` válido
- **Body requerido**: igual que POST
- **Respuesta esperada**: `Usuario actualizado correctamente`
- **Código**: `200 OK`

- **Errores posibles**:
  - Campos nulos o inválidos
- **Código**: `400 Bad Request`

---

### ❌ DELETE

- **URL**: `http://localhost:808x/api/v1/users/delete/{id}`
- **Header**: `Long id` válido
- **Respuesta esperada**: `El usuario fue eliminado correctamente`
- **Código**: `200 OK`

- **Respuesta con ID inválido**:

```json
{
  "message": "El usuario con la id 3 no existe",
  "error": "El usuario no fue encontrado, por favor revisa si la id es correcta"
}
```
- **Código**: `404 Not Found`

---

## ⚠️ Validaciones importantes

- Los campos `nombre`, `apellido`, `email`, `password`, y `rol` son obligatorios.
- La `contraseña` debe tener al menos **8 caracteres** y **una letra mayúscula**.
- El `rol` debe ser: `ADMINISTRADOR`, `USUARIO`, o `CLIENTE` (solo en mayúsculas).

---

# 🏬 Microservicio de Inventario

Este microservicio es uno de los **componentes principales** del sistema, encargado de gestionar los productos y su stock. Maneja tanto el **stock disponible para venta** como el **stock almacenado en bodega**, lo que permite mantener un control detallado y actualizado del inventario.

## 🌐 URLs base

- `/api/v1/products`
- `/api/v1/inventory`

---

## 📦 Módulo: Products

### 🔹 GET /products
- **Respuesta sin productos**: `[]`
- **Código**: `204 No Content`
- **Error de conexión**: `500 Internal Server Error`

### 🔹 GET /products/{id}
- **Respuesta exitosa**:
```json
{
  "marca": "generico",
  "codigo": "gg-001",
  "nombre": "Martillo",
  "precio": 10000,
  "stock": 10,
  "descripcion": "Martillo generico prueba",
  "categoria_id": 1,
  "fecha_creacion": "2025-05-24"
}
```
- **Código**: `200 OK`
- **Error**:
```json
{
  "mensaje": "El producto con la id 2 no existe",
  "detalles": "El producto no pudo ser encontrado, por favor revisa si la id es correcta"
}
```
- **Código**: `404 Not Found`

### 🔹 GET /products/stock/{id}
- **Respuesta**: `{ "stock": 10 }` → `200 OK`
- **Error**: mismo formato anterior → `404 Not Found`

### 🔹 POST /products/create
- **Campos requeridos**: código, nombre, precio, stock, stock_bodega, descripción, categoria_id
- **Respuesta exitosa**: `201 Created`
- **Errores**:
```json
{
  "codigo": "El campo de codigo es obligatorio",
  "precio": "La cantidad debe ser al menos de $1000",
  "stockBodega": "El stock de bodega no puede ser inferior a 1",
  "stock": "El stock no puede ser inferior a 1",
  "nombre": "El campo de nombre es obligatorio",
  "categoriaId": "El campo de categoria es obligatorio"
}
```
- **Error por categoría no existente**:
```json
{
  "mensaje": "La categoria con la id 2 no existe",
  "detalles": "La categoria no fue encontrada, Revise la id e intente nuevamente o cree una nueva categoria"
}
```

> ⚠️ **Importante:** Debes crear una categoría previamente en el microservicio de categorías.

### 🔹 PUT /products/update/{id}
- **Campos requeridos**: mismos que en POST
- **Respuesta exitosa**: `200 OK`
- **Errores**: igual que en POST → `400 Bad Request`

### 🔹 PUT /products/stock/{id}/update/{stock}
- **Respuesta esperada**: `{ "stock": 50 }` → `200 OK`
- **Errores**:
  - Producto no encontrado → `404`
  - Stock no proporcionado → `404`

### 🔹 DELETE /products/delete/{id}
- **Respuesta exitosa**: `"El producto fue eliminado correctamente"` → `200 OK`
- **Error**:
```json
{
  "mensaje": "El producto con la id 2 no existe",
  "detalles": "El producto no pudo ser encontrado, por favor revisa si la id es correcta"
}
```
→ `404 Not Found`

---

## 🗃️ Módulo: Inventory

### 🔹 GET /inventory
- **Respuesta**:
```json
[
  {
    "marca": "generic",
    "codigo": "gg-001",
    "nombre": "Martillo",
    "precio": 10000,
    "stock": 10,
    "descripcion": "Martillo generico prueba",
    "stock_bodega": 100,
    "categoria_id": 1,
    "fecha_creacion": "2025-05-24"
  }
]
```
- **Código**: `200 OK`
- **Error de conexión**: `500 Internal Server Error`

### 🔹 GET /inventory/{id}
- **Respuesta**: igual a `/inventory`
- **Error**: `"El producto con la id 1 no existe"` → `400 Bad Request`

### 🔹 PUT /inventory/update/{id}/{stock}
- **Actualiza el stock de bodega**
- **Respuesta esperada**:
```json
{
  "stock_bodega": 50
}
```
- **Código**: `200 OK`
- **Error**: Producto no existe → `400 Bad Request`

### 🔹 PUT /inventory/transfer/{id}
- **Body requerido**:
```json
{ "cantidad": 10 }
```
- **Restricciones**: cantidad entre 1 y 9999
- **Errores**:
```json
{ "cantidad": "La cantidad no puede ser inferior a 1" }
{ "cantidad": "La cantidad no puede ser mayor a 9999" }
```

---

## ✅ Flujo recomendado para crear productos

1. Inicia el microservicio de **categorías** y crea una categoría.
2. Inicia el microservicio de **inventario** y crea los productos asociándolos a la categoría.
3. Verifica stock, transfiere desde bodega, y actualiza según sea necesario.

---
# 📦 Microservicio de Ventas

El microservicio de **ventas** es el núcleo del sistema, ya que coordina la interacción entre todos los demás microservicios (usuarios, inventario, pagos). Es importante leer cuidadosamente esta documentación antes de consumir o probar esta API.

---

## 🌐 URL Base

```
http://localhost:808x/api/v1/sales
```

> Reemplaza `808x` por el puerto correspondiente en tu entorno local.

---

## 📤 POST `/create`

Crea una nueva venta.

### Requisitos previos:

- Tener activos los demás microservicios (usuarios, inventario, pagos).
- Al menos 1 dato registrado en cada uno.

### Body esperado

```json
{
  "usuarioId": 1,
  "productos": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ],
  "metodoPago": "tarjeta credito",
  "moneda": "dolar"
}
```

- **usuarioId**: ID de un usuario existente.
- **productos**: Array de objetos con ID de producto y cantidad.
- **metodoPago**: Ej. `"tarjeta credito"`.
- **moneda**: `"dolar"`, `"euro"` o `"pesos"`.

### Respuesta Exitosa

```json
Venta creada correctamente, para continuar con el pago dirigete a la URL:
http://localhost:8085/pago?id=1
```
- 🔁 El fragmento `/pago?id=1` siempre se mantiene, lo que cambia es el puerto.

### Códigos de estado

| Código | Situación |
|--------|-----------|
| 201 Created | Venta creada correctamente |
| 404 Not Found | Usuario no encontrado |
| 400 Bad Request | Producto inválido o sin stock / cantidad menor a 1 |

#### Ejemplos de error

- Usuario no válido:

```json
{
  "mensaje": "El usuario con la id 2 no existe",
  "error": "El usuario no fue encontrado en la base de datos"
}
```

- Stock insuficiente:

```json
{
  "mensaje": "El producto con id 1 no tiene stock suficiente",
  "error": "El stock no es suficiente, por favor revise la cantidad solicitada o revise el stock del producto"
}
```

- Producto no válido:

```json
{
  "mensaje": "No se pudo obtener el producto con la id 3",
  "error": "Ocurrió un error al intentar obtener el producto del microservicio de inventario, revise la solicitud e intente nuevamente"
}
```

- Cantidad inválida:

```json
{
  "productos[0].cantidad": "La cantidad no puede ser menor a 1"
}
```

---

## 📥 GET `/`

Retorna todas las ventas registradas.

### Respuesta exitosa

```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "buyOrder": "fm-1",
    "metodoPago": "tarjeta credito",
    "moneda": "dolar",
    "estado": "EN_PROCESO",
    "total": 10000,
    "fechaVenta": "2025-05-25T00:04:56.217332"
  }
]
```

- Si no hay datos: `[]`
- Código: `204 No Content` o `200 OK`
- Error del servidor (microservicio caído): `500 Internal Server Error`

---

## 📥 GET `/{id}`

Consulta una venta específica.

### Respuesta exitosa

```json
{
  "id": 1,
  "usuarioId": 1,
  "buyOrder": "fm-1",
  "metodoPago": "tarjeta credito",
  "moneda": "dolar",
  "estado": "EN_PROCESO",
  "total": 10000,
  "fechaVenta": "2025-05-25T00:14:06.838093",
  "detalle": [
    {
      "productoId": 1,
      "nombre": "Martillo",
      "cantidad": 1,
      "precioUnitario": 10000,
      "subTotal": 10000
    }
  ]
}
```

- Si no se encuentra la venta:

```json
{
  "mensaje": "La venta con la id 3 no existe",
  "error": "La venta no fue encontrada, revisa la base de datos si ya existe o cree la venta"
}
```

---

## 💳 Integración de Pago

Al crear una venta, se genera un link para realizar el pago mediante Webpay o PayPal.

### Métodos:

- **Webpay**: Pagos nacionales (Chile).
- **PayPal**: Pagos internacionales.

> Al seleccionar PayPal, el total se convierte automáticamente a la divisa solicitada.

### ⚠️ Importante

Las credenciales de PayPal **no están en el repositorio** por razones de seguridad. Puedes pedírmelas o generar las tuyas propias desde el sandbox de [PayPal Developer](https://developer.paypal.com/).

### 🧪 Tarjetas de prueba Webpay

| Tipo tarjeta | Número | CVV | Fecha exp. | Resultado |
|--------------|--------|-----|------------|-----------|
| VISA         | 4051 8856 0044 6623 | 123 | Cualquiera | Aprobada |
| AMEX         | 3700 0000 0002 032  | 1234| Cualquiera | Aprobada |

- Si solicita formulario con RUT y contraseña, usar:
  - **RUT**: 11.111.111-1
  - **Clave**: 123

---

## 🔄 Lógica de negocio

- Al completar una venta correctamente:
  - El stock de productos se actualiza en el microservicio de inventario.
  - El estado de la venta cambia de `EN_PROCESO` a `COMPLETADA`.

---

## 🛠️ Notas finales

- Este microservicio es **central y crítico** en la arquitectura del sistema.
- Si algún microservicio externo no está activo, puede generar errores al crear una venta.
