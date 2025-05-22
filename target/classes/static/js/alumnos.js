document.addEventListener('DOMContentLoaded', function () {
    console.log('Initializing alumnos.js');
    initializeSearchForm();
    inicializarBotonesPaginacion();
    reinicializarBotones();
});

function initializeSearchForm() {
    const alumnoSearch = document.getElementById('searchNombre');
    const categoriaSelect = document.getElementById('searchCategoria');
    const nivelSelect = document.getElementById('searchNivel');
    
    // Event listeners
    if (alumnoSearch) {
        alumnoSearch.addEventListener('input', debounce(() => {
            actualizarTablaAlumnos();
        }, 300));
    }

    if (categoriaSelect) {
        categoriaSelect.addEventListener('change', () => {
            actualizarTablaAlumnos();
        });
    }

    if (nivelSelect) {
        nivelSelect.addEventListener('change', () => {
            actualizarTablaAlumnos();
        });
    }
}

function inicializarBotonesPaginacion() {
    // Asegurarse de que los botones de paginación tengan el evento correcto
    document.querySelectorAll('.pagination .page-link').forEach(link => {
        link.addEventListener('click', function(e) {
            // Prevenir la navegación predeterminada
            e.preventDefault();
            
            // Obtener la página del atributo data-page si existe
            const page = this.getAttribute('data-page');
            if (page) {
                cambiarPagina(page);
            }
        });
    });
}

