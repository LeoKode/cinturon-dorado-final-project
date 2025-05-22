document.addEventListener('DOMContentLoaded', function () {
    const claseForm = document.getElementById('claseForm');
    const btnEliminar = document.getElementById('btnEliminar');

    if (claseForm) {
        claseForm.addEventListener('submit', function (e) {
            e.preventDefault();
            guardarClase(this);
        });
    }

    if (btnEliminar) {
        btnEliminar.addEventListener('click', function() {
            const claseId = document.getElementById('claseId').value;
            if (claseId && confirm('¿Estás seguro de eliminar esta clase?')) {
                eliminarClase(claseId);
            }
        });
    }
});

function mostrarModalClase(celda) {
    const dia = celda.dataset.dia;
    const horarioId = celda.closest('tr').querySelector('.fw-bold').dataset.horarioId;
    
    // Obtener la clase existente si hay una
    const claseExistente = celda.querySelector('.badge');
    
    // Get modal and form elements
    const modal = new bootstrap.Modal(document.getElementById('claseModal'));
    const form = document.getElementById('claseForm');
    const claseIdInput = document.getElementById('claseId');
    const diaInput = document.getElementById('diaClase');
    const horarioInput = document.getElementById('horarioId');
    const tituloInput = document.getElementById('titulo');
    const btnEliminar = document.getElementById('btnEliminar');
    
    if (form) {
        // Reset form
        form.reset();
        
        // Set initial values
        diaInput.value = dia;
        horarioInput.value = horarioId; // Establecer el ID del horario
        
        // Si hay una clase existente, cargar sus datos
        if (claseExistente) {
            const claseId = claseExistente.getAttribute('data-clase-id');
            claseIdInput.value = claseId;
            tituloInput.value = claseExistente.textContent.trim();
            btnEliminar.style.display = 'block';
        } else {
            claseIdInput.value = '';
            btnEliminar.style.display = 'none';
        }
        
        // Update modal title
        const modalTitle = document.querySelector('#claseModal .modal-title');
        if (modalTitle) {
            modalTitle.textContent = claseExistente ? 
                `Editar Clase - ${dia}` : 
                `Nueva Clase - ${dia}`;
        }
        
        modal.show();
    }
}

// Función para mostrar el modal de nuevo horario
function mostrarModalNuevoHorario(horaCell) {
    const modal = new bootstrap.Modal(document.getElementById('nuevoHorarioModal'));
    const form = document.getElementById('horarioForm');
    const horarioInput = document.getElementById('nuevoHorario');
    const btnEliminar = document.getElementById('btnEliminarHorario');
    
    form.reset();
    
    if (horaCell) {
        // Modo edición
        const hora = horaCell.textContent;
        const horarioId = horaCell.getAttribute('data-horario-id');
        horarioInput.value = hora;
        document.getElementById('horarioId').value = horarioId;
        btnEliminar.style.display = 'block';
        btnEliminar.onclick = () => eliminarHorario(horarioId);
    } else {
        // Modo nuevo horario
        btnEliminar.style.display = 'none';
        document.getElementById('horarioId').value = '';
    }
    
    modal.show();
}

// Función para agregar el nuevo horario
function agregarNuevoHorario() {
    const horarioInput = document.getElementById('nuevoHorario');
    const nuevoHorario = horarioInput.value;
    const horarioId = document.getElementById('horarioId').value;
    
    if (!nuevoHorario) {
        mostrarError('Debe seleccionar una hora válida');
        return;
    }

    // Determinar si es una actualización o nuevo horario
    const url = horarioId ? 
        `/clases/horario/${horarioId}/actualizar` : 
        '/clases/horario/agregar';

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `hora=${nuevoHorario}`
    })
    .then(response => response.text())
    .then(html => {
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = html;
        
        if (html.includes('error')) {
            const errorMessage = tempContainer.querySelector('.alert-danger')?.textContent;
            mostrarError(errorMessage || 'Error al procesar el horario');
        } else {
            const tablaClases = document.querySelector('.table-responsive');
            if (tablaClases) {
                tablaClases.innerHTML = tempContainer.querySelector('.table-responsive').innerHTML;
            }
            bootstrap.Modal.getInstance(document.getElementById('nuevoHorarioModal')).hide();
            mostrarMensaje(
                horarioId ? 'Horario actualizado correctamente' : 'Horario agregado correctamente', 
                'success'
            );
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al procesar el horario');
    });
}

