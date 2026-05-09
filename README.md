# MayoSalud — Sistema de Historia Clínica Electrónica

Sistema de gestión clínica para **Clínica Mayo S.R.L.** que cubre el registro de pacientes, agenda de turnos, gestión de médicos y trazabilidad completa mediante auditoría.

---

## Tabla de contenidos

1. [Tecnologías](#tecnologías)
2. [Requisitos previos](#requisitos-previos)
3. [Configuración e instalación](#configuración-e-instalación)
4. [Ejecución](#ejecución)
5. [Usuarios y accesos](#usuarios-y-accesos)
6. [Módulos del sistema](#módulos-del-sistema)
7. [Seguridad](#seguridad)
8. [Arquitectura](#arquitectura)
9. [Estructura del proyecto](#estructura-del-proyecto)
10. [Base de datos](#base-de-datos)

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 3.2.0 · Java 17 |
| Persistencia | Spring Data JPA · Hibernate · MySQL 8.4 |
| Frontend | Thymeleaf 3.1 · Bootstrap 5.3 · Bootstrap Icons 1.11 |
| Seguridad | Spring Security 6 · Spring AOP |
| Utilidades | Lombok · Jakarta Validation · Commons IO |
| Build | Apache Maven 3.x |

---

## Requisitos previos

- **Java 17** o superior instalado y configurado en `PATH`
- **MySQL 8.x** corriendo en localhost puerto 3306
- **Maven 3.6+** (o usar el wrapper incluido `mvnw`)

---

## Configuración e instalación

### 1. Crear la base de datos

```sql
CREATE DATABASE mayosalud
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar credenciales

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mayosalud?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=utn123
```

Ajustar `username` y `password` según la instalación local de MySQL.

### 3. Esquema de base de datos

Las tablas se crean automáticamente al iniciar la aplicación gracias a `spring.jpa.hibernate.ddl-auto=update`. No se requiere ejecutar scripts SQL adicionales.

---

## Ejecución

```bash
# Con Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Con Maven instalado globalmente
mvn spring-boot:run
```

La aplicación queda disponible en: **http://localhost:8080**

---

## Usuarios y accesos

| Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|
| `admin` | `admin123` | `ADMIN` | Acceso completo + pantalla de Auditoría |
| `recepcion` | `mayo2024` | `RECEPCION` | Turnos, Pacientes, Médicos |

> **Nota de seguridad:** Cambiar estas credenciales antes de poner el sistema en producción. Los usuarios están definidos en `SecurityConfig.java` con contraseñas hasheadas en BCrypt.

---

## Módulos del sistema

### Inicio (`/`)
Panel principal con estadísticas del día: turnos programados, total de pacientes activos, total de médicos activos. Incluye tabla con la agenda del día actual.

### Agenda de Turnos (`/turnos`)
- Vista diaria con navegación por fecha (anterior / siguiente / Hoy)
- Cada turno se muestra como tarjeta con borde de color según estado
- Cambio de estado inline (Pendiente → Confirmado → Atendido / Cancelado / Ausente)
- Barra de resumen con conteo por estado
- Validación de conflicto de horario: no permite asignar dos turnos al mismo médico en la misma fecha y hora

**Estados posibles:**

| Estado | Color |
|---|---|
| Pendiente | Amarillo |
| Confirmado | Azul |
| Atendido | Verde |
| Cancelado | Rojo |
| Ausente | Gris |

### Pacientes (`/pacientes`)
- Alta, edición y baja lógica (soft delete — el registro se conserva en BD)
- Búsqueda por nombre o apellido
- Ficha completa con datos personales, cobertura médica (obra social / nro. afiliado), antecedentes y alergias
- Historial de turnos del paciente
- **Consentimiento informado obligatorio** al crear un nuevo paciente (Ley 25.326): se registra fecha y hora del consentimiento

**Obras sociales soportadas:** OSDE, Swiss Medical, Galeno, Medicus, Omint, IOMA, PAMI, Accord Salud, Particular.

### Médicos (`/medicos`)
- Alta, edición y baja lógica
- Búsqueda por nombre o apellido
- 31 especialidades médicas disponibles (ver enum `Especialidad`)
- Validación de DNI y matrícula únicos

### Auditoría (`/auditoria`) — Solo ADMIN
Registro automático de todas las operaciones del sistema:

| Acción | Color | Descripción |
|---|---|---|
| CREAR | Verde | Alta de un registro |
| MODIFICAR | Azul | Edición de datos |
| BAJA | Rojo | Baja lógica |
| ELIMINAR | Rojo | Eliminación física (turnos) |
| LOGIN_FALLIDO | Amarillo | Intento de acceso fallido |
| BLOQUEADO | Rojo | Cuenta bloqueada por intentos fallidos |

Filtros disponibles: rango de fechas, nombre de usuario.

### Política de Privacidad (`/politica-privacidad`)
Página accesible sin login. Describe el tratamiento de datos según Ley 25.326 (Protección de Datos Personales) y Ley 26.529 (Derechos del Paciente).

---

## Seguridad

### Autenticación
- Formulario de login en `/login`
- Sesión con tiempo de expiración de **30 minutos** de inactividad
- Al expirar la sesión redirige a `/login?timeout` con mensaje informativo

### Protección contra ataques de fuerza bruta
- Se registra cada intento fallido de login (en memoria y en auditoría)
- Tras **5 intentos fallidos consecutivos** el usuario queda bloqueado por **15 minutos**
- Mientras está bloqueado, cualquier intento redirige a `/login?bloqueado`
- El bloqueo se libera automáticamente al vencer el tiempo; un login exitoso resetea el contador

### Autorización
- `/auditoria/**` requiere rol `ADMIN`
- `/politica-privacidad` y `/login` son públicos
- Todos los demás endpoints requieren autenticación

### Auditoría automática (AOP)
Implementada con Spring AOP (`@Aspect`). Los aspectos interceptan los métodos de servicio sin modificar el código de negocio:
- `@Around` en `guardar()`: detecta si es creación o modificación inspeccionando el `id` antes de ejecutar
- `@AfterReturning` en `darDeBaja()`, `eliminar()` y `cambiarEstado()`

---

## Arquitectura

```
Navegador
    │
    ▼
Controllers  ──→  Services  ──→  Repositories  ──→  MySQL
    │                │
    │          AuditoriaAspect (AOP)
    │                │
    │          AuditoriaService ──→ auditoria_logs
    │
Thymeleaf Templates (Bootstrap 5)
```

### Capas

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Entidades | `entity` | Mapeo JPA con validaciones Jakarta |
| Repositorios | `repository` | Acceso a datos (Spring Data JPA) |
| Servicios | `service` | Lógica de negocio y validaciones |
| Controladores | `controller` | Manejo de requests HTTP y binding de vistas |
| Configuración | `config` | Security, AOP, advice global |
| Vistas | `resources/templates` | HTML con Thymeleaf + Bootstrap 5 |

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/ar/com/mayosalud/
    │   ├── App.java                          # Entry point Spring Boot
    │   ├── entity/
    │   │   ├── Paciente.java
    │   │   ├── Medico.java
    │   │   ├── Turno.java
    │   │   ├── AuditoriaLog.java
    │   │   ├── Especialidad.java             # Enum: 31 especialidades
    │   │   ├── ObraSocial.java               # Enum: 9 obras sociales
    │   │   └── EstadoTurno.java              # Enum: 5 estados de turno
    │   ├── repository/
    │   │   ├── PacienteRepository.java
    │   │   ├── MedicoRepository.java
    │   │   ├── TurnoRepository.java
    │   │   └── AuditoriaLogRepository.java
    │   ├── service/
    │   │   ├── PacienteService.java
    │   │   ├── MedicoService.java
    │   │   ├── TurnoService.java
    │   │   ├── AuditoriaService.java
    │   │   └── LoginAttemptService.java      # Control de intentos fallidos (en memoria)
    │   ├── controller/
    │   │   ├── HomeController.java
    │   │   ├── PacienteController.java
    │   │   ├── MedicoController.java
    │   │   ├── TurnoController.java
    │   │   ├── AuditoriaController.java
    │   │   └── PoliticaPrivacidadController.java
    │   └── config/
    │       ├── SecurityConfig.java           # Reglas de acceso + usuarios
    │       ├── AuditoriaAspect.java          # Interceptores AOP
    │       ├── GlobalControllerAdvice.java   # Inyecta requestURI en todos los modelos
    │       ├── CustomAuthFailureHandler.java # Manejo de login fallido
    │       └── CustomAuthSuccessHandler.java # Reset de contador al hacer login exitoso
    └── resources/
        ├── application.properties
        ├── static/images/logo.png
        └── templates/
            ├── layout.html                   # Layout base con sidebar y navbar
            ├── login.html
            ├── home.html
            ├── politica-privacidad.html
            ├── paciente/
            │   ├── lista.html
            │   ├── form.html
            │   └── detalle.html
            ├── medico/
            │   ├── lista.html
            │   └── form.html
            ├── turno/
            │   ├── agenda.html
            │   └── form.html
            └── auditoria/
                └── lista.html
```

---

## Base de datos

### Tablas generadas automáticamente

| Tabla | Descripción |
|---|---|
| `pacientes` | Datos personales, cobertura médica, consentimiento |
| `medicos` | Datos del profesional y especialidad |
| `turnos` | Relación paciente-médico con fecha, hora y estado |
| `auditoria_logs` | Registro completo de operaciones del sistema |

### Campos clave

**pacientes**
- `consentimiento_fecha` — timestamp del consentimiento informado (Ley 25.326)
- `activo` — `false` cuando el paciente fue dado de baja (soft delete)

**medicos**
- `activo` — `false` cuando el médico fue dado de baja (soft delete)
- `especialidad` — almacenado como String (nombre del enum)

**auditoria_logs**
- `accion` — CREAR / MODIFICAR / BAJA / ELIMINAR / LOGIN_FALLIDO / BLOQUEADO
- `entidad` — Paciente / Medico / Turno / LOGIN
- `usuario` — nombre del usuario que realizó la operación
- `ip` — dirección IP del cliente

---

## Consideraciones legales

El sistema implementa los requisitos de:
- **Ley 25.326** — Protección de Datos Personales: consentimiento informado obligatorio, política de privacidad accesible, datos protegidos con autenticación y auditoría.
- **Ley 26.529** — Derechos del Paciente: historia clínica como propiedad del paciente, datos conservados con identificación permanente (soft delete).

---

*MayoSalud v1.0.0 · Clínica Mayo S.R.L.*
