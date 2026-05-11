- [x] Plan aprobado e implementación de duración por turno
- [ ] Agregar campo `duracionMinutos` a `Turno` (entidad)
- [ ] Actualizar `turno/form.html` con selector 15/30/45/60
- [ ] Actualizar `turnos-libres.js` para enviar `duracionMinutos` al endpoint
- [ ] Actualizar `TurnoController` para recibir `duracionMinutos` en `/turnos/libres`
- [ ] Actualizar `TurnoService.calcularTurnosLibres` para marcar libres por intervalos (sin solapamiento)
- [ ] Actualizar `TurnoService.guardar` para validar solapamiento por intervalo usando duración
- [ ] Ajustar/crear migración SQL si corresponde (si no hay migraciones, documentar)
- [ ] Compilar y verificar (`mvn -DskipTests compile`)

