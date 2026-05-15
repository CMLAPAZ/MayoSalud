(function () {

  var ESTILOS = {
    LIBRE:       'background:#dbeafe; color:#1e40af; border-color:#93c5fd;',
    OCUPADO:     'background:#f1f3f5; color:#9ca3af; border-color:#e5e7eb;',
    SUSPENDIDO:  'background:#fef9c3; color:#92400e; border-color:#fde68a;',
    SELECCIONADO:'background:#1e40af; color:#fff;    border-color:#1e3a8a; font-weight:600;'
  };

  var ETIQUETAS = {
    LIBRE:      'Libre',
    OCUPADO:    'Ocupado',
    SUSPENDIDO: 'Suspendido'
  };

  function setMensaje(texto, esError) {
    var el = document.getElementById('mensajeHorarios');
    if (!el) return;
    if (!texto) { el.innerHTML = ''; return; }
    var icono = esError ? 'bi-exclamation-triangle text-danger' : 'bi-info-circle text-muted';
    el.innerHTML = '<span class="small"><i class="bi ' + icono + ' me-1"></i>' + texto + '</span>';
  }

  async function loadLibres() {
    var medicoId  = document.getElementById('medicoIdSelect')?.value;
    var fecha     = document.getElementById('fechaInput')?.value;
    var grilla    = document.getElementById('grillaHorarios');
    var hidden    = document.getElementById('horaHiddenInput');

    if (!grilla) return;

    grilla.innerHTML = '';
    if (hidden) hidden.value = '';

    if (!medicoId || !fecha) {
      setMensaje('Seleccioná médico, fecha y duración para ver los horarios disponibles.');
      return;
    }

    var duracion = document.getElementById('duracionMinutosSelect')?.value || '30';
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
        setMensaje('El médico no tiene horarios de atención configurados para esta fecha.');
        return;
      }

      setMensaje('');

      // Valor preexistente (modo edición)
      var horaPrevia = hidden ? hidden.value : '';

      json.slots.forEach(function (slot) {
        var esSeleccionado = (slot.hora === horaPrevia);

        var btn = document.createElement('button');
        btn.type = 'button';
        btn.dataset.hora   = slot.hora;
        btn.dataset.estado = slot.estado;
        btn.disabled = !slot.disponible;

        // Contenido: hora grande + etiqueta pequeña
        btn.innerHTML =
          '<span style="display:block;font-size:1rem;font-weight:600;line-height:1.2">' + slot.hora + '</span>' +
          '<span style="display:block;font-size:0.65rem;letter-spacing:.5px;text-transform:uppercase">' +
            (ETIQUETAS[slot.estado] || slot.estado) +
          '</span>';

        btn.style.cssText = 'min-width:72px; padding:6px 8px; border-radius:8px; border:1.5px solid; ' +
                            'text-align:center; margin:0 6px 8px 0; ' +
                            (ESTILOS[esSeleccionado ? 'SELECCIONADO' : slot.estado] || ESTILOS.SUSPENDIDO);

        if (slot.disponible) {
          btn.addEventListener('click', function () {
            if (hidden) hidden.value = slot.hora;
            grilla.querySelectorAll('button').forEach(function (b) {
              var sel = b.dataset.hora === slot.hora;
              b.style.cssText = btn.style.cssText.replace(
                /background:[^;]+;.*?border-color:[^;]+/,
                ESTILOS[sel ? 'SELECCIONADO' : b.dataset.estado] || ESTILOS.SUSPENDIDO
              );
              // Aplicar estilo limpio en lugar de manipular string
              aplicarEstilo(b, b.dataset.estado, sel);
            });
          });
        }

        grilla.appendChild(btn);
      });

      // Remarcar si hay hora previa (modo edición)
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

  function aplicarEstilo(btn, estado, seleccionado) {
    var base = 'min-width:72px; padding:6px 8px; border-radius:8px; border:1.5px solid; text-align:center; margin:0 6px 8px 0; ';
    btn.style.cssText = base + (ESTILOS[seleccionado ? 'SELECCIONADO' : estado] || ESTILOS.SUSPENDIDO);
  }

  function wireEvents() {
    var medicoSelect   = document.getElementById('medicoIdSelect');
    var fechaInput     = document.getElementById('fechaInput');
    var duracionSelect = document.getElementById('duracionMinutosSelect');

    if (medicoSelect)   medicoSelect.addEventListener('change', loadLibres);
    if (fechaInput)     fechaInput.addEventListener('change', loadLibres);
    if (duracionSelect) duracionSelect.addEventListener('change', loadLibres);

    loadLibres();
  }

  document.addEventListener('DOMContentLoaded', wireEvents);
})();
