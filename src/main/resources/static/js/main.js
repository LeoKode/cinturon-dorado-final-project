document.addEventListener('DOMContentLoaded', function() {
    // Ejemplo de funcionalidad general para la UI
    console.log('Página cargada con estadísticas iniciales.');

    // Si necesitas manejar un evento en un botón
    const actualizarBtn = document.querySelector('#actualizarEstadisticasBtn');
    if (actualizarBtn) {
        actualizarBtn.addEventListener('click', function() {
            console.log('El botón de actualizar ha sido presionado. Página recargada.');
        });
    }
});
