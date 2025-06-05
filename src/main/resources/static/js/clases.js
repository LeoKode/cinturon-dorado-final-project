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
        btnEliminar.addEventListener('click', function () {
            const claseId = document.getElementById('claseId').value;
            if (claseId && confirm('¿Estás seguro de eliminar esta clase?')) {
                eliminarClase(claseId);
            }
        });
    }

    // Agregar event listener para el formulario de crear clase
    const asignarClaseForm = document.getElementById('asignarClaseForm');
    if (asignarClaseForm) {
        asignarClaseForm.addEventListener('submit', function (e) {
            e.preventDefault();
            asignarClase(this);
        });
    }

    // Event delegation para las celdas de clase
    document.addEventListener('click', function (e) {
        const celda = e.target.closest('.clase-celda');
        if (celda && !e.target.closest('.clase-badge')) {
            // Verificar si la celda está vacía (no tiene clase asignada)
            const claseBadge = celda.querySelector('.clase-badge');
            if (!claseBadge) {
                // Solo mostrar el modal de asignar si la celda está vacía
                const modal = new bootstrap.Modal(document.getElementById('claseAsignarModal'));
                mostrarModalClase(celda);
                modal.show();
            }
        }
    });

    // Event delegation para los badges de gestión de alumnos
    document.addEventListener('click', function (e) {
        const badge = e.target.closest('.badge');
        if (badge && badge.classList.contains('clase-badge')) {
            e.stopPropagation();
            const claseId = badge.getAttribute('data-clase-id');
            const dia = badge.getAttribute('data-dia');
            const hora = badge.getAttribute('data-hora');
            eliminarAsignacion(claseId, dia, hora);
        }
    });

    // Event listener para el botón de nueva clase
    const btnNuevaClase = document.getElementById('btnNuevaClase');
    if (btnNuevaClase) {
        btnNuevaClase.addEventListener('click', function () {
            const modal = new bootstrap.Modal(document.getElementById('claseModal'));
            const form = document.getElementById('claseForm');
            if (form) {
                form.reset();
                document.getElementById('claseId').value = '';
            }
            modal.show();
        });
    }
});

function mostrarModalClase(celda) {
    const dia = celda.getAttribute('data-dia');
    const hora = celda.getAttribute('data-hora');

    // Actualizar los campos ocultos en el modal
    const modal = document.getElementById('claseAsignarModal');
    if (modal) {
        const diaInput = modal.querySelector('input[name="dia"]');
        const horaInput = modal.querySelector('input[name="hora"]');
        if (diaInput) diaInput.value = dia;
        if (horaInput) horaInput.value = hora;

        // Actualizar el título del modal
        const titulo = modal.querySelector('.modal-title');
        if (titulo) {
            titulo.textContent = `Asignar Clase - ${dia} ${hora}`;
        }
    }
}

function asignarClase(form) {
    fetch(form.action, {
        method: 'POST',
        body: new FormData(form)
    })
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');

            // Verificar si hay error
            const errorElement = doc.querySelector('.alert-danger');
            if (errorElement) {
                mostrarError(errorElement.textContent.trim());
                return;
            }

            // Recargar la lista completa de clases
            fetch('/clases')
                .then(response => response.text())
                .then(fullHtml => {
                    const fullDoc = new DOMParser().parseFromString(fullHtml, 'text/html');

                    // Actualizar el contenedor de clases disponibles
                    const clasesContainer = fullDoc.querySelector('#clasesContainer');
                    if (clasesContainer) {
                        document.querySelector('#clasesContainer').innerHTML = clasesContainer.innerHTML;
                    }

                    // Actualizar la tabla de horarios
                    const tablaClases = fullDoc.querySelector('.table-responsive');
                    if (tablaClases) {
                        document.querySelector('.table-responsive').innerHTML = tablaClases.innerHTML;
                    }

                    // Actualizar los modales
                    actualizarModales(fullDoc);

                    // Cerrar el modal
                    bootstrap.Modal.getInstance(document.getElementById('claseAsignarModal')).hide();

                    // Mostrar mensaje de éxito
                    mostrarMensaje('Clase asignada correctamente', 'success');

                    // Limpiar el formulario
                    form.reset();
                });
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al asignar la clase');
        });
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

function agregarNuevoHorario() {
    const horarioInput = document.getElementById('nuevoHorario');
    const hora = horarioInput.value;

    if (!hora) {
        mostrarError('Debe ingresar una hora válida');
        return;
    }

    fetch('/clases/horario/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `hora=${hora}`
    })
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');

            // Verificar si hay error
            const errorElement = doc.querySelector('.alert-danger');
            if (errorElement) {
                mostrarError(errorElement.textContent.trim());
                return;
            }

            // Recargar la lista completa de clases
            fetch('/clases')
                .then(response => response.text())
                .then(fullHtml => {
                    const fullDoc = new DOMParser().parseFromString(fullHtml, 'text/html');

                    // Actualizar la tabla de horarios
                    const tablaClases = fullDoc.querySelector('.table-responsive');
                    if (tablaClases) {
                        document.querySelector('.table-responsive').innerHTML = tablaClases.innerHTML;
                    }

                    // Cerrar el modal
                    bootstrap.Modal.getInstance(document.getElementById('nuevoHorarioModal')).hide();

                    // Mostrar mensaje de éxito
                    mostrarMensaje('Horario agregado correctamente', 'success');

                    // Limpiar el formulario
                    document.getElementById('horarioForm').reset();
                });
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al agregar el horario');
        });
}

