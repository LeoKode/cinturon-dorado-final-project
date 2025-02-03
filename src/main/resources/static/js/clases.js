document.addEventListener('DOMContentLoaded', function () {
    initializeCalendar();
    initializeFilters();
});

function initializeCalendar() {
    var Calendar = FullCalendar.Calendar;
    var Draggable = FullCalendar.Draggable;

    var containerEl = document.getElementById('external-events');
    var calendarEl = document.getElementById('calendar');
    if (!calendarEl || !containerEl) return;

  // Initialize draggable events
  new Draggable(containerEl, {
    itemSelector: '.fc-event',
    eventData: function(eventEl) {
        const mainEl = eventEl.querySelector('.fc-event-main');
        const dataStr = mainEl.getAttribute('data-event');
        const data = JSON.parse(dataStr);
        console.log('Dragged event data:', data); // Debug
        
        return {
            title: data.title,
            duration: parseInt(data.duration),
            extendedProps: {
                duration: parseInt(data.duration)
            }
        };
    }
});
    // Initialize calendar
    var calendar = new Calendar(calendarEl, {
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        editable: true,
        droppable: true,
        events: '/clases/api/clases',
        eventClick: function(info) {
            if (confirm('¿Estás seguro de eliminar esta clase?')) {
                eliminarClase(info.event.id);
            }
        },
        eventReceive: function(info) {
            guardarClaseArrastrada(info.event);
        },
        eventDrop: function(info) {
            const id = info.event.id;
            const nuevaFecha = info.event.start;
            if (id) {
                actualizarFechaClase(id, nuevaFecha, info);
            }
        }
    });

    calendar.render();
}

function actualizarFechaClase(id, fecha, info) {
    if (!id || !fecha) return;

    // Ajustar la zona horaria
    const offset = fecha.getTimezoneOffset();
    fecha.setMinutes(fecha.getMinutes() - offset);

    const fechaHora = fecha.toISOString().slice(0, 19);
    
    fetch(`/clases/${id}/actualizar-fecha`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ fechaHora: fechaHora })
    })
    .then(response => {
        if (!response.ok) {
            info.revert();
            throw new Error('Error al actualizar la fecha');
        }
        return response.json();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al actualizar la fecha de la clase');
        info.revert();
    });
}

function agregarClaseDisponible() {
    const titulo = document.getElementById('titulo').value;
    const duracion = document.getElementById('duracion').value;
    
    if (!titulo || !duracion) {
        alert('Por favor complete todos los campos');
        return;
    }

    const formData = new FormData();
    formData.append('titulo', titulo);
    formData.append('duracion', duracion);
    formData.append('tipo', titulo.toLowerCase().includes('infantil') ? 'PRINCIPIANTE' : 'AVANZADO');

    fetch('/clases/plantilla', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al guardar la clase');
        }
        return response.json();
    })
    .then(data => {
        // Crear el elemento visual
        const nuevoEvento = document.createElement('div');
        nuevoEvento.className = 'fc-event fc-h-event fc-daygrid-event fc-daygrid-block-event';
        
        const eventoMain = document.createElement('div');
        eventoMain.className = 'fc-event-main';
        eventoMain.setAttribute('data-event', JSON.stringify({
            title: titulo,
            duration: duracion // Guardar la duración directamente como número        
            }));
        eventoMain.textContent = titulo;
        
        nuevoEvento.appendChild(eventoMain);
        
        // Añadir al contenedor y hacer draggable
        const container = document.getElementById('external-events');
        if (container) {
            container.appendChild(nuevoEvento);
            new FullCalendar.Draggable(nuevoEvento, {
                eventData: function(eventEl) {
                    const dataEvent = eventEl.querySelector('.fc-event-main').getAttribute('data-event');
                    return JSON.parse(dataEvent);
                }
            });
        }

        // Cerrar el modal y limpiar el formulario
        const modal = bootstrap.Modal.getInstance(document.getElementById('nuevaClaseModal'));
        modal.hide();
        document.getElementById('claseForm').reset();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al guardar la clase');
    });
}

function guardarClase() {
    // For form submission
    const form = document.getElementById('claseForm');
    form.submit();
}

function eliminarClase(id) {
    if (!id) {
        console.error('ID de clase no proporcionado');
        return;
    }

    fetch(`/clases/${id}/eliminar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        credentials: 'same-origin'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('Error al eliminar la clase');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al eliminar la clase');
        });
}



function guardarClaseArrastrada(event) {
    // Crear elemento modal
    const modalDiv = document.createElement('div');
    modalDiv.className = 'modal fade';
    modalDiv.id = 'timeSelectModal';
    modalDiv.setAttribute('tabindex', '-1');
    modalDiv.innerHTML = `
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Seleccionar Hora</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="timeInput" class="form-label">Hora de la clase</label>
                        <input type="time" class="form-control" id="timeInput" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-primary" onclick="confirmarGuardarClase(event)">Guardar</button>
                </div>
            </div>
        </div>
    `;
    
    // Añadir modal al DOM
    document.body.appendChild(modalDiv);
    
    // Crear e inicializar el modal
    const timeModal = new bootstrap.Modal(modalDiv);
    
    // Mostrar modal
    timeModal.show();
    
    // Guardar referencia al evento
    window.tempEvent = event;
    
    // Limpiar modal cuando se cierre
    modalDiv.addEventListener('hidden.bs.modal', function() {
        document.body.removeChild(modalDiv);
    });
}

function confirmarGuardarClase(event) {
    const timeInput = document.getElementById('timeInput');
    const selectedTime = timeInput.value;
    
    if (!selectedTime) {
        alert('Por favor seleccione una hora');
        return;
    }

    // Obtener la fecha del evento
    const fecha = new Date(window.tempEvent.startStr);
    const [hours, minutes] = selectedTime.split(':');
    
    // Ajustar la hora considerando la zona horaria
    const offset = fecha.getTimezoneOffset();
    fecha.setHours(parseInt(hours), parseInt(minutes));
    fecha.setMinutes(fecha.getMinutes() - offset); // Compensar la diferencia horaria
    
    const fechaHora = fecha.toISOString().slice(0, 19);

    // Resto del código igual...
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/clases';

    try {
        const eventData = window.tempEvent._def?.extendedProps;
        const duration = eventData?.duration;
        
        console.log('Event data:', window.tempEvent._def);
        console.log('Extended props:', eventData);
        console.log('Duration from event:', duration);
    
        if (!duration) {
            throw new Error('Duration not found in event data');
        }
    
        const datos = {
            'tipo': window.tempEvent.title?.toLowerCase().includes('infantil') ? 'PRINCIPIANTE' : 'AVANZADO',
            'fechaHora': fechaHora,
            'titulo': window.tempEvent.title,
            'duracion': parseInt(duration)
        };

        Object.entries(datos).forEach(([key, value]) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = value;
            form.appendChild(input);
        });

        document.body.appendChild(form);
        form.submit();
    } catch (e) {
        console.error('Error procesando evento:', e);
        alert('Error al guardar la clase en el calendario');
    }

    // Cerrar modal
    const modal = bootstrap.Modal.getInstance(document.querySelector('.modal'));
    modal.hide();
}

function exportarCalendario() {
    window.location.href = '/api/clases/exportar';
} 