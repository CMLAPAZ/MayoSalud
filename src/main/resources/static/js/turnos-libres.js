(function() {
  function pad2(n) { return String(n).padStart(2, '0'); }

  // Slots fijos solicitados por el usuario: cada 30 min desde 08:00.
  // Ajustalo acá si querés otro rango.
  const SLOT_TIMES = [
    '08:00','08:30','09:00','09:30','10:00','10:30','11:00','11:30','12:00'
  ];

  async function loadLibres() {
    const medicoId = document.getElementById('medicoIdSelect')?.value;
    const fecha = document.getElementById('fechaInput')?.value;
    const duracionMinutos = document.querySelector('[name="duracionMinutos"]')?.value;

    if (!medicoId || !fecha || !duracionMinutos) return;

    const resp = await fetch(`/turnos/libres?medicoId=${encodeURIComponent(medicoId)}&fecha=${encodeURIComponent(fecha)}&duracionMinutos=${encodeURIComponent(duracionMinutos)}`);
    if (!resp.ok) return;

    const json = await resp.json();
    // json = { libres: ['08:00', ...] }

    const container = document.getElementById('horaLibresContainer');
    if (!container) return;

    // Limpiar
    container.innerHTML = '';

    SLOT_TIMES.forEach(function(t) {
      const disabled = !json.libres.includes(t);

      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn btn-sm me-2 mb-2';
      btn.textContent = t;
      btn.style = disabled
        ? 'background:#f1f3f5; color:#adb5bd; border-color:#e9ecef;'
        : 'background:#5B8DB8; color:#fff; border-color:#5B8DB8;';

      btn.disabled = disabled;
      if (!disabled) {
        btn.addEventListener('click', function() {
          const horaHidden = document.getElementById('horaHiddenInput');
          horaHidden.value = t;

          // Visual feedback
          document.querySelectorAll('#horaLibresContainer button').forEach(function(b) {
            b.style.outline = '';
          });
          btn.style.outline = '2px solid #2C5F82';
        });
      }

      container.appendChild(btn);
    });
  }

  function wireEvents() {
    const medicoSelect = document.getElementById('medicoIdSelect');
    const fechaInput = document.getElementById('fechaInput');

    if (medicoSelect) medicoSelect.addEventListener('change', loadLibres);
    if (fechaInput) fechaInput.addEventListener('change', loadLibres);

    // Al cargar
    loadLibres();
  }

  document.addEventListener('DOMContentLoaded', wireEvents);
})();

