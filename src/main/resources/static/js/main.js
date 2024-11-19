// Funcionalidad para actualizar datos en tiempo real
document.addEventListener('DOMContentLoaded', function() {
    // Actualizar estadísticas cada 5 minutos
    setInterval(actualizarEstadisticas, 300000);
});

function actualizarEstadisticas() {
    fetch('/api/dashboard/stats')
        .then(response => response.json())
        .then(data => {
            document.querySelector('[data-stat="alumnos"]').textContent = data.totalAlumnos;
            document.querySelector('[data-stat="clases"]').textContent = data.clasesHoy;
            document.querySelector('[data-stat="pagos"]').textContent = data.pagosPendientes;
            document.querySelector('[data-stat="examenes"]').textContent = data.proximosExamenes;
        })
        .catch(error => console.error('Error actualizando estadísticas:', error));
} 