function realizarBusqueda(event) {
    if (event) {
        event.preventDefault();
    }
    
    const nombre = document.getElementById('searchNombre')?.value?.trim() || '';
    const categoria = document.getElementById('searchCategoria')?.value || '';
    const nivel = document.getElementById('searchNivel')?.value || '';
    
    console.log('Realizando búsqueda con:', { nombre, categoria, nivel });
    
    const params = new URLSearchParams();
    if (nombre) params.append('nombre', nombre);
    if (categoria) params.append('categoria', categoria);
    if (nivel) params.append('nivelCinturon', nivel);
    params.append('page', '0');
    
    mostrarIndicadorCarga(true);
    
    fetch(`/alumnos/buscar?${params.toString()}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al filtrar alumnos');
            return response.text();
        })
        .then(html => {
            const tableContainer = document.querySelector('#tableContainer');
            if (tableContainer) {
                tableContainer.innerHTML = html;
                reinicializarBotones();
                inicializarBotonesPaginacion();
                
                // Actualizar la URL sin recargar la página
                window.history.pushState({}, '', `/alumnos/buscar?${params.toString()}`);
            }
            mostrarIndicadorCarga(false);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarIndicadorCarga(false);
            mostrarError('Error al filtrar los alumnos');
        });
}

function reinicializarBotones() {
    console.log('Reinicializando botones de la tabla');
    
    document.querySelectorAll('#tablaAlumnos button').forEach(button => {
        // Si ya tiene un onclick definido, no hacer nada
        if (button.hasAttribute('onclick')) {
            return;
        }
        
        // Botón de editar (azul/primary)
        if (button.classList.contains('btn-primary')) {
            button.onclick = function() { editarAlumno(this); };
        }
        // Botón de eliminar (rojo/danger)
        else if (button.classList.contains('btn-danger')) {
            const id = button.getAttribute('data-id');
            if (id) {
                button.onclick = function() { eliminarAlumno(id); };
            }
        }
    });
}

// Función para mostrar/ocultar el indicador de carga
function mostrarIndicadorCarga(mostrar) {
    const tableContainer = document.querySelector('#tableContainer');
    if (!tableContainer) return;
    
    // Eliminar indicador anterior si existe
    const existingIndicator = document.getElementById('searchLoadingIndicator');
    if (existingIndicator) {
        existingIndicator.remove();
    }
    
    if (mostrar) {
        // Crear nuevo indicador
        const loadingIndicator = document.createElement('div');
        loadingIndicator.id = 'searchLoadingIndicator';
        loadingIndicator.className = 'text-center my-3';
        loadingIndicator.innerHTML = '<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Buscando...</span></div>';
        
        // Añadir al DOM antes de la tabla
        const tablaAlumnos = document.querySelector('#tablaAlumnos');
        if (tablaAlumnos) {
            tableContainer.insertBefore(loadingIndicator, tablaAlumnos);
        } else {
            tableContainer.appendChild(loadingIndicator);
        }
        
        // Añadir clase de carga al contenedor
        tableContainer.classList.add('loading');
    } else {
        // Quitar clase de carga
        tableContainer.classList.remove('loading');
    }
}

// Función para actualizar la tabla con el HTML recibido
function actualizarTablaConHTML(html) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, 'text/html');
    
    // El controlador devuelve el fragmento tablaAlumnos, pero necesitamos extraer su contenido
    // Si el HTML contiene el fragmento completo
    let tablaAlumnos = doc.querySelector('#tablaAlumnos');
    
    // Si no encontramos el elemento con ID, es posible que el HTML sea directamente el contenido del fragmento
    if (!tablaAlumnos) {
        // Crear un contenedor temporal
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = html;
        tablaAlumnos = tempContainer;
    }
    
    // Actualizar la tabla de alumnos
    if (tablaAlumnos) {
        const currentTable = document.querySelector('#tablaAlumnos');
        if (currentTable) {
            currentTable.innerHTML = tablaAlumnos.innerHTML;
            console.log('Tabla actualizada correctamente');
            
            // Reinicializar botones
            reinicializarBotones();
        }
    } else {
        console.error('No se pudo procesar la respuesta HTML');
        return; // Salir si no se puede procesar la respuesta
    }
    
    // Actualizar la paginación
    // Buscar la paginación en la respuesta o en el documento actual
    let paginacion = doc.querySelector('.pagination');
    
    // Si no encontramos la paginación en la respuesta, intentar cargarla desde la página actual
    if (!paginacion) {
        // Hacer una petición adicional para obtener la página completa con la paginación
        const currentUrl = window.location.href;
        fetch(currentUrl)
            .then(response => response.text())
            .then(html => {
                const fullDoc = new DOMParser().parseFromString(html, 'text/html');
                const fullPagination = fullDoc.querySelector('.pagination');
                
                if (fullPagination) {
                    const currentPagination = document.querySelector('.pagination');
                    if (currentPagination) {
                        currentPagination.innerHTML = fullPagination.innerHTML;
                        console.log('Paginación actualizada correctamente');
                        
                        // Reinicializar los botones de paginación
                        inicializarBotonesPaginacion();
                    }
                }
            })
            .catch(error => console.error('Error al cargar la paginación:', error));
    } else if (paginacion) {
        const currentPagination = document.querySelector('.pagination');
        if (currentPagination) {
            currentPagination.innerHTML = paginacion.innerHTML;
            console.log('Paginación actualizada correctamente');
            
            // Reinicializar los botones de paginación
            inicializarBotonesPaginacion();
        }
    }
    
    // Actualizar la URL del navegador sin recargar la página
    // Obtener los parámetros actuales de búsqueda
    const url = new URL(window.location.href);
    const nombreValue = document.getElementById('searchNombre')?.value || '';
    const categoriaValue = document.getElementById('searchCategoria')?.value || '';
    const nivelValue = document.getElementById('searchNivel')?.value || '';
    const pageValue = document.querySelector('#tablaAlumnos')?.getAttribute('data-current-page') || '0';
    
    // Actualizar los parámetros de búsqueda en la URL
    if (nombreValue) url.searchParams.set('nombre', nombreValue);
    else url.searchParams.delete('nombre');
    
    if (categoriaValue) url.searchParams.set('categoria', categoriaValue);
    else url.searchParams.delete('categoria');
    
    if (nivelValue) url.searchParams.set('nivelCinturon', nivelValue);
    else url.searchParams.delete('nivelCinturon');
    
    if (pageValue && pageValue !== '0') url.searchParams.set('page', pageValue);
    else url.searchParams.delete('page');
    
    // Actualizar la URL sin recargar la página
    window.history.pushState({}, '', url);
}

function reiniciarFiltrosAlumnos() {
    document.getElementById('searchNombre').value = '';
    document.getElementById('searchCategoria').value = '';
    document.getElementById('searchNivel').value = '';
    actualizarTablaAlumnos();
}

// Función debounce para evitar múltiples llamadas
function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            func.apply(context, args);
        }, wait);
    };
}

// Función para cambiar página
function cambiarPagina(page) {
    const nombre = document.getElementById('searchNombre')?.value?.trim() || '';
    const categoria = document.getElementById('searchCategoria')?.value || '';
    const nivel = document.getElementById('searchNivel')?.value || '';

    const params = new URLSearchParams();
    params.append('page', page);
    if (nombre) params.append('nombre', nombre);
    if (categoria) params.append('categoria', categoria);
    if (nivel) params.append('nivelCinturon', nivel);

    mostrarIndicadorCarga(true);

    fetch(`/alumnos/buscar?${params.toString()}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cambiar de página');
            return response.text();
        })
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            const tableContent = doc.querySelector('#tablaAlumnos');
            if (tableContent) {
                document.querySelector('#tablaAlumnos').innerHTML = tableContent.innerHTML;
                reinicializarBotones();
                
                // Actualizar la paginación
                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');
                
                actualizarPaginacion(totalElements, pageSize, currentPage);
            }
            mostrarIndicadorCarga(false);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarIndicadorCarga(false);
            mostrarError('Error al cambiar de página');
        });
}

