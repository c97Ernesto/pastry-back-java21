# Malva Pastry Shop - Backend

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/PostgreSQL-13+-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Thymeleaf-3.x-green?style=for-the-badge&logo=thymeleaf" alt="Thymeleaf">
</p>

## Descripción

Sistema backend para la gestión de una pastelería artesanal. Este proyecto es una **refactorización completa y actualización** de un sistema anterior, modernizando la arquitectura y actualizando todas las dependencias a sus versiones más recientes.

### Cambios Principales en la Refactorización

| Aspecto | Versión Anterior | Versión Actual |
|---------|------------------|----------------|
| **Java** | 17 | **21** (LTS) |
| **Spring Boot** | 3.x | **4.0.1** |
| **Spring Security** | 6.x | **7.x** (incluido en SB 4.0) |
| **Hibernate** | 6.x | **7.2** |
| **Jakarta EE** | 9 | **11** |

### Mejoras Implementadas

- Migración a Spring Boot 4.0 con las últimas mejoras de rendimiento
- Actualización a Java 21 con soporte para Virtual Threads y Pattern Matching
- Nuevo sistema de autenticación con Spring Security 7
- Arquitectura modular preparada para microservicios
- Soporte para API pública (React) y panel admin (Thymeleaf)

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                     MALVA PASTRY SHOP                           │
├────────────────────────────┬────────────────────────────────────┤
│   PANEL ADMIN              │   API PÚBLICA                      │
│   (Thymeleaf + Sesión)     │   (REST + JWT + OAuth Google)      │
│   /login, /dashboard       │   /api/public/**                   │
│   /products, /categories   │   Consumida por React              │
├────────────────────────────┴────────────────────────────────────┤
│                 Spring Boot 4.0 + Spring Security               │
├─────────────────────────────────────────────────────────────────┤
│                     PostgreSQL Database                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## Stack Tecnológico

### Backend
- **Framework:** Spring Boot 4.0.1
- **Lenguaje:** Java 21
- **Seguridad:** Spring Security 7
- **ORM:** Hibernate 7.2 / Spring Data JPA
- **Validación:** Jakarta Validation

### Frontend (Panel Admin)
- **Motor de plantillas:** Thymeleaf 3.x
- **Layout:** Thymeleaf Layout Dialect 3.3
- **Estilos:** CSS3 con Variables CSS

### Base de Datos
- **RDBMS:** PostgreSQL 13+

### Integraciones
- **Storage:** AWS S3 / MinIO (SDK 2.25.11)
- **OAuth:** Google API Client 2.2.0
- **JWT:** JJWT 0.11.5
- **Documentación:** SpringDoc OpenAPI 2.3.0

---

## Inicio Rápido

### Prerrequisitos

- Java 21+
- PostgreSQL 13+
- Maven 3.9+ (o usar el wrapper incluido)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/malva-pastry-shop.git
cd malva-pastry-shop/backend
```

### 2. Configurar Base de Datos

```sql
CREATE DATABASE malva_pastry_db;
```

### 3. Configurar Credenciales

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/malva_pastry_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### 4. Ejecutar la Aplicación

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 5. Acceder al Sistema

- **Panel Admin:** http://localhost:8080/login
- **Credenciales por defecto:**
  - Email: `admin@malva.com`
  - Password: `admin123`

---

## Estructura del Proyecto

```
src/main/java/com/malva_pastry_shop/backend/
├── config/                 # Configuración (Security, Seeder)
├── controller/
│   ├── admin/              # Controladores MVC (Thymeleaf)
│   └── api/                # Controladores REST (API Pública)
├── domain/
│   ├── auth/               # Entidades de autenticación
│   ├── product/            # Entidades de productos
│   └── common/             # Entidades base
├── dto/
│   ├── request/            # DTOs de entrada
│   └── response/           # DTOs de salida
├── repository/             # Repositorios JPA
├── service/                # Lógica de negocio
└── security/               # Componentes de seguridad

src/main/resources/
├── static/css/             # Estilos CSS
├── templates/
│   ├── layout/             # Layouts Thymeleaf
│   ├── auth/               # Vistas de autenticación
│   └── dashboard/          # Vistas del panel
└── application.properties  # Configuración
```

---

## Seguridad

### Panel de Administración (Thymeleaf)
- Autenticación basada en sesión
- Formulario de login con CSRF protection
- Roles: `ADMIN`, `EMPLOYEE`

### API Pública (REST)
- Autenticación JWT
- OAuth 2.0 con Google
- Endpoints públicos y protegidos

---

## Testing

```bash
# Ejecutar tests
.\mvnw.cmd test

# Ejecutar con cobertura
.\mvnw.cmd test jacoco:report
```

---

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## Autor

Desarrollado como parte del proyecto Malva Pastry Shop.
