(function () {

  // th:field sobreescribe el id explícito del HTML, pero name lo setea correctamente.
  // Usamos querySelector por name para encontrar los elementos de forma confiable.
  function porNombre(selector) {
    return document.querySelector(selector);
  }

  var DIA_ES = {
    MONDAY: 'Lunes', TUESDAY: 'Martes', WEDNESDAY: 'Miércoles',
    THURSDAY: 'Jueves', FRIDAY: 'Viernes', SATURDAY: 'Sábado', SUNDAY: 'Domingo'
  };
  var DIA_ORDEN = {
    MONDAY: 0, TUESDAY: 1, WEDNESDAY: 2, THURSDAY: 3, FRIDAY: 4, SATURDAY: 5, SUNDAY: 6
  };

  var ESTILOS = {
    LIBRE:        'background:#dbeafe; color:#1d4ed8; border-color:#93c5fd;',
    OCUPADO:      'background:#f3f4f6; color:#9ca3af; border-color:#e5e7eb;',
    PASADO:       'background:#f3f4f6; color:#d1d5db; border-color:#e5e7eb; text-decoration:line-through;',
    SUSPENDIDO:   'background:#fef9c3; color:#92400e; border-color:#fde68a;',
    SELECCIONADO: 'background:#1d4ed8; color:#fff;    border-color:#1e3a8a; font-weight:700;'
  };

  var ETIQUETAS = {
    LIBRE:      'Libre',
    OCUPADO:    'Ocupado',
    PASADO:     'Pasado',
    SUSPENDIDO: 'Suspendido'
  };

  // Horarios activos del médico seleccionado (se actualiza al cambiar el médico)
  var horariosDelMedico = [];

  function setMensaje(texto, esError) {
    var el = document.getElementById('mensajeHorarios');
    if (!el) return;
    if (!texto) { el.innerHTML = ''; return; }
    var icono = esError
      ? 'bi-exclamation-triangle-fill text-danger'
      : 'bi-info-circle text-muted';
    el.innerHTML =
      '<span class="small"><i class="bi ' + icono + ' me-1"></i>' + texto + '</span>';
  }

  function aplicarEstilo(btn, estado, seleccionado) {
    var base = 'display:inline-block; min-width:76px; padding:7px 10px; ' +
               'border-radius:10px; border:1.5px solid; text-align:center; ' +
               'margin:0 8px 10px 0; cursor:default; ';
    if (seleccionado) {
      btn.style.cssText = base + ESTILOS.SELECCIONADO + ' cursor:pointer;';
    } else {
      btn.style.cssText = base + (ESTILOS[estado] || ESTILOS.SUSPENDIDO) +
        (btn.disabled ? '' : ' cursor:pointer;');
    }
  }

  function renderHorariosMedico(horarios) {
    var el = document.getElementById('infoHorariosMedico');
    if (!el) return;
    if (!horarios || horarios.length === 0) {
      el.innerHTML = '<div class="alert alert-warning py-2 small mb-0 mt-1">' +
        '<i class="bi bi-calendar-x me-1"></i>' +
        'Este médico no tiene horarios de atención cargados.</div>';
      return;
    }
    var sorted = horarios.slice().sort(function (a, b) {
      return (DIA_ORDEN[a.diaSemana] || 9) - (DIA_ORDEN[b.diaSemana] || 9);
    });
    var filas = sorted.map(function (h) {
      return '<li class="mb-0">' +
        '<strong>' + (DIA_ES[h.diaSemana] || h.diaSemana) + ':</strong> ' +
        h.horaDesde + ' a ' + h.horaHasta +
        '</li>';
    }).join('');
    el.innerHTML =
      '<div class="alert alert-info py-2 small mb-0 mt-1">' +
        '<i class="bi bi-calendar-check me-1"></i>' +
        '<strong>Días de atención:</strong>' +
        '<ul class="mb-0 mt-1 ps-3">' + filas + '</ul>' +
      '</div>';
  }

  async function loadHorariosMedico() {
    var medicoEl = porNombre('select[name="medico.id"]');
    var el       = document.getElementById('infoHorariosMedico');
    horariosDelMedico = [];
    if (!el) return;
    var medicoId = medicoEl ? medicoEl.value : '';
    if (!medicoId) { el.innerHTML = ''; return; }
    try {
      var resp = await fetch('/turnos/horarios-medico?medicoId=' + encodeURIComponent(medicoId));
      if (!resp.ok) { el.innerHTML = ''; return; }
      horariosDelMedico = await resp.json();
      renderHorariosMedico(horariosDelMedico);
    } catch (e) {
      el.innerHTML = '';
    }
  }

  async function loadLibres() {
    // Buscar por name (confiable) no por id (sobreescrito por th:field)
    var medicoEl   = porNombre('select[name="medico.id"]');
    var fechaEl    = porNombre('input[name="fecha"]');
    var duracionEl = porNombre('select[name="duracionMinutos"]');
    var grilla     = document.getElementById('grillaHorarios');
    var hidden     = document.getElementById('horaHiddenInput');

    if (!grilla) return;

    grilla.innerHTML = '';
    if (hidden) hidden.value = '';

    var medicoId = medicoEl ? medicoEl.value : '';
    var fecha    = fechaEl  ? fechaEl.value  : '';

    if (!medicoId || !fecha) {
      setMensaje('Seleccioná médico, fecha y duración para ver los horarios disponibles.');
      return;
    }

    var duracion = (duracionEl && duracionEl.value) ? duracionEl.value : '30';

    var url = '/turnos/libres'
            + '?medicoId='        + encodeURIComponent(medicoId)
            + '&fecha='           + encodeURIComponent(fecha)
            + '&duracionMinutos=' + encodeURIComponent(duracion);

    setMensaje('Cargando horarios...');

    try {
      var resp = await fetch(url);

      if (!resp.ok) {
        setMensaje('No se pudieron cargar los horarios disponibles.', true);
        return;
      }

      var json = await resp.json();
      // json = { slots: [{ hora, estado, disponible }, ...] }

      if (!json.slots || json.slots.length === 0) {
        if (horariosDelMedico.length > 0) {
          var sorted = horariosDelMedico.slice().sort(function (a, b) {
            return (DIA_ORDEN[a.diaSemana] || 9) - (DIA_ORDEN[b.diaSemana] || 9);
          });
          var diasTexto = sorted.map(function (h) {
            return (DIA_ES[h.diaSemana] || h.diaSemana).toLowerCase();
          }).join(', ');
          setMensaje('El médico seleccionado no atiende ese día. Días de atención: ' + diasTexto + '.', true);
        } else {
          setMensaje('El médico no tiene horarios de atención configurados para esta fecha.');
        }
        return;
      }

      setMensaje('');

      var horaPrevia = hidden ? hidden.value : '';

      json.slots.forEach(function (slot) {
        var esSeleccionado = slot.hora === horaPrevia;

        var btn = document.createElement('button');
        btn.type           = 'button';
        btn.dataset.hora   = slot.hora;
        btn.dataset.estado = slot.estado;
        btn.disabled       = !slot.disponible;

        btn.innerHTML =
          '<span style="display:block;font-size:1rem;font-weight:600;line-height:1.3">' +
            slot.hora +
          '</span>' +
          '<span style="display:block;font-size:0.6rem;letter-spacing:.6px;text-transform:uppercase;margin-top:2px">' +
            (ETIQUETAS[slot.estado] || slot.estado) +
          '</span>';

        aplicarEstilo(btn, slot.estado, esSeleccionado);

        if (slot.disponible) {
          btn.addEventListener('click', function () {
            if (hidden) hidden.value = slot.hora;
            grilla.querySelectorAll('button').forEach(function (b) {
              aplicarEstilo(b, b.dataset.estado, b.dataset.hora === slot.hora);
            });
          });
        }

        grilla.appendChild(btn);
      });

      // Modo edición: remarcar hora ya asignada
      if (horaPrevia) {
        grilla.querySelectorAll('button').forEach(function (b) {
          aplicarEstilo(b, b.dataset.estado, b.dataset.hora === horaPrevia);
        });
      }

    } catch (e) {
      console.error('[turnos-libres]', e);
      setMensaje('No se pudieron cargar los horarios disponibles.', true);
    }
  }

  function wireEvents() {
    var medicoEl   = porNombre('select[name="medico.id"]');
    var fechaEl    = porNombre('input[name="fecha"]');
    var duracionEl = porNombre('select[name="duracionMinutos"]');
    var idEl       = porNombre('input[name="id"]');

    // Restringir fecha mínima a hoy solo en turnos nuevos
    var esNuevo = !idEl || !idEl.value;
    if (esNuevo && fechaEl) {
      fechaEl.min = new Date().toISOString().split('T')[0];
    }

    if (medicoEl) {
      medicoEl.addEventListener('change', function () {
        loadHorariosMedico();
        loadLibres();
      });
    }
    if (fechaEl)    fechaEl.addEventListener('change', loadLibres);
    if (duracionEl) duracionEl.addEventListener('change', loadLibres);

    // Cargar horarios del médico si ya hay uno seleccionado (modo edición)
    loadHorariosMedico();
    loadLibres();
  }

  document.addEventListener('DOMContentLoaded', wireEvents);
})();