// Función para reinicializar los botones de la tabla
function reinicializarBotones() {
    console.log('Reinicializando botones de la tabla');
    
    document.querySelectorAll('#tablaAlumnos button').forEach(button => {
        // Si ya tiene un onclick definido, no hacer nada
        if (button.hasAttribute('onclick')) {
            return;
        }
        
        // Botón de editar (azul/primary)
        if (button.classList.contains('btn-primary')) {
            button.onclick = function() { editarAlumno(this); };
        }
        // Botón de eliminar (rojo/danger)
        else if (button.classList.contains('btn-danger')) {
            const id = button.getAttribute('data-id');
            if (id) {
                button.onclick = function() { eliminarAlumno(id); };
            }
        }
    });
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    console.error(mensaje);
    
    // Eliminar alerta anterior si existe
    const existingAlert = document.getElementById('errorAlert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    // Crear nueva alerta de error
    const errorAlert = document.createElement('div');
    errorAlert.id = 'errorAlert';
    errorAlert.className = 'alert alert-danger alert-dismissible fade show mt-3';
    errorAlert.setAttribute('role', 'alert');
    
    // Agregar mensaje
    errorAlert.appendChild(document.createTextNode(mensaje));
    
    // Agregar botón para cerrar la alerta
    const closeButton = document.createElement('button');
    closeButton.type = 'button';
    closeButton.className = 'btn-close';
    closeButton.setAttribute('data-bs-dismiss', 'alert');
    closeButton.setAttribute('aria-label', 'Close');
    errorAlert.appendChild(closeButton);
    
    // Insertar la alerta antes de la tabla o al inicio del contenedor
    const tableContainer = document.querySelector('#tableContainer');
    if (tableContainer) {
        const tablaAlumnos = document.querySelector('#tablaAlumnos');
        if (tablaAlumnos) {
            tableContainer.insertBefore(errorAlert, tablaAlumnos);
        } else {
            tableContainer.prepend(errorAlert);
        }
    } else {
        // Si no hay tableContainer, insertar al inicio del contenido principal
        const mainContent = document.querySelector('main .container');
        if (mainContent) {
            mainContent.prepend(errorAlert);
        }
    }
    
    // Configurar temporizador para ocultar la alerta después de 5 segundos
    setTimeout(() => {
        if (errorAlert && errorAlert.parentNode) {
            errorAlert.parentNode.removeChild(errorAlert);
        }
    }, 5000);
    
    // Hacer scroll hasta la alerta
    errorAlert.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// Función para actualizar la paginación
function actualizarPaginacion(totalElements, pageSize, currentPage) {
    const totalPages = Math.ceil(totalElements / pageSize);
    
    // Buscar o crear el contenedor de paginación
    let paginationContainer = document.querySelector('.d-flex.justify-content-center.mt-4');
    
    if (totalElements > pageSize) {
        const html = generarPaginacionHTML(totalPages, currentPage);
        
        if (paginationContainer) {
            paginationContainer.innerHTML = html;
        } else {
            paginationContainer = document.createElement('div');
            paginationContainer.className = 'd-flex justify-content-center mt-4';
            paginationContainer.innerHTML = html;
            document.querySelector('#tablaAlumnos').insertAdjacentElement('afterend', paginationContainer);
        }
    } else if (paginationContainer) {
        paginationContainer.remove();
    }
}

function generarPaginacionHTML(totalPages, currentPage) {
    let html = '<nav aria-label="Navegación de páginas"><ul class="pagination">';
    
    // Primera página
    html += `<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="cambiarPagina(0)">&laquo;</a></li>`;
    
    // Anterior
    html += `<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="cambiarPagina(${currentPage - 1})">&lt;</a></li>`;
    
    // Páginas numeradas
    for (let i = 0; i < totalPages; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPagina(${i})">${i + 1}</a></li>`;
    }
    
    // Siguiente
    html += `<li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="cambiarPagina(${currentPage + 1})">&gt;</a></li>`;
    
    // Última página
    html += `<li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="cambiarPagina(${totalPages - 1})">&raquo;</a></li>`;
    
    html += '</ul></nav>';
    return html;
}

// Función debounce mejorada
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Función para mostrar errores
function mostrarError(mensaje) {
    const alertContainer = document.createElement('div');
    alertContainer.className = 'alert alert-danger alert-dismissible fade show';
    alertContainer.setAttribute('role', 'alert');
    alertContainer.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
    `;
    
    // Insertar al principio del contenedor principal
    const mainContainer = document.querySelector('main.container');
    if (mainContainer) {
        mainContainer.insertBefore(alertContainer, mainContainer.firstChild);
        
        // Auto-eliminar después de 5 segundos
        setTimeout(() => {
            alertContainer.classList.remove('show');
            setTimeout(() => alertContainer.remove(), 300);
        }, 5000);
    }
}

function reinicializarBotones() {
    document.querySelectorAll('button.btn-outline-primary').forEach(button => {
        button.onclick = function() { editarAlumno(this); };
    });
    document.querySelectorAll('button.btn-outline-danger').forEach(button => {
        const id = button.getAttribute('data-id');
        button.onclick = function() { eliminarAlumno(id); };
    });
}

function actualizarTablaAlumnos() {
    const nombre = document.getElementById('searchNombre')?.value?.trim() || '';
    const categoria = document.getElementById('searchCategoria')?.value || '';
    const nivel = document.getElementById('searchNivel')?.value || '';

    // Construir URL base y parámetros
    let url = '/alumnos/buscar';
    const params = new URLSearchParams();

    // Añadir parámetros solo si tienen valor
    if (nombre) params.append('nombre', nombre);
    if (categoria) params.append('categoria', categoria);
    if (nivel) params.append('nivelCinturon', nivel);

    // Añadir parámetros a la URL si existen
    if (params.toString()) {
        url += '?' + params.toString();
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.text();
        })
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            const tableContent = doc.querySelector('#tablaAlumnos');
            if (tableContent) {
                document.querySelector('#tablaAlumnos').innerHTML = tableContent.innerHTML;
                reinicializarBotones();
                inicializarBotonesPaginacion();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al filtrar los alumnos');
        });
}

// Función para guardar un alumno (crear o editar)
function guardarAlumno() {
    const form = document.getElementById('alumnoForm');
    form.submit();
}

function editarAlumno(button) {
    // First show the modal
    const modal = new bootstrap.Modal(document.getElementById('nuevoAlumnoModal'));
    modal.show();

    // Then get the data attributes
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const fechaNacimiento = button.getAttribute('data-fecha-nacimiento');
    const email = button.getAttribute('data-email');
    const telefono = button.getAttribute('data-telefono');
    const nivelCinturon = button.getAttribute('data-nivel-cinturon');
    const categoria = button.getAttribute('data-categoria');

    // Wait for modal to be shown before trying to access form elements
    modal._element.addEventListener('shown.bs.modal', function () {
        const form = document.getElementById('alumnoForm');
        if (form) {
            // Establecer la acción del formulario para actualizar
            form.action = `/alumnos`;

            // Rellenar los campos del formulario con los datos del alumno
            const formFields = {
                'id': id,
                'nombre': nombre,
                'fechaNacimiento': fechaNacimiento,
                'email': email,
                'telefono': telefono,
                'nivelCinturon': nivelCinturon,
                'categoria': categoria
            };

            // Update each field if it exists
            Object.entries(formFields).forEach(([key, value]) => {
                const field = form.querySelector(`[name="${key}"]`);
                if (field) {
                    field.value = value || '';
                }
            });

            // Actualizar el título del modal
            modal._element.querySelector('.modal-title').textContent = 'Editar Alumno';
        }
    }, { once: true }); // Remove listener after first execution
}

// Función para preparar el modal para crear un nuevo alumno
function nuevoAlumno() {
    const modal = new bootstrap.Modal(document.getElementById('nuevoAlumnoModal'));
    const form = document.getElementById('alumnoForm');

    // Resetear el formulario
    form.reset();

    // Limpiar el campo oculto de ID
    form.querySelector('input[name="id"]').value = '';

    // Establecer la acción del formulario para crear
    form.action = '/alumnos';

    // Actualizar el título del modal
    modal._element.querySelector('.modal-title').textContent = 'Nuevo Alumno';

    // Mostrar el modal
    modal.show();
}

function toggleActivoAlumno(id) {
    fetch(`/alumnos/${id}/toggle-activo`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al cambiar el estado del alumno');
        }
        return response.text();
    })
    .then(html => {
        // Actualizar la tabla con el HTML devuelto
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        
        const tableContent = doc.querySelector('.table-responsive');
        if (tableContent) {
            document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al cambiar el estado del alumno');
    });
}

// Función para mostrar mensajes (si no la tienes ya)
function mostrarMensaje(mensaje, tipo = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('.container').prepend(alertDiv);
    
    // Auto-cerrar después de 3 segundos
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// Función para eliminar un alumno
function eliminarAlumno(id) {
    if (confirm('¿Está seguro de eliminar este alumno?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/alumnos/${id}/eliminar`;
        // Si estás usando Spring Security, asegúrate de incluir el token CSRF aquí
        document.body.appendChild(form);
        form.submit();
    }
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show mt-3';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('#searchForm').insertAdjacentElement('afterend', alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}
