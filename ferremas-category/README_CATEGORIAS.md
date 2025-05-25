# 📦 Microservicio de Categorías

Este microservicio forma parte de un sistema distribuido basado en microservicios para la gestión de productos. Su responsabilidad principal es manejar la información relacionada a las **categorías**, las cuales son indispensables para la creación y clasificación de productos dentro del sistema.

Este proyecto sigue los principios de **separación de responsabilidades**, **escalabilidad** y **autonomía de servicios**, permitiendo que cada microservicio opere de forma independiente y con su propia lógica de negocio.

---

## 🛠️ Tecnologías utilizadas

| Tecnología  | Descripción                         |
|-------------|-------------------------------------|
| Java        | Lenguaje de programación principal  |
| Spring Boot | Framework para desarrollo de APIs   |
| PostgreSQL  | Base de datos relacional            |
| Postman     | Herramienta para pruebas de endpoints|

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