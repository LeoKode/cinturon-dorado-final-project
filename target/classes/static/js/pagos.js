document.addEventListener('DOMContentLoaded', function () {
    initializePagosForm();
    initializeDataTable();
});

function initializePagosForm() {
    const alumnoSearch = document.getElementById('alumnoSearch');
    const estadoSelect = document.getElementById('estadoSelect');
    const mesPago = document.getElementById('mesPago');

    // Configurar el límite máximo para el input de mes
    const now = new Date();
    const maxDate = now.toISOString().slice(0, 7); // Formato YYYY-MM
    mesPago.setAttribute('max', maxDate);

    // Event listeners
    if (alumnoSearch) {
        alumnoSearch.addEventListener('input', actualizarTablaPagos);
    }

    if (estadoSelect) {
        estadoSelect.addEventListener('change', actualizarTablaPagos);
    }

    if (mesPago) {
        mesPago.addEventListener('change', function () {
            const selectedDate = new Date(this.value + '-01');
            const currentDate = new Date(maxDate + '-01');

            if (selectedDate > currentDate) {
                this.value = maxDate;
                mostrarError('No puede seleccionar fechas futuras');
            }
            actualizarTablaPagos();
        });
    }
}

// Listener para el botón de aplicar filtros
const btnAplicarFiltros = document.getElementById('btnAplicarFiltros');
if (btnAplicarFiltros) {
    btnAplicarFiltros.addEventListener('click', actualizarTablaPagos);
}
// Cierre de la función initializePagosForm

function actualizarTablaPagos() {
    const mesPago = document.getElementById('mesPago').value;
    const estado = document.getElementById('estadoSelect').value;
    const nombre = document.getElementById('alumnoSearch').value;

    // Construir URL base y parámetros
    let url = '/pagos/buscar';
    const params = new URLSearchParams();

    // Añadir parámetros solo si tienen valor
    if (mesPago) params.append('mesPago', mesPago);
    if (estado) params.append('estado', estado);
    if (nombre) params.append('nombre', nombre);

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

            const tableContent = doc.querySelector('.table-responsive');
            if (tableContent) {
                document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;

                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');

                actualizarPaginacionPagos(totalElements, pageSize, currentPage);
            }
        })
        .catch(error => {
            console.error('Error al actualizar la tabla:', error);
            mostrarError('Error al cargar los datos. Por favor, inténtelo de nuevo.');
        });
}

// Función para actualizar la paginación de pagos
function actualizarPaginacionPagos(totalElements, pageSize, currentPage) {
    const totalPages = Math.ceil(totalElements / pageSize);

    // Buscar o crear el contenedor de paginación
    let paginationContainer = document.querySelector('.d-flex.justify-content-center.mt-4');

    if (totalElements > pageSize) {
        // Si hay más elementos que la página, mostrar paginación
        const paginationHTML = generarPaginacionPagos(totalPages, currentPage);

        if (paginationContainer) {
            // Actualizar paginación existente
            paginationContainer.innerHTML = paginationHTML;
            paginationContainer.style.display = 'flex';
        } else {
            // Crear nueva paginación
            paginationContainer = document.createElement('div');
            paginationContainer.className = 'd-flex justify-content-center mt-4';
            paginationContainer.innerHTML = paginationHTML;
            document.querySelector('.table-responsive').insertAdjacentElement('afterend', paginationContainer);
        }
    } else if (paginationContainer) {
        // Ocultar paginación si no hay suficientes elementos
        paginationContainer.style.display = 'none';
    }
}

function generarPaginacionPagos(totalPages, currentPage) {
    let html = '<nav aria-label="Navegación de páginas"><ul class="pagination">';

    // Primera página
    html += `<li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaPagos(0)">&laquo;</a>
        </li>`;

    // Anterior
    html += `<li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaPagos(${currentPage - 1})">&lt;</a>
        </li>`;

    // Números de página
    for (let i = 0; i < totalPages; i++) {
        html += `<li class="page-item ${i == currentPage ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaPagos(${i})">${i + 1}</a>
            </li>`;
    }

    // Siguiente
    html += `<li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaPagos(${currentPage + 1})">&gt;</a>
        </li>`;

    // Última página
    html += `<li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaPagos(${totalPages - 1})">&raquo;</a>
        </li>`;

    html += '</ul></nav>';
    return html;
}

