# Manual del Usuario (Visual) — MayoSalud

Este documento es una versión **más visual** del manual para empleados y administrador.

---

## 0) Antes de empezar (muy importante)

- **Cierre de sesión automático:** se desconecta tras **30 min** de inactividad.
- **Protección anti fuerza bruta:** tras **5 intentos fallidos** el usuario queda **bloqueado 15 min**.
- **Acceso por rol:** si no ves una opción, es porque tu rol no tiene permisos.

---

## 1) Roles del sistema

| Rol | ¿Quién es? | ¿Qué puede hacer? |
|---|---|---|
| **ADMIN** | Administrador | Gestiona Usuarios, Feriados y Auditoría. Controla todo. |
| **RECEPCION** | Recepción | Gestiona Turnos, Pacientes, y consulta agenda. |
| **MEDICO** | Médico | Consulta agenda y registra Evolución Médica + Estudios en la Historia Clínica. |
| **ENFERMERIA** | Enfermermería | Registra Signos Vitales en la Historia Clínica. |

---

## 2) Leyes implementadas (resumen)

### ✅ Ley 25.326 (Protección de Datos Personales)

- **Consentimiento informado obligatorio** para **nuevos pacientes**.
- **Política de privacidad** accesible desde la aplicación.
- **Accesos autenticados** y **auditoría** de operaciones.

### ✅ Ley 26.529 (Derechos del Paciente)

- Conservación de historia clínica mediante **baja lógica (soft delete)** en Pacientes.
- Los registros históricos no se eliminan físicamente (para preservar trazabilidad).

---

## 3) Manual rápido por módulo

### 🗓️ Turnos

#### Ver agenda
- Entrar a **Turnos**.
- Elegir fecha: **Anterior / Siguiente / Hoy**.

#### Cambiar estado del turno
(Desde la tarjeta en la agenda)
- **Pendiente → Confirmado**
- **Confirmado → Atendido**
- También se puede **Cancelar** o marcar **Ausente**.

#### Crear turno
- Botón **Nuevo turno**.
- Seleccionar **Paciente**, **Médico**, **Fecha y hora**.
- Guardar.

> Validación: el sistema evita **conflictos** (mismo médico y misma fecha/hora).

---

### 👥 Pacientes

#### Buscar
- En la barra de búsqueda: nombre o apellido.

#### Alta de paciente
- Completar datos personales + cobertura.
- **Marcar consentimiento informado (obligatorio)**.
- Guardar.

#### Editar paciente
- Ir a **Editar** (icono lápiz) y guardar.

#### Dar de baja (soft delete)
- Usar la acción **baja**.
- El paciente deja de aparecer en activos, pero queda preservado el historial.

---

### 🧑‍⚕️ Médicos

- Crear / editar / dar de baja.
- Duplicados: se valida **DNI** y **matrícula** únicos.

---

### 🏥 Clínica — Historia Clínica

#### Acceder
- Desde **Clínica** buscar paciente o abrir desde la agenda.

#### Enfermería: Signos Vitales
- Pestaña **Signos Vitales**.
- Completar datos y guardar.

#### Médico: Evolución médica
- Pestaña **Evolución médica**.
- Completar motivo/diagnóstico/tratamiento/observaciones.

#### Médico: Estudios
- Pestaña **Estudios**.
- Registrar tipo, estado (solicitado/realizado/informado/cancelado) y observaciones.

---

### 🔐 Usuarios (solo ADMIN)

- Crear usuario con rol.
- Editar / cambiar datos.
- Desactivar usuario si corresponde.

---

### 🧾 Feriados (solo ADMIN)

- Registrar feriados para que afecten la gestión de agenda.

---

### 🛡️ Auditoría (solo ADMIN)

- Ver historial de acciones del sistema.
- Filtrar por fechas y usuario.

Acciones registradas (ejemplos):
- CREAR / MODIFICAR / BAJA / ELIMINAR
- LOGIN_FALLIDO / BLOQUEADO

---

## 4) Recordatorios automáticos por email

- El sistema envía recordatorios **diarios a las 08:00** (hora Argentina).
- Se envía a pacientes con turno para el **día siguiente** en estado **Pendiente** o **Confirmado**.
- Se registra el resumen del envío en **Auditoría**.

---

## 5) Checklist final (para empleados)

- [ ] Ingreso con usuario propio (no compartir)
- [ ] Cerrar sesión al terminar
- [ ] Completar consentimiento informado al crear pacientes
- [ ] Registrar eventos clínicos (signos vitales / evolución / estudios)
- [ ] Si hay error o dato faltante: avisar al ADMIN

