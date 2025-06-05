document.addEventListener('DOMContentLoaded', function () {
    initializeExamenesForm();

    // Inicializar el manejo del modal
    const detallesModal = document.getElementById('detallesExamenModal');
    const nuevoExamenModal = document.getElementById('nuevoExamenModal');
    if (detallesModal) {
        detallesModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            verDetallesExamen(id);
        });
    }

    if (nuevoExamenModal) {
        nuevoExamenModal.addEventListener('show.bs.modal', function() {
            // Resetear el filtro
            document.getElementById('categoriaFiltro').value = '';
            // Mostrar todos los alumnos
            filtrarAlumnosPorCategoria();
        });
    }

    // Evento para el botón de limpiar filtro (si existe)
    const btnLimpiarFiltro = document.getElementById('btnLimpiarFiltro');
    if (btnLimpiarFiltro) {
        btnLimpiarFiltro.addEventListener('click', function () {
            document.getElementById('fechaFiltro').value = '';
            filtrarExamenesPorFecha();
        });
    }

    const fechaDesdeExamenes = document.getElementById('fechaDesdeExamenes');
    const fechaHastaExamenes = document.getElementById('fechaHastaExamenes');

    if (fechaDesdeExamenes && fechaHastaExamenes) {
        fechaDesdeExamenes.addEventListener('change', filtrarExamenesPorFecha);
        fechaHastaExamenes.addEventListener('change', filtrarExamenesPorFecha);
    }
});

function initializeExamenesForm() {
    const searchForm = document.getElementById('searchExamenesForm');
    const nivelAspiranteSelect = document.getElementById('nivelAspirante');
    const estadoSelect = document.getElementById('estado');
    const categoriaAlumnoSelect = document.getElementById('categoriaAlumno');
    const yearSelect = document.getElementById('yearExamen');

    if (nivelAspiranteSelect) {
        nivelAspiranteSelect.addEventListener('change', realizarBusqueda);
    }
    if (estadoSelect) {
        estadoSelect.addEventListener('change', realizarBusqueda);
    }
    if (categoriaAlumnoSelect) {
        categoriaAlumnoSelect.addEventListener('change', realizarBusqueda);
    }
    if (yearSelect) {
        yearSelect.addEventListener('change', realizarBusqueda);
    }
}

function realizarBusqueda() {
    const nivelAspirante = document.getElementById('nivelAspirante')?.value || '';
    const estado = document.getElementById('estado')?.value || '';
    const claseId = document.getElementById('claseSelect')?.value || ''; // Changed from categoriaAlumno
    const year = document.getElementById('yearExamen')?.value || '';
    
    const params = new URLSearchParams();
    
    if (nivelAspirante) params.append('nivelAspirante', nivelAspirante);
    if (estado) params.append('estado', estado);
    if (claseId) params.append('claseId', claseId);
    if (year) params.append('year', year);

    fetch(`/examenes/buscar?${params.toString()}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al filtrar exámenes');
            return response.text();
        })
        .then(html => {
            const tableContainer = document.querySelector('#tablaExamenes');
            if (tableContainer) {
                tableContainer.innerHTML = html;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al filtrar los exámenes');
        });
}

function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('.container').prepend(alertDiv);
}

// Agregar función para reiniciar filtros
function reiniciarFiltros() {
    const nivelAspiranteSelect = document.getElementById('nivelAspirante');
    const estadoSelect = document.getElementById('estado');
    const categoriaAlumnoSelect = document.getElementById('categoriaAlumno');
    const yearSelect = document.getElementById('yearExamen');

    // Reiniciar valores de los selectores
    if (nivelAspiranteSelect) nivelAspiranteSelect.value = '';
    if (estadoSelect) estadoSelect.value = '';
    if (categoriaAlumnoSelect) categoriaAlumnoSelect.value = '';
    if (yearSelect) yearSelect.value = '';

    realizarBusqueda();
}

// Initialize form when document is ready
document.addEventListener('DOMContentLoaded', initializeExamenesForm);

function verDetallesExamen(id) {
    fetch(`/examenes/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar los detalles');
            return response.text();
        })
        .then(html => {
            // Update just the modal-content
            const modalContent = document.querySelector('#detallesExamenModal .modal-content');
            modalContent.innerHTML = html;

            // Verifica el valor del estado
            const estadoExamen = modalContent.querySelector('.estado-examen');
            if (estadoExamen) {
                console.log(estadoExamen.textContent);
            }

            // Show the modal
            const modal = new bootstrap.Modal(document.getElementById('detallesExamenModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al cargar los detalles del examen');
        });
}


function buscarExamenes() {
    const formData = new FormData(document.getElementById('searchExamenesForm'));
    const params = new URLSearchParams(formData);

    fetch(`/api/examenes/buscar?${params.toString()}`)
        .then(response => response.json())
        .then(data => actualizarTablaExamenes(data))
        .catch(error => mostrarError('Error al buscar exámenes'));
}

function evaluarExamen(id) {
    fetch(`/examenes/${id}/evaluar`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar los detalles');
            return response.text();
        })
        .then(html => {
            const modalContent = document.querySelector('#evaluacionModal .modal-content');
            modalContent.innerHTML = html;

            // Show the modal
            const modal = new bootstrap.Modal(document.getElementById('evaluacionModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al cargar los detalles del examen');
        });
}

