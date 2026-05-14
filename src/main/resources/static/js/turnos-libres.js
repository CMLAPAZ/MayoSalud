(function() {

  async function loadLibres() {
    const medicoId = document.getElementById('medicoIdSelect')?.value;
    const fecha = document.getElementById('fechaInput')?.value;
    const container = document.getElementById('horaLibresContainer');
    if (!container) return;

    if (!medicoId || !fecha) {
      container.innerHTML = '';
      return;
    }

    const duracionMinutos = document.getElementById('duracionMinutosSelect')?.value || '30';
    const url = `/turnos/libres?medicoId=${encodeURIComponent(medicoId)}&fecha=${encodeURIComponent(fecha)}&duracionMinutos=${encodeURIComponent(duracionMinutos)}`;

    try {
      const resp = await fetch(url);
      if (!resp.ok) return;
      const json = await resp.json();
      // json = { todos: ['08:00', ...], libres: ['08:00', ...] }

      container.innerHTML = '';

      if (!json.todos || json.todos.length === 0) {
        container.innerHTML = '<span class="text-muted small">Sin horario configurado para este médico y día.</span>';
        return;
      }

      json.todos.forEach(function(t) {
        const libre = json.libres.includes(t);

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn btn-sm me-2 mb-2';
        btn.textContent = t;
        btn.style = libre
          ? 'background:#5B8DB8; color:#fff; border-color:#5B8DB8;'
          : 'background:#f1f3f5; color:#adb5bd; border-color:#e9ecef;';

        btn.disabled = !libre;
        if (libre) {
          btn.addEventListener('click', function() {
            document.getElementById('horaHiddenInput').value = t;
            document.querySelectorAll('#horaLibresContainer button').forEach(function(b) {
              b.style.outline = '';
            });
            btn.style.outline = '2px solid #2C5F82';
          });
        }

        container.appendChild(btn);
      });
    } catch (e) {
      console.error('[turnos-libres]', e);
    }
  }

  function wireEvents() {
    const medicoSelect = document.getElementById('medicoIdSelect');
    const fechaInput = document.getElementById('fechaInput');
    const duracionSelect = document.getElementById('duracionMinutosSelect');

    if (medicoSelect) medicoSelect.addEventListener('change', loadLibres);
    if (fechaInput) fechaInput.addEventListener('change', loadLibres);
    if (duracionSelect) duracionSelect.addEventListener('change', loadLibres);

    loadLibres();
  }

  document.addEventListener('DOMContentLoaded', wireEvents);
})();