function actualizarModales(fullDoc) {
    // Actualizar select en modal de asignación
    const selectClaseOriginal = fullDoc.querySelector('#claseSelect');
    const selectClaseModal = document.querySelector('#claseSelect');
    if (selectClaseOriginal && selectClaseModal) {
        const valorSeleccionado = selectClaseModal.value; // Guardar valor actual
        selectClaseModal.innerHTML = selectClaseOriginal.innerHTML;
        if (valorSeleccionado) {
            selectClaseModal.value = valorSeleccionado; // Restaurar valor si existe
        }
    }
}

function guardarClase(form) {
    fetch(form.action, {
        method: 'POST',
        body: new FormData(form)
    })
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');

            // Buscar mensajes de error
            const errorElement = doc.querySelector('.alert-danger');
            if (errorElement) {
                mostrarError(errorElement.textContent.trim());
                return;
            }

            // Recargar la lista completa de clases
            fetch('/clases')
                .then(response => response.text())
                .then(fullHtml => {
                    const fullDoc = new DOMParser().parseFromString(fullHtml, 'text/html');

                    // Actualizar el contenedor de clases
                    const clasesContainer = fullDoc.querySelector('#clasesContainer');
                    if (clasesContainer) {
                        document.querySelector('#clasesContainer').innerHTML = clasesContainer.innerHTML;
                    }

                    // Actualizar los modales
                    actualizarModales(fullDoc);

                    // Cerrar el modal
                    bootstrap.Modal.getInstance(document.getElementById('claseModal'))?.hide();

                    // Mostrar mensaje de éxito
                    mostrarMensaje('Clase guardada correctamente', 'success');

                    // Limpiar el formulario
                    form.reset();
                });
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al guardar la clase');
        });
}

function eliminarClase(id) {
    if (confirm('¿Estás seguro de que deseas eliminar esta clase?')) {
        fetch(`/clases/${id}/eliminar`, {
            method: 'POST'
        })
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');

                // Verificar si hay error
                const errorElement = doc.querySelector('.alert-danger');
                if (errorElement) {
                    mostrarError(errorElement.textContent.trim());
                    return;
                }

                // Recargar la lista completa de clases
                fetch('/clases')
                    .then(response => response.text())
                    .then(fullHtml => {
                        const fullDoc = new DOMParser().parseFromString(fullHtml, 'text/html');

                        // Actualizar el contenedor de clases
                        const clasesContainer = fullDoc.querySelector('#clasesContainer');
                        if (clasesContainer) {
                            document.querySelector('#clasesContainer').innerHTML = clasesContainer.innerHTML;
                        }

                        // Actualizar la tabla de horarios
                        const tablaClases = fullDoc.querySelector('.table-responsive');
                        if (tablaClases) {
                            document.querySelector('.table-responsive').innerHTML = tablaClases.innerHTML;
                        }

                        // Actualizar los modales
                        actualizarModales(fullDoc);

                        // Cerrar el modal si está abierto
                        bootstrap.Modal.getInstance(document.getElementById('claseModal'))?.hide();

                        // Mostrar mensaje de éxito
                        mostrarMensaje('Clase eliminada correctamente', 'success');
                    });
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al eliminar la clase');
            });
    }
}

