# Manual de Usuario — MayoSalud
**Clínica Mayo S.R.L. · La Paz, Entre Ríos**
*Tel: 422237 · WhatsApp: 3437 402962*

---

## Índice

1. [Introducción](#1-introducción)
2. [Acceso al sistema](#2-acceso-al-sistema)
3. [Roles y permisos](#3-roles-y-permisos)
4. [Pantalla de Inicio](#4-pantalla-de-inicio)
5. [Módulo Turnos](#5-módulo-turnos)
   - 5.1 [Ver la agenda del día](#51-ver-la-agenda-del-día)
   - 5.2 [Crear un nuevo turno](#52-crear-un-nuevo-turno)
   - 5.3 [Cambiar el estado de un turno](#53-cambiar-el-estado-de-un-turno)
   - 5.4 [Editar o cancelar un turno](#54-editar-o-cancelar-un-turno)
   - 5.5 [Navegar entre fechas](#55-navegar-entre-fechas)
6. [Módulo Pacientes](#6-módulo-pacientes)
   - 6.1 [Buscar un paciente](#61-buscar-un-paciente)
   - 6.2 [Registrar un nuevo paciente](#62-registrar-un-nuevo-paciente)
   - 6.3 [Editar datos de un paciente](#63-editar-datos-de-un-paciente)
   - 6.4 [Ver ficha completa del paciente](#64-ver-ficha-completa-del-paciente)
   - 6.5 [Dar de baja un paciente](#65-dar-de-baja-un-paciente)
7. [Módulo Médicos](#7-módulo-médicos)
   - 7.1 [Registrar un médico](#71-registrar-un-médico)
   - 7.2 [Editar o dar de baja un médico](#72-editar-o-dar-de-baja-un-médico)
8. [Módulo Clínica — Historia Clínica](#8-módulo-clínica--historia-clínica)
   - 8.1 [Acceder a la ficha clínica de un paciente](#81-acceder-a-la-ficha-clínica-de-un-paciente)
   - 8.2 [Registrar signos vitales (Enfermería)](#82-registrar-signos-vitales-enfermería)
   - 8.3 [Registrar evolución médica (Médico)](#83-registrar-evolución-médica-médico)
   - 8.4 [Cargar un estudio (Médico)](#84-cargar-un-estudio-médico)
   - 8.5 [Línea de tiempo clínica](#85-línea-de-tiempo-clínica)
9. [Módulo Usuarios](#9-módulo-usuarios)
10. [Módulo Feriados](#10-módulo-feriados)
11. [Módulo Auditoría](#11-módulo-auditoría)
12. [Recordatorios automáticos de turno](#12-recordatorios-automáticos-de-turno)
13. [Seguridad y buenas prácticas](#13-seguridad-y-buenas-prácticas)
14. [Preguntas frecuentes](#14-preguntas-frecuentes)

---

## 1. Introducción

**MayoSalud** es el sistema de gestión clínica de **Clínica Mayo S.R.L.** Permite administrar la agenda de turnos, el registro de pacientes y médicos, y la historia clínica electrónica de cada paciente.

El sistema funciona desde cualquier navegador web (Chrome, Firefox, Edge) con conexión a internet. No requiere instalación en la computadora.

**Dirección de acceso:** la URL que le indique el administrador del sistema (puede ser local o en la nube).

---

## 2. Acceso al sistema

### Iniciar sesión

1. Ingresar la dirección del sistema en el navegador.
2. Completar **Usuario** y **Contraseña**.
3. Hacer clic en **Ingresar**.

> Si olvidó su contraseña, comuníquese con el administrador del sistema.

### Cierre de sesión automático

La sesión se cierra automáticamente después de **30 minutos de inactividad**. Al volver a la pantalla aparecerá el mensaje *"Su sesión ha expirado"*. Ingrese nuevamente con su usuario y contraseña.

### Bloqueo por intentos fallidos

Tras **5 intentos fallidos consecutivos** el usuario queda bloqueado durante **15 minutos**. Pasado ese tiempo se desbloquea automáticamente.

### Cerrar sesión manualmente

Hacer clic en el botón **Salir** (esquina superior derecha de la barra de navegación).

---

## 3. Roles y permisos

El sistema tiene cuatro roles. Cada usuario tiene asignado uno de ellos.

| Rol | Descripción | Accesos |
|---|---|---|
| **ADMIN** | Administrador del sistema | Acceso completo a todos los módulos |
| **RECEPCION** | Personal de recepción | Turnos, lista de pacientes, lista de médicos |
| **MEDICO** | Médico de la clínica | Agenda (solo lectura), historia clínica (evolución y estudios) |
| **ENFERMERIA** | Personal de enfermería | Agenda (solo lectura), historia clínica (signos vitales) |

> **Importante:** si al intentar acceder a una sección aparece el mensaje *"Acceso denegado"*, significa que su rol no tiene permiso para esa función. Consulte con el administrador.

---

## 4. Pantalla de Inicio

Al ingresar al sistema se muestra el **panel principal** con:

- **Tarjetas de estadísticas:** total de pacientes registrados, turnos del día, médicos activos y último turno registrado.
- **Contadores rápidos:** turnos del día agrupados por estado (Pendientes, Atendidos, Cancelados, Confirmados).
- **Agenda de hoy:** tabla con todos los turnos del día actual.
  - El personal de **Recepción y Admin** ve el botón de edición del turno (ícono lápiz).
  - El personal de **Médico y Enfermería** ve el botón para abrir la ficha clínica del paciente (ícono planilla).

El reloj digital en la barra superior muestra la hora actual actualizada en tiempo real.

---

## 5. Módulo Turnos

Acceso desde el menú lateral: **Turnos**

### 5.1 Ver la agenda del día

La pantalla muestra los turnos del día seleccionado con:
- Hora del turno
- Nombre del paciente
- Médico asignado
- Motivo de consulta
- Estado actual del turno (color identificador)

**Estados de turno:**

| Estado | Color |
|---|---|
| Pendiente | Amarillo |
| Confirmado | Azul |
| Atendido | Verde |
| Cancelado | Rojo |
| Ausente | Gris |

### 5.2 Crear un nuevo turno

1. Hacer clic en **Nuevo turno** (botón azul, arriba a la derecha).
2. Completar los campos:
   - **Paciente:** buscar por nombre o apellido.
   - **Médico:** seleccionar de la lista.
   - **Fecha y hora:** ingresar la fecha y seleccionar el horario disponible.
   - **Motivo de consulta:** opcional.
3. Hacer clic en **Guardar**.

> Si el médico ya tiene un turno en ese horario, el sistema mostrará un aviso de conflicto y no permitirá guardar.

### 5.3 Cambiar el estado de un turno

Desde la tarjeta de turno en la agenda, usar los botones de acción rápida:
- **Confirmar** → pasa a estado Confirmado (azul)
- **Atender** → pasa a estado Atendido (verde)
- **Cancelar** → pasa a estado Cancelado (rojo)
- **Ausente** → pasa a estado Ausente (gris)

### 5.4 Editar o cancelar un turno

1. Hacer clic en el ícono de **lápiz** sobre el turno.
2. Modificar los datos necesarios.
3. Hacer clic en **Guardar**.

Para eliminar un turno hacer clic en el ícono de **papelera** (disponible para ADMIN).

### 5.5 Navegar entre fechas

Usar los botones **← Anterior** y **Siguiente →** para moverse entre días.
El botón **Hoy** vuelve a la fecha actual.

---

## 6. Módulo Pacientes

Acceso desde el menú lateral: **Pacientes**  
*(disponible para ADMIN y RECEPCION)*

### 6.1 Buscar un paciente

En la barra de búsqueda ingresar el nombre o apellido del paciente. La lista se filtra automáticamente.

### 6.2 Registrar un nuevo paciente

1. Hacer clic en **Nuevo paciente**.
2. Completar los datos personales:
   - Apellido y nombre (obligatorios)
   - DNI (obligatorio, único en el sistema)
   - Fecha de nacimiento, sexo, dirección, teléfono, email
3. Completar la cobertura médica:
   - **Obra social:** escribir las primeras letras y seleccionar de la lista desplegable.
   - Nro. de afiliado (si corresponde)
4. Completar antecedentes y alergias (opcional).
5. Marcar el **consentimiento informado** (obligatorio para nuevos pacientes, requerido por Ley 25.326).
6. Hacer clic en **Guardar**.

> El sistema registra automáticamente la fecha y hora en que se otorgó el consentimiento.

### 6.3 Editar datos de un paciente

1. Buscar el paciente en la lista.
2. Hacer clic en el ícono de **lápiz**.
3. Modificar los datos necesarios.
4. Hacer clic en **Guardar**.

### 6.4 Ver ficha completa del paciente

Hacer clic en el nombre del paciente o en el ícono de **detalle** para ver:
- Datos personales completos
- Cobertura médica
- Historial de turnos
- Enlace a la historia clínica

### 6.5 Dar de baja un paciente

Hacer clic en el ícono de **baja** sobre el paciente. El registro se conserva en el sistema (baja lógica) pero no aparece en la lista activa. Esto garantiza el historial clínico conforme a la Ley 26.529.

---

## 7. Módulo Médicos

Acceso desde el menú lateral: **Médicos**  
*(disponible para ADMIN)*

### 7.1 Registrar un médico

1. Hacer clic en **Nuevo médico**.
2. Completar apellido, nombre, DNI, matrícula y especialidad.
3. Hacer clic en **Guardar**.

> El DNI y la matrícula deben ser únicos. El sistema no permite duplicados.

### 7.2 Editar o dar de baja un médico

- **Editar:** clic en el ícono de lápiz → modificar datos → Guardar.
- **Dar de baja:** clic en el ícono de baja. El médico deja de aparecer en el selector de turnos pero sus registros históricos se conservan.

---

## 8. Módulo Clínica — Historia Clínica

Acceso desde el menú lateral: **Clínica**  
*(disponible para ADMIN, MEDICO y ENFERMERIA)*

La historia clínica registra todos los eventos médicos del paciente de forma cronológica.

### 8.1 Acceder a la ficha clínica de un paciente

**Desde el menú Clínica:**
1. Buscar el paciente por nombre o apellido.
2. Hacer clic en el nombre para abrir la ficha.

**Desde la agenda del día (MEDICO / ENFERMERIA):**
- Hacer clic en el ícono de **planilla clínica** junto al turno del paciente.

### 8.2 Registrar signos vitales (Enfermería)

> Disponible para **ADMIN** y **ENFERMERIA**.

1. Abrir la ficha clínica del paciente.
2. Seleccionar la pestaña **Signos Vitales**.
3. Completar los campos disponibles (todos opcionales):
   - Temperatura (°C)
   - Presión sistólica y diastólica (mmHg)
   - Pulso / frecuencia cardíaca (lpm)
   - Saturación de oxígeno (%)
   - Peso (kg) y talla (cm)
   - Observaciones
4. Hacer clic en **Guardar signos vitales**.

El registro queda guardado con fecha, hora y nombre del usuario que lo ingresó.

### 8.3 Registrar evolución médica (Médico)

> Disponible para **ADMIN** y **MEDICO**.

1. Abrir la ficha clínica del paciente.
2. Seleccionar la pestaña **Evolución Médica**.
3. Completar:
   - Motivo de consulta
   - Diagnóstico presuntivo / definitivo
   - Tratamiento indicado
   - Observaciones adicionales
4. Hacer clic en **Guardar evolución**.

### 8.4 Cargar un estudio (Médico)

> Disponible para **ADMIN** y **MEDICO**.

1. Abrir la ficha clínica del paciente.
2. Seleccionar la pestaña **Estudios**.
3. Completar:
   - Tipo de estudio
   - Estado: Solicitado / Realizado / Informado / Cancelado
   - Observaciones
4. Hacer clic en **Guardar estudio**.

### 8.5 Línea de tiempo clínica

En la parte inferior de la ficha clínica se muestra una **línea de tiempo** con los últimos 20 eventos registrados del paciente, ordenados del más reciente al más antiguo.

Cada evento indica:
- Tipo (Signos Vitales, Evolución Médica o Estudio) con color identificador
- Fecha y hora
- Usuario que lo registró
- Resumen del contenido

---

## 9. Módulo Usuarios

Acceso desde el menú lateral: **Usuarios**  
*(disponible solo para ADMIN)*

Permite crear y gestionar las cuentas de acceso al sistema.

**Crear un usuario:**
1. Hacer clic en **Nuevo usuario**.
2. Completar nombre de usuario, nombre completo, contraseña y rol.
3. Hacer clic en **Guardar**.

**Cambiar contraseña o rol:** hacer clic en el ícono de edición del usuario.

**Desactivar un usuario:** hacer clic en el ícono de baja. El usuario no podrá iniciar sesión.

> **Buena práctica:** crear un usuario personal para cada empleado. No compartir cuentas.

---

## 10. Módulo Feriados

Acceso desde el menú lateral: **Feriados**  
*(disponible solo para ADMIN)*

Permite registrar los días feriados para que el sistema los tenga en cuenta al gestionar la agenda.

---

## 11. Módulo Auditoría

Acceso desde el menú lateral: **Auditoría**  
*(disponible solo para ADMIN)*

Muestra un registro completo de todas las operaciones realizadas en el sistema:

| Acción | Descripción |
|---|---|
| CREAR | Alta de un registro |
| MODIFICAR | Edición de datos |
| BAJA | Baja lógica de un registro |
| ELIMINAR | Eliminación de un turno |
| LOGIN_FALLIDO | Intento de acceso con credenciales incorrectas |
| BLOQUEADO | Cuenta bloqueada por exceso de intentos fallidos |

**Filtros disponibles:** rango de fechas y nombre de usuario.

Cada registro muestra: fecha y hora, usuario, acción, entidad afectada y dirección IP desde donde se realizó la operación.

---

## 12. Recordatorios automáticos de turno

El sistema envía automáticamente un **correo electrónico** a cada paciente con turno confirmado o pendiente para el día siguiente.

- **Horario de envío:** todos los días a las 8:00 hs (hora Argentina).
- **Remitente:** Clínica Mayo S.R.L. (clinicamayolp@gmail.com)
- **Contenido:** fecha, hora, médico, especialidad y motivo del turno.

> Para que el recordatorio llegue, el paciente debe tener un email registrado en su ficha. Si el campo email está vacío, no se envía el correo.

---

## 13. Seguridad y buenas prácticas

- **No comparta su contraseña** con otras personas.
- **Cierre sesión** al terminar de trabajar, especialmente en computadoras compartidas.
- Si sospecha que alguien usó su cuenta sin autorización, comuníquelo al administrador de inmediato.
- El sistema registra todas las operaciones con el nombre del usuario responsable y la dirección IP.
- Las contraseñas se almacenan cifradas: ni el administrador puede ver su contraseña actual.
- **Cambie su contraseña** periódicamente desde el módulo Usuarios.

---

## 14. Preguntas frecuentes

**¿Por qué no puedo ver el menú de Pacientes?**
Su rol no tiene acceso a esa sección. El menú de Pacientes está disponible para ADMIN y RECEPCION.

**¿Por qué no puedo ingresar un turno nuevo?**
Solo ADMIN y RECEPCION pueden crear y editar turnos. MEDICO y ENFERMERIA tienen acceso de solo lectura a la agenda.

**¿El sistema perdió mis datos al cerrarse?**
La sesión expira a los 30 minutos de inactividad. Los datos guardados antes del cierre están a salvo. Los datos no guardados (formularios sin confirmar) se pierden.

**¿Puedo recuperar un paciente dado de baja?**
Sí, el administrador puede reactivar un paciente dado de baja desde el módulo de Usuarios o contactando al soporte técnico.

**¿Qué hago si el sistema no responde?**
Verificar la conexión a internet. Si el problema persiste, comunicarse con el administrador del sistema.

**¿Cómo sé si el recordatorio de turno llegó al paciente?**
El módulo de Auditoría registra el resultado del envío diario de recordatorios con el detalle de cuántos correos se enviaron, cuántos pacientes no tenían email y si hubo errores.

---

*MayoSalud — Clínica Mayo S.R.L. · La Paz, Entre Ríos*
*Tel: 422237 · WhatsApp: 3437 402962*
