document.addEventListener('DOMContentLoaded', function() {
    initializeCalendar();
    initializeFilters();
});

function initializeCalendar() {
    const calendarEl = document.getElementById('calendario');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        locale: 'es',
        events: '/api/clases',
        eventClick: function(info) {
            mostrarDetallesClase(info.event);
        },
        selectable: true,
        select: function(info) {
            prepararNuevaClase(info);
        }
    });
    calendar.render();
}

function guardarClase() {
    const formData = new FormData(document.getElementById('claseForm'));
    const data = Object.fromEntries(formData.entries());
    
    fetch('/api/clases', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            throw new Error('Error al guardar la clase');
        }
    })
    .catch(error => mostrarError('Error al guardar la clase'));
}

function exportarCalendario() {
    window.location.href = '/api/clases/exportar';
} 