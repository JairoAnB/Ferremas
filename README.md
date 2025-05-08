# FERREMAS - Sistema de Consulta RESTful para Red de Ferreterías

FERREMAS es un proyecto semestral desarrollado para la asignatura **Integración de Plataformas** en el instituto **Duoc UC**. Este sistema simula la solución tecnológica de una red de ferreterías con múltiples sucursales físicas, enfocándose principalmente en la integración de plataformas mediante **APIs RESTful** construidas en **Java con Spring Boot**.

## :dart: Contexto

Esta entrega corresponde al **Paso 2** del proyecto semestral, parte de una evaluación grupal, se construyó una **API RESTful** que permite consultar información detallada de productos, como nombre, código, marca, precio, stock y categoría. Esta API tiene un enfoque de doble integración:

- :arrows_counterclockwise: **Consumo interno**: Para que las sucursales puedan consultar el catálogo y realizar pedidos.
- :globe_with_meridians: **Consumo externo**: Para que otras tiendas o desarrolladores externos puedan integrarse fácilmente.

> :bulb: Además, se proyecta a futuro integrar pagos con **Webpay** y una conversión de divisas en tiempo real con la **API del Banco Central de Chile**.

---

## :toolbox: Tecnologías utilizadas

| Herramienta | Descripción |
|-------------|-------------|
| ![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2.x-brightgreen?logo=spring-boot) | Framework principal para el backend |
| ![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk) | Lenguaje utilizado |
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql) | Base de datos relacional |
| ![Lombok](https://img.shields.io/badge/Lombok-%E2%9C%93-yellow) | Anotaciones para simplificar el código |
| ![Postman](https://img.shields.io/badge/Postman-API%20Testing-orange?logo=postman) | Herramienta para probar las APIs |
| Maven | Sistema de gestión de dependencias |

---
