# Manual de Usuario — MayoSalud
**Clínica Mayo S.R.L. · La Paz, Entre Ríos**
*Tel: 422237 · WhatsApp: 3437 402962*

*Desarrollo y documentación: Ana Carolina Martin · CM | Systems*

---

## Índice

1. [Introducción](#1-introducción)
2. [Acceso al sistema](#2-acceso-al-sistema)
3. [Roles y permisos](#3-roles-y-permisos)
4. [Pantalla de Inicio](#4-pantalla-de-inicio)
5. [Módulo Agenda / Turnos](#5-módulo-agenda--turnos)
   - 5.1 [Vistas por rol: Agenda General, Mi Agenda, Turnos del Día](#51-vistas-por-rol)
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
8. [Módulo Atención del Paciente](#8-módulo-atención-del-paciente)
   - 8.1 [Acceder a la Ficha Asistencial](#81-acceder-a-la-ficha-asistencial)
   - 8.2 [Registrar signos vitales (Enfermería)](#82-registrar-signos-vitales-enfermería)
   - 8.3 [Registrar evolución médica (Médico)](#83-registrar-evolución-médica-médico)
   - 8.4 [Cargar un estudio (Médico)](#84-cargar-un-estudio-médico)
   - 8.5 [Línea de tiempo asistencial](#85-línea-de-tiempo-asistencial)
9. [Módulo Usuarios](#9-módulo-usuarios)
10. [Módulo Feriados](#10-módulo-feriados)
11. [Módulo Auditoría](#11-módulo-auditoría)
12. [Recordatorios automáticos de turno](#12-recordatorios-automáticos-de-turno)
13. [Seguridad y buenas prácticas](#13-seguridad-y-buenas-prácticas)
14. [Preguntas frecuentes](#14-preguntas-frecuentes)
15. [Portal del Paciente — acceso público](#15-portal-del-paciente--acceso-público)
16. [Módulo Horarios de Atención](#16-módulo-horarios-de-atención)
17. [Sitio Web Institucional](#17-sitio-web-institucional)
18. [Funcionalidades futuras](#18-funcionalidades-futuras)

---

## 1. Introducción

**MayoSalud** es la **plataforma de gestión clínica y atención digital** de **Clínica Mayo S.R.L.** Permite administrar la agenda de turnos, el registro de pacientes y médicos, y el seguimiento asistencial de cada paciente.

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

| Rol | Descripción | Accesos principales | Restricciones |
|---|---|---|---|
| **ADMIN** | Administrador del sistema | Usuarios, médicos, auditoría, agenda, pacientes, Atención del Paciente, configuración | — |
| **RECEPCION** | Personal de recepción | Agenda General, pacientes administrativos, médicos (lectura) | Sin acceso a Atención del Paciente ni registros asistenciales |
| **MEDICO** | Médico de la clínica | Mi Agenda (solo sus turnos), Atención del Paciente, Ficha Asistencial (evolución y estudios) | No crea, edita ni cancela turnos |
| **ENFERMERIA** | Personal de enfermería | Turnos del Día (referencia), Atención del Paciente, Ficha Asistencial (signos vitales) | No crea, edita ni cancela turnos; no carga evolución médica |

**MEDICO:** puede consultar la Ficha Asistencial de cualquier paciente activo de la institución para atención, interconsulta, guardia, reemplazo o continuidad asistencial. Puede ver signos vitales registrados, pero no cargarlos.

**RECEPCION:** gestiona toda la operativa de agenda pero no tiene acceso a registros clínicos. Esta separación protege la confidencialidad asistencial.

> **Importante:** si al intentar acceder a una sección aparece el mensaje *"Acceso denegado"*, significa que su rol no tiene permiso para esa función. Consulte con el administrador.

---

## 4. Pantalla de Inicio

Al ingresar, el sistema muestra un **panel de inicio personalizado según el rol**:

| Rol | Nombre del panel | Contenido principal |
|---|---|---|
| **ADMIN** | Panel de Administración | Estadísticas generales, agenda del día, accesos rápidos a todos los módulos |
| **RECEPCION** | Panel de Recepción | Agenda del día, contadores de turnos por estado, acceso rápido a nuevo turno y a pacientes |
| **MEDICO** | Panel de Atención Médica | Sus turnos del día, acceso directo a Atención del Paciente |
| **ENFERMERIA** | Bandeja de Enfermería | Turnos del día como referencia, acceso directo a Ficha Asistencial por paciente |

- El personal de **ADMIN y RECEPCION** ve el selector de estado y el ícono de edición en cada turno.
- El personal de **MEDICO y ENFERMERIA** ve el ícono de Ficha Asistencial en cada turno.

El reloj digital en la barra superior muestra la hora actual actualizada en tiempo real.

---

## 5. Módulo Agenda / Turnos

El menú lateral muestra un nombre diferente según el rol:

| Rol | Nombre en el menú | Vista |
|---|---|---|
| ADMIN / RECEPCION | Agenda General | Todos los turnos de la clínica |
| MEDICO | Mi Agenda | Solo los turnos asignados al médico autenticado |
| ENFERMERIA | Turnos del Día | Turnos del día como referencia de flujo asistencial |

Cada vista muestra un mensaje de orientación contextual al pie del encabezado.

### 5.1 Vistas por rol

**Agenda General (ADMIN / RECEPCION)**

Muestra todos los turnos del día seleccionado para todos los médicos. Permite crear, editar, cambiar estado y eliminar turnos. Incluye resumen de contadores por estado (Pendientes, Confirmados, Atendidos, Cancelados/Ausentes) y acceso al menú de impresión.

La pantalla muestra el mensaje: *"Desde aquí se gestiona la agenda general de turnos de la clínica."*

**Mi Agenda (MEDICO)**

Muestra únicamente los turnos del día asignados al médico autenticado. Es de solo lectura para la gestión de turnos. Desde cada turno se puede abrir la Ficha Asistencial del paciente.

La pantalla muestra el mensaje: *"Vista de tus turnos asignados. Para consultar información asistencial, usá Atención del Paciente."*

**Turnos del Día (ENFERMERIA)**

Muestra los turnos del día como referencia para preparar pacientes y registrar signos vitales. Es de solo lectura para la gestión de turnos. Desde cada turno se puede abrir la Ficha Asistencial.

La pantalla muestra el mensaje: *"Vista de turnos del día como referencia para preparar pacientes y cargar signos vitales."*

**Estados de turno:**

| Estado | Color |
|---|---|
| Pendiente | Amarillo |
| Confirmado | Azul |
| Atendido | Verde |
| Cancelado | Rojo |
| Ausente | Gris |

### 5.2 Crear un nuevo turno

> **Solo ADMIN y RECEPCION** pueden crear nuevos turnos. MEDICO y ENFERMERIA no tienen acceso al formulario de nuevo turno.

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

Para eliminar o cancelar un turno, utilizar las acciones disponibles según el rol autorizado. ADMIN y RECEPCION gestionan la agenda; MEDICO y ENFERMERIA no pueden modificar turnos.

### 5.5 Navegar entre fechas

Usar los botones **← Anterior** y **Siguiente →** para moverse entre días.
El botón **Hoy** vuelve a la fecha actual.

---

## 6. Módulo Pacientes

Acceso desde el menú lateral: **Pacientes**  
*(disponible para ADMIN y RECEPCION)*

> **Esta sección es exclusivamente administrativa.** Contiene datos personales, de contacto y de cobertura médica del paciente. No incluye historia clínica, evoluciones médicas ni registros asistenciales. Para acceder a la información clínica de un paciente, usar el módulo **Atención del Paciente** (Sección 8).

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
   Los campos de antecedentes y alergias, si se utilizan, deben considerarse información sensible de apoyo y cargarse según el criterio institucional definido por la clínica.
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
- Historial de turnos del paciente

*(Para acceder a la Ficha Asistencial, usar el módulo Atención del Paciente)*

### 6.5 Dar de baja un paciente

Hacer clic en el ícono de **baja** sobre el paciente. El registro se conserva en el sistema (baja lógica) pero no aparece en la lista activa. Esto permite conservar el historial del paciente y la trazabilidad de sus registros, evitando pérdida de información.

---

## 7. Módulo Médicos

Acceso desde el menú lateral: **Médicos**  
*(Disponible para ADMIN. RECEPCION puede consultar la lista en modo lectura, según permisos del sistema.)*

### 7.1 Registrar un médico

1. Hacer clic en **Nuevo médico**.
2. Completar apellido, nombre, DNI, matrícula y especialidad.
3. Hacer clic en **Guardar**.

> El DNI y la matrícula deben ser únicos. El sistema no permite duplicados.

### 7.2 Editar o dar de baja un médico

- **Editar:** clic en el ícono de lápiz → modificar datos → Guardar.
- **Dar de baja:** clic en el ícono de baja. El médico deja de aparecer en el selector de turnos pero sus registros históricos se conservan.

---

## 8. Módulo Atención del Paciente

Acceso desde el menú lateral: **Atención del Paciente**  
*(disponible para ADMIN, MEDICO y ENFERMERIA)*

> El módulo muestra el mensaje: *"Módulo asistencial para consulta clínica, signos vitales, evoluciones y estudios. Acceso restringido a roles clínicos."* El personal de RECEPCION no tiene acceso a esta sección.

El módulo registra todos los eventos asistenciales del paciente de forma cronológica.

### 8.1 Acceder a la Ficha Asistencial

**Desde el menú Atención del Paciente:**
1. Buscar el paciente por nombre o apellido.
2. Hacer clic en el nombre para abrir la Ficha Asistencial.

**Desde la agenda del día (MEDICO / ENFERMERIA):**
- Hacer clic en el ícono de **Ficha Asistencial** junto al turno del paciente.

### 8.2 Registrar signos vitales (Enfermería)

> Disponible para **ADMIN** y **ENFERMERIA**.
>
> **MEDICO:** puede ver los signos vitales registrados, pero no cargar nuevos registros en esta pestaña.

1. Abrir la Ficha Asistencial del paciente.
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

1. Abrir la Ficha Asistencial del paciente.
2. Seleccionar la pestaña **Evolución Médica**.
3. Completar:
   - Motivo de consulta
   - Diagnóstico presuntivo / definitivo
   - Tratamiento indicado
   - Observaciones adicionales
4. Hacer clic en **Guardar evolución**.

### 8.4 Cargar un estudio (Médico)

> Disponible para **ADMIN** y **MEDICO**.

1. Abrir la Ficha Asistencial del paciente.
2. Seleccionar la pestaña **Estudios**.
3. Completar:
   - Tipo de estudio
   - Estado: Solicitado / Realizado / Informado / Cancelado
   - Observaciones
4. Hacer clic en **Guardar estudio**.

### 8.5 Línea de tiempo asistencial

En la parte inferior de la Ficha Asistencial se muestra una **línea de tiempo** con los últimos 20 eventos registrados del paciente, ordenados del más reciente al más antiguo.

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

> El módulo muestra el mensaje: *"Registro de operaciones del sistema. Uso reservado para administración."*

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
- **Remitente:** clinicamayolp@gmail.com
- **Contenido:** fecha, hora, médico, especialidad y motivo del turno.

> Para que el recordatorio llegue, el paciente debe tener un email registrado en su ficha. Si el campo email está vacío, no se envía el correo.

> **Importante:** para activar el envío de correos es necesario configurar las variables `MAIL_USERNAME` y `MAIL_PASSWORD` en Clever Cloud. Si no están configuradas, los recordatorios no se envían.

---

## 13. Seguridad y buenas prácticas

### Restricciones de acceso por rol

| Intento de acceso | Resultado |
|---|---|
| MEDICO o ENFERMERIA intenta crear, editar o cancelar un turno | Acceso denegado (HTTP 403) |
| RECEPCION intenta acceder a Atención del Paciente | Acceso denegado (HTTP 403) |
| Cualquier rol intenta acceder a una ruta no permitida | Redirección a pantalla de error |

### Buenas prácticas

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
Solo ADMIN y RECEPCION pueden crear y editar turnos. MEDICO y ENFERMERIA tienen acceso de solo lectura: ven la agenda pero no pueden modificarla. Si necesitás registrar un turno, comunicalo a recepción.

**¿El sistema perdió mis datos al cerrarse?**
La sesión expira a los 30 minutos de inactividad. Los datos guardados antes del cierre están a salvo. Los datos no guardados (formularios sin confirmar) se pierden.

**¿Puedo recuperar un paciente dado de baja?**
Sí, el administrador puede reactivar un paciente dado de baja desde el módulo de Usuarios o contactando al soporte técnico.

**¿Qué hago si el sistema no responde?**
Verificar la conexión a internet. Si el problema persiste, comunicarse con el administrador del sistema.

**¿Cómo sé si el recordatorio de turno llegó al paciente?**
El módulo de Auditoría registra el resultado del envío diario de recordatorios con el detalle de cuántos correos se enviaron, cuántos pacientes no tenían email y si hubo errores.

**¿Por qué MEDICO puede ver la Ficha Asistencial de un paciente que no es "suyo"?**
El sistema permite consulta institucional: un médico puede ver la Ficha Asistencial de cualquier paciente activo por razones de atención, interconsulta, guardia, reemplazo o continuidad asistencial. Esto es intencional y responde al criterio de atención institucional. En una etapa futura puede agregarse auditoría específica del motivo de consulta por interconsulta.

---

## 15. Portal del Paciente — acceso público

El **Portal del Paciente** es una página pública accesible desde el sitio web de la clínica. Los pacientes pueden consultarla **sin necesidad de crear una cuenta ni iniciar sesión**.

**Acceso:** desde el sitio institucional, botón **Portal del Paciente**, o directamente en `/portal`.

### Qué puede hacer un paciente desde el portal

1. **Consultar disponibilidad de turnos**
   - Seleccionar un médico de la lista desplegable.
   - Elegir una fecha (no se pueden seleccionar fechas pasadas).
   - Hacer clic en **Ver horarios**.
   - El sistema muestra los horarios del médico para ese día:

| Color del bloque | Significado |
|---|---|
| Azul | Turno **libre** — disponible |
| Gris | Turno **ocupado** |
| Tachado | Horario ya **pasado** |

2. **Ver los horarios de atención del médico** (días y franjas horarias que atiende habitualmente).

3. **Contactar a la clínica** para confirmar el turno:
   - Teléfono: **3437-422237**
   - WhatsApp: **3437-402962**

> **Importante:** el portal solo muestra disponibilidad. El turno **no se confirma automáticamente**. El paciente debe llamar o escribir por WhatsApp para que la recepción registre el turno en el sistema.

---

## 16. Módulo Horarios de Atención

Acceso desde el menú lateral: **Horarios**
*(disponible para ADMIN)*

Permite definir los días y horarios en que cada médico atiende en la clínica. Esta información es utilizada por:
- El sistema para verificar disponibilidad al crear turnos.
- El **Portal del Paciente** para mostrar los slots libres y ocupados.

### Cargar horario de un médico

1. Hacer clic en **Nuevo horario**.
2. Seleccionar el **médico**.
3. Seleccionar el **día de la semana**.
4. Ingresar la **hora de inicio** y la **hora de fin**.
5. Hacer clic en **Guardar**.

Se pueden cargar múltiples franjas por médico (por ejemplo: Lunes 8:00–12:00 y Lunes 15:00–19:00).

### Editar o eliminar un horario

- **Editar:** clic en el ícono de lápiz → modificar → Guardar.
- **Eliminar:** clic en el ícono de papelera.

> Si un médico no tiene horarios cargados, el Portal del Paciente mostrará "Sin horarios cargados" al seleccionarlo.

---

## 17. Sitio Web Institucional

El sitio web público de Clínica Mayo (**clinicamayolapaz.com.ar**) es independiente del sistema MayoSalud. Está construido en **WordPress** y contiene las siguientes páginas:

| Página | Contenido |
|---|---|
| **Inicio** | Presentación general, estadísticas, médicos destacados |
| **Médicos** | Listado de profesionales y especialidades |
| **Especialidades** | Grilla de especialidades disponibles |
| **Equipo** | Socios institucionales, fundadores e históricos, staff |
| **Nosotros** | Historia, misión y valores de la clínica |
| **Contacto** | Teléfonos, WhatsApp y horarios de atención |
| **Portal del Paciente** | Acceso al portal de consulta de turnos |

### Relación entre el sitio web y MayoSalud

- El sitio web es **solo informativo** (gestionado en WordPress).
- El **Portal del Paciente** está alojado dentro de MayoSalud y consume datos reales del sistema (médicos activos, horarios, turnos).
- El personal de la clínica accede al sistema interno desde el botón **Personal** del sitio, que dirige al login de MayoSalud.

> **Para actualizar contenido del sitio web** (textos, fotos, médicos, etc.) se hace desde el panel de administración de WordPress, no desde MayoSalud. Los médicos activos en MayoSalud aparecen automáticamente en el Portal del Paciente.

---

## 18. Funcionalidades futuras

### Teleconsulta

MayoSalud podrá incorporar en una etapa futura un módulo de Teleconsulta para permitir atenciones remotas entre profesionales y pacientes, siempre que la dirección de Clínica Mayo S.R.L. lo defina y que exista revisión médica, operativa y legal previa.

Esta funcionalidad podría incluir:

- solicitud de teleconsulta;
- asignación de profesional;
- enlace seguro para videollamada;
- registro del motivo de consulta;
- vinculación con la Ficha Asistencial;
- registro de evolución médica posterior;
- notificaciones al paciente;
- trazabilidad de la atención realizada.

**Importante:** la Teleconsulta no forma parte del alcance actual del sistema. Es una funcionalidad futura y su implementación dependerá de la política institucional, del criterio médico y de la validación legal correspondiente.

---

*MayoSalud — Clínica Mayo S.R.L. · La Paz, Entre Ríos*
*Tel: 422237 · WhatsApp: 3437 402962*
*Desarrollo y documentación: Ana Carolina Martin · CM | Systems*