// This function submits the evaluation form
function guardarEvaluacion() {
    const form = document.getElementById('evaluacionForm');
    const id = form.querySelector('input[name="id"]').value;

    form.action = `/examenes/${id}/evaluar`;
    form.method = 'POST';
    form.submit();
}

function guardarObservaciones() {
    const modalContent = document.querySelector('#detallesExamenModal .modal-content');
    const observaciones = document.querySelector('#observaciones').value;

    // Obtener el ID del examen del atributo data-id del formulario
    const examenId = modalContent.querySelector('#detallesExamenForm').getAttribute('data-id');

    if (!examenId) {
        console.error('ID del examen no encontrado');
        mostrarError('Error: ID del examen no encontrado');
        return;
    }

    const formData = new FormData();
    formData.append('observaciones', observaciones);

    fetch(`/examenes/${examenId}/observaciones`, {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'text/html'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al guardar las observaciones');
            }
            return response.text();
        })
        .then(html => {
            // Actualizar el contenido del modal con la respuesta
            modalContent.innerHTML = html;
            // Mostrar mensaje de éxito
            mostrarMensajeExito('Observaciones guardadas correctamente');
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al guardar las observaciones');
        });
}

// Función auxiliar para mostrar mensajes de éxito
function mostrarMensajeExito(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('.container').prepend(alertDiv);
}

function eliminarExamen() {
    const modalContent = document.querySelector('#detallesExamenModal .modal-content');
    const examenId = modalContent.querySelector('#detallesExamenForm').getAttribute('data-id');

    if (!examenId) {
        mostrarError('Error: ID del examen no encontrado');
        return;
    }

    if (confirm('¿Está seguro de que desea eliminar este examen?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/examenes/${examenId}/eliminar`;

        document.body.appendChild(form);
        form.submit();
    }
}

// Función para filtrar exámenes por fecha (usando fetch como los otros módulos)
function filtrarExamenesPorFecha() {
    const fechaDesdeExamenes = document.getElementById('fechaDesdeExamenes').value;
    const fechaHastaExamenes = document.getElementById('fechaHastaExamenes').value;
    
    // Guardar filtros en localStorage
    if (fechaDesdeExamenes) localStorage.setItem('examenesFiltroPorFechaDesde', fechaDesdeExamenes);
    if (fechaHastaExamenes) localStorage.setItem('examenesFiltroPorFechaHasta', fechaHastaExamenes);
    
    // Construir URL - Modificado para prevenir redirección incorrecta
    let url = '/examenes/fecha';
    const params = new URLSearchParams();
    if (fechaDesdeExamenes) params.append('fechaDesdeExamenes', fechaDesdeExamenes);
    if (fechaHastaExamenes) params.append('fechaHastaExamenes', fechaHastaExamenes);
    
    if (params.toString()) {
        url += '?' + params.toString();
    }
    
    console.log('Filtrando exámenes con URL:', url);  // Para depuración
    
    // Usar fetch para obtener los resultados
    fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'text/html',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Error en la respuesta');
        return response.text();
    })
    .then(html => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        
        const tableContent = doc.querySelector('#tablaExamenes');
        if (tableContent) {
            document.querySelector('#tablaExamenes').innerHTML = tableContent.innerHTML;
            
            const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
            const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
            const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');
            
            if (typeof actualizarPaginacionExamenes === 'function') {
                actualizarPaginacionExamenes(totalElements, pageSize, currentPage);
            }
        } else {
            console.error('No se encontró el contenido de la tabla en la respuesta');
        }
    })
    .catch(error => {
        console.error('Error al filtrar exámenes por fecha:', error);
        mostrarError('Error al filtrar exámenes por fecha');
    });
    
    // Prevenir comportamiento por defecto (importante)
    return false;
}

function filtrarAlumnosPorCategoria() {
    const categoriaSeleccionada = document.getElementById('categoriaFiltro').value;
    const selectAlumnos = document.getElementById('alumnoIds');
    const opciones = selectAlumnos.options;

    for (let opcion of opciones) {
        const categoriaAlumno = opcion.getAttribute('data-categoria');
        if (!categoriaSeleccionada || categoriaAlumno === categoriaSeleccionada) {
            opcion.style.display = '';
        } else {
            opcion.style.display = 'none';
            opcion.selected = false;
        }
    }
}

// Añadir la inicialización del filtro cuando se abre el modal
document.addEventListener('DOMContentLoaded', function () {
    const nuevoExamenModal = document.getElementById('nuevoExamenModal');
    if (nuevoExamenModal) {
        nuevoExamenModal.addEventListener('show.bs.modal', function () {
            // Resetear el filtro
            document.getElementById('categoriaFiltro').value = '';
            // Mostrar todos los alumnos
            filtrarAlumnosPorCategoria();
        });
    }
});

// Función para mostrar mensajes
function mostrarMensaje(mensaje, tipo = "success") {
    const alertContainer = document.getElementById('alertContainer');
    if (alertContainer) {
        alertContainer.innerHTML = `
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
    }
}