// Modificar la función cambiarPaginaPagos para usar la nueva función
function cambiarPaginaPagos(page) {
    const mesPago = document.getElementById('mesPago').value;
    const estado = document.getElementById('estadoSelect').value;
    const nombre = document.getElementById('alumnoSearch').value;

    // Construir los parámetros usando URLSearchParams
    const params = new URLSearchParams();

    // Añadir parámetros solo si tienen valor
    params.append('page', page);
    if (mesPago) params.append('mesPago', mesPago);
    if (estado) params.append('estado', estado);
    if (nombre) params.append('nombre', nombre);

    // Construir la URL base y añadir los parámetros
    const url = `/pagos/buscar?${params.toString()}`;

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

            const tableContent = doc.querySelector('.table-responsive');
            if (tableContent) {
                document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;

                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');

                actualizarPaginacionPagos(totalElements, pageSize, currentPage);
            }
        })
        .catch(error => {
            console.error('Error en la paginación:', error);
            mostrarError('Error al cambiar de página');
        });
}

function reiniciarFiltrosPagos() {
    const now = new Date();
    const currentYearMonth = now.toISOString().slice(0, 7); // Formato YYYY-MM
    
    document.getElementById('alumnoSearch').value = '';
    document.getElementById('estadoSelect').value = '';
    document.getElementById('mesPago').value = currentYearMonth;
    actualizarTablaPagos();
}

function registrarPagoPendiente() {
    const form = document.getElementById('pagoForm');
    const formData = new FormData(form);

    fetch('/pagos/registrar-pendiente', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'text/html'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al registrar el pago');
            }
            return response.text();
        })
        .then(html => {
            // Cerrar el modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('nuevoPagoModal'));
            modal.hide();

            // Actualizar la tabla
            document.querySelector('.table-responsive').innerHTML = html;

            // Mostrar mensaje de éxito
            mostrarMensajeExito('Pago registrado como pendiente');

            // Limpiar el formulario
            form.reset();
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al registrar el pago');
        });
}

function registrarPago(id) {
    if (confirm('¿Confirma el registro del pago?')) {
        fetch(`/pagos/${id}/registrar`, {
            method: 'POST'
        })
            .then(response => response.text())
            .then(html => {
                document.querySelector('.table-responsive').innerHTML = html;
                mostrarMensajeExito('Pago registrado exitosamente');
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al registrar el pago');
            });
    }
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

// Función auxiliar para mostrar mensajes de error
function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('.container').prepend(alertDiv);

    // Log para depuración
    console.error(mensaje);
}

function verDetallesPagos(id) {
    fetch(`/pagos/${id}/detalles`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar detalles del pago');
            }
            return response.text();
        })
        .then(html => {
            const modalContent = document.querySelector('#detallePagoModal .modal-content');
            if (!modalContent) {
                throw new Error('No se encontró el contenedor del modal');
            }
            modalContent.innerHTML = html;
            const modal = new bootstrap.Modal(document.getElementById('detallePagoModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al cargar detalles del pago');
        });
}

function cancelarPago(id) {
    if (confirm('¿Está seguro que desea cancelar este pago?')) {
        fetch(`/pagos/${id}/cancelar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'text/html'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cancelar el pago');
                }
                return response.text();
            })
            .then(html => {
                document.querySelector('.table-responsive').innerHTML = html;
                mostrarMensajeExito('Pago cancelado correctamente');
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al cancelar el pago');
            });
    }
}

function eliminarPago(id) {
    if (confirm('¿Está seguro que desea eliminar este pago?')) {
        fetch(`/pagos/${id}/eliminar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'text/html'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al eliminar el pago');
                }
                return response.text();
            })
            .then(html => {
                document.querySelector('.table-responsive').innerHTML = html;
                mostrarMensajeExito('Pago eliminado correctamente');
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al eliminar el pago');
            });
    }
}