function guardarClase(form) {
    fetch(form.action, {
        method: 'POST',
        body: new FormData(form)
    })
    .then(response => response.text())
    .then(html => {
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = html;
        
        const tablaClases = document.querySelector('.table-responsive');
        if (tablaClases) {
            tablaClases.innerHTML = tempContainer.querySelector('.table-responsive').innerHTML;
        }
        
        // Verificar si hay mensaje de error
        const errorElement = tempContainer.querySelector('.alert-danger');
        if (errorElement) {
            mostrarError(errorElement.textContent);
        } else {
            // Cerrar el modal y mostrar mensaje de éxito
            bootstrap.Modal.getInstance(document.getElementById('claseModal')).hide();
            mostrarMensaje('Clase guardada correctamente', 'success');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al guardar la clase');
    });
}

function eliminarClase(id) {
    fetch(`/clases/${id}/eliminar`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(html => {
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = html;
        
        const tablaClases = document.querySelector('.table-responsive');
        if (tablaClases) {
            tablaClases.innerHTML = tempContainer.querySelector('.table-responsive').innerHTML;
        }
        
        // Cerrar el modal y mostrar mensaje
        bootstrap.Modal.getInstance(document.getElementById('claseModal')).hide();
        mostrarMensaje('Clase eliminada correctamente', 'success');
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al eliminar la clase');
    });
}

function eliminarHorario(id) {
    if (!confirm('¿Está seguro de eliminar este horario? Esta acción no se puede deshacer una vez confirmada.')) {
        return;
    }

    fetch(`/clases/horario/${id}/eliminar`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(html => {
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = html;
        
        // Buscar mensaje de error
        const errorElement = tempContainer.querySelector('.alert-danger');
        if (errorElement) {
            // Si hay error, mostrar el mensaje
            mostrarError(errorElement.textContent.trim());
            // Cerrar el modal
            bootstrap.Modal.getInstance(document.getElementById('nuevoHorarioModal'))?.hide();
        } else {
            // Si no hay error, actualizar la tabla y mostrar mensaje de éxito
            const tablaClases = document.querySelector('.table-responsive');
            if (tablaClases) {
                tablaClases.innerHTML = tempContainer.querySelector('.table-responsive').innerHTML;
            }
            bootstrap.Modal.getInstance(document.getElementById('nuevoHorarioModal')).hide();
            mostrarMensaje('Horario eliminado correctamente', 'success');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al eliminar el horario');
    });
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" onclick="this.parentElement.remove()"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);
    
    // Auto-ocultar después de 5 segundos con animación
    setTimeout(() => {
        alertDiv.classList.remove('show');
        alertDiv.classList.add('fade');
        setTimeout(() => {
            if (alertDiv && alertDiv.parentElement) {
                alertDiv.remove();
            }
        }, 150); // Tiempo de la animación de fade
    }, 5000);
}

function mostrarMensaje(mensaje, tipo = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" onclick="this.parentElement.remove()"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);
    
    // Auto-ocultar después de 3 segundos con animación
    setTimeout(() => {
        alertDiv.classList.remove('show');
        alertDiv.classList.add('fade');
        setTimeout(() => {
            if (alertDiv && alertDiv.parentElement) {
                alertDiv.remove();
            }
        }, 150); // Tiempo de la animación de fade
    }, 3000);
}