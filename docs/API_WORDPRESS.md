# API publica para WordPress

Base URL produccion: `https://<host-mayosalud>/api/v1/public`

Origen WordPress autorizado por CORS: `https://clinicamayolapaz.com.ar`

Para usar otro origen, configurar:

```properties
MAYOSALUD_CORS_ALLOWED_ORIGINS=https://clinicamayolapaz.com.ar,https://www.clinicamayolapaz.com.ar
```

## Endpoints

### Listar medicos activos

`GET /api/v1/public/medicos`

Respuesta:

```json
[
  {
    "id": 1,
    "nombreCompleto": "Dr/a. Perez, Ana",
    "especialidad": "CLINICA_MEDICA"
  }
]
```

No expone DNI, email, telefono ni matricula.

### Horarios de un medico

`GET /api/v1/public/medicos/{medicoId}/horarios`

Respuesta:

```json
[
  {
    "diaSemana": "MONDAY",
    "horaDesde": "08:00",
    "horaHasta": "12:00",
    "duracionBaseMinutos": 30
  }
]
```

Si el medico esta inactivo, devuelve `404`.

### Turnos libres

`GET /api/v1/public/turnos/libres?medicoId=1&fecha=2026-05-18&duracionMinutos=30`

Parametros:

| Nombre | Tipo | Obligatorio | Detalle |
| --- | --- | --- | --- |
| `medicoId` | number | Si | ID del medico activo |
| `fecha` | date | Si | Formato `YYYY-MM-DD` |
| `duracionMinutos` | number | No | Default `30`; valores del sistema: `15`, `30`, `45`, `60` |

Respuesta:

```json
{
  "slots": [
    {
      "hora": "08:00",
      "estado": "LIBRE",
      "disponible": true
    },
    {
      "hora": "08:30",
      "estado": "OCUPADO",
      "disponible": false
    }
  ]
}
```

Estados posibles:

| Estado | Significado |
| --- | --- |
| `LIBRE` | Puede mostrarse como disponible |
| `OCUPADO` | Ya existe un turno que solapa ese horario |
| `PASADO` | Horario anterior a la hora actual cuando la fecha es hoy |

En domingos, feriados o dias sin horario activo, responde:

```json
{
  "slots": []
}
```

## Ejemplo JavaScript

```js
async function cargarTurnosLibres(medicoId, fecha) {
  const params = new URLSearchParams({
    medicoId,
    fecha,
    duracionMinutos: "30"
  });

  const res = await fetch(`https://<host-mayosalud>/api/v1/public/turnos/libres?${params}`, {
    credentials: "include"
  });

  if (!res.ok) {
    throw new Error(`Error MayoSalud API: ${res.status}`);
  }

  return res.json();
}
```

## Notas de seguridad

- La API publica es de solo lectura.
- No permite crear, editar ni cancelar turnos.
- Los endpoints internos `/turnos/**` siguen protegidos por roles de Spring Security.
- CORS solo acepta origenes configurados en `MAYOSALUD_CORS_ALLOWED_ORIGINS`.
