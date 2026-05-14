(function () {

  var horaSeleccionada = null;

  async function loadLibres() {
    var medicoId  = document.getElementById('medicoIdSelect')?.value;
    var fecha     = document.getElementById('fechaInput')?.value;
    var container = document.getElementById('horaLibresContainer');
    var hidden    = document.getElementById('horaHiddenInput');
    if (!container) return;

    // Limpiar selección al cambiar parámetros
    horaSeleccionada = null;
    if (hidden) hidden.value = '';
    container.innerHTML = '';

    if (!medicoId || !fecha) return;

    var duracion = document.getElementById('duracionMinutosSelect')?.value || '30';
    var url = '/turnos/libres?medicoId=' + encodeURIComponent(medicoId) +
              '&fecha=' + encodeURIComponent(fecha) +
              '&duracionMinutos=' + encodeURIComponent(duracion);

    try {
      var resp = await fetch(url);
      if (!resp.ok) return;
      var json = await resp.json();
      // json = { slots: [{ hora, estado, disponible }, ...] }

      if (!json.slots || json.slots.length === 0) {
        container.innerHTML =
          '<span class="text-muted small"><i class="bi bi-info-circle me-1"></i>' +
          'Sin horario configurado para este médico en ese día.</span>';
        return;
      }

      json.slots.forEach(function (slot) {
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn btn-sm me-2 mb-2';
        btn.textContent = slot.hora;
        btn.dataset.hora = slot.hora;
        btn.dataset.estado = slot.estado;

        aplicarEstilo(btn, slot.estado, false);

        if (slot.disponible) {
          btn.addEventListener('click', function () {
            horaSeleccionada = slot.hora;
            if (hidden) hidden.value = slot.hora;
            // Actualizar estilos de todos los botones
            container.querySelectorAll('button').forEach(function (b) {
              var esSeleccionado = b.dataset.hora === slot.hora;
              aplicarEstilo(b, b.dataset.estado, esSeleccionado);
            });
          });
        } else {
          btn.disabled = true;
        }

        container.appendChild(btn);
      });

    } catch (e) {
      console.error('[turnos-libres]', e);
    }
  }

  function aplicarEstilo(btn, estado, seleccionado) {
    if (seleccionado) {
      btn.style.cssText = 'background:#2C5F82; color:#fff; border-color:#2C5F82; font-weight:600;';
      return;
    }
    if (estado === 'LIBRE') {
      btn.style.cssText = 'background:#5B8DB8; color:#fff; border-color:#5B8DB8;';
    } else if (estado === 'OCUPADO') {
      btn.style.cssText = 'background:#e9ecef; color:#adb5bd; border-color:#dee2e6;';
    } else {
      btn.style.cssText = 'background:#fff3cd; color:#856404; border-color:#ffc107;';
    }
  }

  function wireEvents() {
    var medicoSelect  = document.getElementById('medicoIdSelect');
    var fechaInput    = document.getElementById('fechaInput');
    var duracionSelect = document.getElementById('duracionMinutosSelect');

    if (medicoSelect)  medicoSelect.addEventListener('change', loadLibres);
    if (fechaInput)    fechaInput.addEventListener('change', loadLibres);
    if (duracionSelect) duracionSelect.addEventListener('change', loadLibres);

    loadLibres();
  }

  document.addEventListener('DOMContentLoaded', wireEvents);
})();