function eliminarAsignacion(claseId, dia, hora) {
    if (confirm('¿Estás seguro de que deseas eliminar esta asignación?')) {
        fetch(`/clases/${claseId}/desasignar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'text/html'
            },
            body: new URLSearchParams({
                dia: dia,
                hora: hora
            }).toString()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al eliminar la asignación');
            }
            return response.text();
        })
        .then(html => {
            actualizarVista();
            mostrarMensaje('Asignación eliminada correctamente', 'success');
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al eliminar la asignación');
        });
    }
}

function actualizarVista() {
    fetch('/clases')
        .then(response => response.text())
        .then(fullHtml => {
            const fullDoc = new DOMParser().parseFromString(fullHtml, 'text/html');

            // Actualizar el contenedor de clases
            const clasesContainer = fullDoc.querySelector('#clasesContainer');
            if (clasesContainer) {
                document.querySelector('#clasesContainer').innerHTML = clasesContainer.innerHTML;
            }

            // Actualizar la tabla de horarios
            const tablaClases = fullDoc.querySelector('.table-responsive');
            if (tablaClases) {
                document.querySelector('.table-responsive').innerHTML = tablaClases.innerHTML;
            }
        });
}

// MODAL GESTIONAR ALUMNOS POR CLASE

function mostrarModalGestionAlumnos(claseId) {
    if (!claseId) {
        mostrarError('ID de clase no válido');
        return;
    }

    mostrarIndicadorCarga(true);

    fetch(`/clases/${claseId}/gestionar`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            return response.text();
        })
        .then(html => {
            mostrarIndicadorCarga(false);

            // Eliminar modal existente si hay uno
            const existingModal = document.querySelector('#gestionarAlumnosModal');
            if (existingModal) {
                existingModal.remove();
            }

            // Crear contenedor temporal y agregar el HTML
            const temp = document.createElement('div');
            temp.innerHTML = html;

            // Verificar si hay mensaje de error
            const errorElement = temp.querySelector('.alert-danger, .error-message');
            if (errorElement) {
                mostrarError(errorElement.textContent.trim());
                return;
            }

            // Verificar que tenemos el modal y la clase
            const modalElement = temp.querySelector('.modal');
            if (!modalElement) {
                throw new Error('Error: No se pudo cargar el modal');
            }

            // Verificar que tenemos el ID de la clase
            const gestionClaseId = temp.querySelector('#gestionClaseId');
            if (!gestionClaseId || !gestionClaseId.value) {
                throw new Error('Error: No se pudo obtener el ID de la clase');
            }

            // Añadir el modal al body
            document.body.appendChild(modalElement);

            // Inicializar y mostrar el modal
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarIndicadorCarga(false);
            mostrarError('Error al cargar la gestión de alumnos: ' + error.message);
        });
}

function inscribirAlumnoEnClaseSeleccionado() {
    const claseId = document.getElementById('gestionClaseId').value;
    const alumnoIdSelect = document.getElementById('selectAlumnosDisponibles');
    const alumnoId = alumnoIdSelect.value;

    if (!alumnoId) {
        mostrarError('Por favor, seleccione un alumno para agregar.');
        return;
    }

    fetch(`/clases/${claseId}/inscribir?alumnoId=${alumnoId}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al inscribir alumno');
            }
            return response.text();
        })
        .then(html => {
            // Actualizar el contenido del modal con la respuesta
            const modalContent = document.querySelector('#gestionarAlumnosModal .modal-content');
            if (modalContent) {
                modalContent.innerHTML = html;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al inscribir alumno');
        });
}

function desinscribirAlumnoDeClase(claseId, alumnoId) {
    if (!confirm(`¿Está seguro de que desea quitar al alumno de esta clase?`)) {
        return;
    }
    console.log(`Intentando desinscribir alumno ${alumnoId} de clase ${claseId}`);
    // Aquí irá la llamada fetch al endpoint POST para desinscribir
    // Por ahora, solo recargamos el modal para simular el cambio
    // mostrarModalGestionAlumnos(claseId);
    alert('Funcionalidad de desinscribir alumno pendiente de implementación en backend.');
}

// Función para mostrar el indicador de carga
function mostrarIndicadorCarga(mostrar) {
    let loadingIndicator = document.getElementById('loadingIndicator');
    if (!loadingIndicator) {
        loadingIndicator = document.createElement('div');
        loadingIndicator.id = 'loadingIndicator';
        loadingIndicator.className = 'position-fixed top-50 start-50 translate-middle';
        loadingIndicator.innerHTML = `
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
        `;
        document.body.appendChild(loadingIndicator);
    }
    loadingIndicator.style.display = mostrar ? 'flex' : 'none';
}

function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);

    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 300);
    }, 3000);
}

function mostrarMensaje(mensaje, tipo = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);

    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 300);
    }, 3000);
}

// Función para editar una clase existente
function editarClase(id) {
    // Mostrar indicador de carga
    mostrarIndicadorCarga(true);

    // Obtener los datos de la clase
    fetch(`/clases/editar/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener datos de la clase');
            return response.text();
        })
        .then(html => {
            // Ocultar indicador de carga
            mostrarIndicadorCarga(false);

            // Buscar o crear el contenedor del modal
            let modalContainer = document.getElementById('modalEditarContainer');
            if (!modalContainer) {
                modalContainer = document.createElement('div');
                modalContainer.id = 'modalEditarContainer';
                document.body.appendChild(modalContainer);
            }

            // Insertar el HTML del modal
            modalContainer.innerHTML = html;

            // Mostrar el modal
            const modal = new bootstrap.Modal(document.getElementById('claseEditarModal'));
            modal.show();

            // Asegurarse de que el formulario tenga el event listener correcto
            const form = document.getElementById('claseEditarForm');
            if (form) {
                form.addEventListener('submit', function (e) {
                    e.preventDefault();
                    guardarClase(this);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarIndicadorCarga(false);
            mostrarError('Error al cargar los datos de la clase');
        });
}

// Función para mostrar el indicador de carga
function mostrarIndicadorCarga(mostrar) {
    let loadingIndicator = document.getElementById('loadingIndicator');
    if (!loadingIndicator) {
        loadingIndicator = document.createElement('div');
        loadingIndicator.id = 'loadingIndicator';
        loadingIndicator.className = 'position-fixed top-50 start-50 translate-middle';
        loadingIndicator.innerHTML = `
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
        `;
        document.body.appendChild(loadingIndicator);
    }
    loadingIndicator.style.display = mostrar ? 'flex' : 'none';
}