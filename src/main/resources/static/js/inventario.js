document.addEventListener('DOMContentLoaded', function() {
    const tipoSelect = document.getElementById('tipoSelect');
    if (tipoSelect) {
        tipoSelect.addEventListener('change', function() {
            const tipo = this.value;
            const url = tipo === '' ? '/inventario/todos' : `/inventario/tipo/${tipo}`;
            
            fetch(url)
                .then(response => response.text())
                .then(html => {
                    document.querySelector('.table-responsive').innerHTML = html;
                });
        });
    }
});

function mostrarBajoStock() {
    fetch('/inventario/bajo-stock')
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            // Obtener el contenido de la tabla
            const tableContent = doc.querySelector('.table-responsive');
            if (tableContent) {
                // Actualizar la tabla
                document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;
                
                // Obtener información de paginación
                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');
                
                // Actualizar la paginación
                actualizarPaginacionInventario(totalElements, pageSize, currentPage);
            }
        })
        .catch(error => {
            console.error('Error al mostrar items con bajo stock:', error);
            mostrarError('Error al cargar los datos de bajo stock');
        });
}

function abrirModalItem(button) {
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const tipo = button.getAttribute('data-tipo');
    const cantidad = button.getAttribute('data-cantidad');
    const descripcion = button.getAttribute('data-descripcion');
    const stockMinimo = button.getAttribute('data-stock-minimo');

    const modal = new bootstrap.Modal(document.getElementById('actualizarItemModal'));
    const form = document.getElementById('editarItemForm');

    if (!form) {
        console.error('Form not found');
        return;
    }

    try {
        // Establecer la acción del formulario
        form.action = `/inventario/${id}/item`;

        // Campo oculto para el ID
        const idInput = form.querySelector('input[name="id"]');
        if (idInput) idInput.value = id;

        // Rellenar los campos visibles
        const nombreInput = form.querySelector('input[name="nombre"]');
        if (nombreInput) nombreInput.value = nombre;

        const tipoSelect = form.querySelector('select[name="tipo"]');
        if (tipoSelect) tipoSelect.value = tipo;

        const cantidadInput = form.querySelector('input[name="cantidad"]');
        if (cantidadInput) cantidadInput.value = cantidad;

        const stockMinimoInput = form.querySelector('input[name="stockMinimo"]');
        if (stockMinimoInput) stockMinimoInput.value = stockMinimo;

        const descripcionTextarea = form.querySelector('textarea[name="descripcion"]');
        if (descripcionTextarea) descripcionTextarea.value = descripcion || '';

        // Mostrar el modal
        modal.show();
    } catch (error) {
        console.error('Error al cargar los datos en el modal:', error);
        alert('Error al cargar los datos del item');
    }
}

function guardarItem() {
    const form = document.getElementById('nuevoItemForm') || document.getElementById('editarItemForm');
    
    // Activar la validación nativa del navegador
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return false;
    }
    
    // Validación mejorada
    let formValido = true;
    let primerCampoInvalido = null;
    
    // 1. Validar nombre
    const nombreInput = form.querySelector('[name="nombre"]');
    if (!nombreInput || !nombreInput.value.trim()) {
        mostrarAlertaEnModal('El nombre es obligatorio', 'danger');
        nombreInput.classList.add('is-invalid');
        formValido = false;
        primerCampoInvalido = primerCampoInvalido || nombreInput;
    } else {
        nombreInput.classList.remove('is-invalid');
        nombreInput.classList.add('is-valid');
    }
    
    // 2. Validar tipo
    const tipoSelect = form.querySelector('[name="tipo"]');
    if (!tipoSelect || !tipoSelect.value) {
        mostrarAlertaEnModal('El tipo de equipo es obligatorio', 'danger');
        tipoSelect.classList.add('is-invalid');
        formValido = false;
        primerCampoInvalido = primerCampoInvalido || tipoSelect;
    } else {
        tipoSelect.classList.remove('is-invalid');
        tipoSelect.classList.add('is-valid');
    }
    
    // Si hay algún campo inválido, enfocarlo
    if (primerCampoInvalido) {
        primerCampoInvalido.focus();
        return false;
    }
    
    // Si todo está bien, enviar el formulario
    form.submit();
    return true;
}

function mostrarAlertaEnModal(mensaje, tipo) {
    const alertContainer = document.createElement('div');
    alertContainer.className = `alert alert-${tipo} mt-2`;
    alertContainer.textContent = mensaje;
    
    // Eliminar alertas anteriores
    document.querySelectorAll('.modal .alert').forEach(el => el.remove());
    
    // Agregar nueva alerta al principio del formulario
    const form = document.getElementById('nuevoItemForm') || document.getElementById('editarItemForm');
    form.prepend(alertContainer);
    
    // Desaparecer después de 5 segundos
    setTimeout(() => {
        alertContainer.classList.add('fade');
        setTimeout(() => alertContainer.remove(), 500);
    }, 5000);
}

function nuevoItem() {
    const modal = new bootstrap.Modal(document.getElementById('nuevoItemModal'));
    const form = document.getElementById('nuevoItemForm');

    if (form) {
        // Limpiar el formulario
        form.reset();
        form.action = '/inventario';
        
        // Limpiar cualquier mensaje de error previo
        document.querySelectorAll('.modal .alert').forEach(el => el.remove());
        
        // Asegurarnos que todos los campos requeridos estén marcados como required
        const nombreInput = form.querySelector('[name="nombre"]');
        if (nombreInput) nombreInput.required = true;
        
        const tipoSelect = form.querySelector('[name="tipo"]');
        if (tipoSelect) tipoSelect.required = true;
    }

    modal.show();
}

// Función para actualizar la tabla de inventario con fetch
function actualizarTablaInventario(url) {
    fetch(url)
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            // Obtener el contenido de la tabla
            const tableContent = doc.querySelector('.table-responsive');
            if (tableContent) {
                // Actualizar la tabla
                document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;
                
                // Obtener información de paginación
                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');
                
                // Actualizar la paginación
                actualizarPaginacionInventario(totalElements, pageSize, currentPage);
                
                // Verificar si hay mensaje o error para mostrar
                const mensaje = doc.querySelector('[data-mensaje]');
                if (mensaje) {
                    mostrarMensaje(mensaje.getAttribute('data-mensaje'), "success");
                }
                
                const error = doc.querySelector('[data-error]');
                if (error) {
                    mostrarError(error.getAttribute('data-error'));
                }
                
                // Cerrar todas las descripciones expandidas
                document.querySelectorAll('.description-row.show').forEach(row => {
                    row.classList.remove('show');
                    row.style.display = 'none';
                });
                
                // Restaurar la descripción expandida después de actualizar la tabla
                setTimeout(restaurarDescripcionExpandida, 100);
            } else {
                console.error('No se encontró el fragmento de tabla en la respuesta');
            }
        })
        .catch(error => {
            console.error('Error al actualizar la tabla de inventario:', error);
            mostrarError('Error al cargar los datos. Intente nuevamente.');
        });
}

// Función para actualizar la paginación
function actualizarPaginacionInventario(totalElements, pageSize, currentPage) {
    const totalPages = Math.ceil(totalElements / pageSize);
    
    // Buscar o crear el contenedor de paginación
    let paginationContainer = document.querySelector('.d-flex.justify-content-center.mt-4');
    
    if (totalElements > pageSize) {
        // Si hay más elementos que la página, mostrar paginación
        const paginationHTML = generarPaginacionInventario(totalPages, currentPage);
        
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

// Función para cambiar de página
function cambiarPaginaInventario(page) {
    // Verificar qué filtro está activo actualmente
    const tipoSelect = document.getElementById('tipoSelect');
    let url = `/inventario/todos?page=${page}`;
    
    if (tipoSelect && tipoSelect.value) {
        url = `/inventario/tipo/${tipoSelect.value}?page=${page}`;
    }
    
    // Si estamos en la vista de "bajo stock", mantener ese filtro
    const currentUrl = window.location.pathname;
    if (currentUrl.includes('bajo-stock')) {
        url = `/inventario/bajo-stock?page=${page}`;
    }
    
    // Llamar a la función que actualiza la tabla con fetch
    actualizarTablaInventario(url);
}

// Función para generar el HTML de la paginación
function generarPaginacionInventario(totalPages, currentPage) {
    let html = '<nav aria-label="Navegación de páginas"><ul class="pagination">';

    // Primera página
    html += `<li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaInventario(0)">&laquo;</a>
        </li>`;

    // Anterior
    html += `<li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaInventario(${currentPage - 1})">&lt;</a>
        </li>`;

    // Números de página
    for (let i = 0; i < totalPages; i++) {
        html += `<li class="page-item ${i == currentPage ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaInventario(${i})">${i + 1}</a>
           </li>`;
    }

    // Siguiente
    html += `<li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaInventario(${currentPage + 1})">&gt;</a>
        </li>`;

    // Última página
    html += `<li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="cambiarPaginaInventario(${totalPages - 1})">&raquo;</a>
        </li>`;

    html += '</ul></nav>';
    return html;
}

// Inicializar filtros y eventos cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', function() {
    // Evento para filtrar por tipo
    const tipoFiltro = document.getElementById('tipoFiltro');
    if (tipoFiltro) {
        tipoFiltro.addEventListener('change', function() {
            const tipo = this.value;
            let url = '/inventario/todos';
            
            if (tipo) {
                url = `/inventario/tipo/${tipo}`;
            }
            
            actualizarTablaInventario(url);
        });
    }
    
    // Evento para mostrar ítems con bajo stock
    const btnBajoStock = document.getElementById('btnBajoStock');
    if (btnBajoStock) {
        btnBajoStock.addEventListener('click', function() {
            actualizarTablaInventario('/inventario/bajo-stock');
        });
    }
});

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    const alertContainer = document.getElementById('alertContainer');
    if (alertContainer) {
        alertContainer.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
    }
}

// Modificar la función para eliminar item
function eliminarItem(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este ítem?')) {
        fetch(`/inventario/${id}/eliminar`, {
            method: 'POST'
        })
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            // Obtener el contenido de la tabla
            const tableContent = doc.querySelector('.table-responsive');
            if (tableContent) {
                // Actualizar la tabla
                document.querySelector('.table-responsive').innerHTML = tableContent.innerHTML;
                
                // Obtener información de paginación
                const totalElements = parseInt(tableContent.getAttribute('data-total-elements') || '0');
                const pageSize = parseInt(tableContent.getAttribute('data-page-size') || '10');
                const currentPage = parseInt(tableContent.getAttribute('data-current-page') || '0');
                
                // Actualizar la paginación
                actualizarPaginacionInventario(totalElements, pageSize, currentPage);
                
                // Mostrar mensaje de éxito
                mostrarMensaje("Item eliminado exitosamente", "success");
            } else {
                // Si no hay fragmento de tabla, podría ser una redirección
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error al eliminar item:', error);
            mostrarError('Error al eliminar el item. Intente nuevamente.');
        });
    }
}

// Función para mostrar mensaje de éxito
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

// Función para editar un item
function editarItem(button) {
    // Primero mostrar el modal
    const modal = new bootstrap.Modal(document.getElementById('actualizarItemModal'));
    modal.show();

    // Obtener los datos de los atributos data-*
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const tipo = button.getAttribute('data-tipo');
    const cantidad = button.getAttribute('data-cantidad');
    const stockMinimo = button.getAttribute('data-stock-minimo');
    const descripcion = button.getAttribute('data-descripcion');

    // Esperar a que el modal esté visible antes de intentar acceder a los elementos del formulario
    modal._element.addEventListener('shown.bs.modal', function () {
        const form = document.getElementById('editarItemForm');
        if (form) {
            // Establecer la acción del formulario para actualizar
            form.action = `/inventario/${id}/item`;

            // Rellenar los campos del formulario con los datos del item
            const formFields = {
                'id': id,
                'nombre': nombre,
                'tipo': tipo,
                'cantidad': cantidad,
                'stockMinimo': stockMinimo,
                'descripcion': descripcion
            };

            // Actualizar cada campo si existe
            Object.entries(formFields).forEach(([key, value]) => {
                const field = form.querySelector(`[name="${key}"]`);
                if (field) {
                    field.value = value || '';
                }
            });

            // Actualizar el título del modal
            modal._element.querySelector('.modal-title').textContent = 'Editar Item';
        }
    }, { once: true }); // Eliminar el listener después de la primera ejecución
}

// Función para mostrar/ocultar la descripción al hacer clic en la fila
function toggleDescriptionByRow(row, itemId) {
    const descriptionRow = document.getElementById(`description-row-${itemId}`);
    
    // Desactivar todas las filas activas
    document.querySelectorAll('.item-row.active').forEach(activeRow => {
        if (activeRow.id !== `item-row-${itemId}`) {
            activeRow.classList.remove('active');
        }
    });
    
    // Si estamos abriendo esta descripción, guardar su ID
    if (descriptionRow && descriptionRow.style.display === 'none') {
        localStorage.setItem('expandedItemId', itemId);
    } else {
        // Si la estamos cerrando, borrar el ID guardado
        localStorage.removeItem('expandedItemId');
    }
    
    // Cerrar todas las descripciones abiertas
    document.querySelectorAll('.description-row.show').forEach(r => {
        if (r.id !== `description-row-${itemId}`) {
            r.classList.remove('show');
            r.style.display = 'none';
            
            // Obtener el ID de la fila correspondiente
            const rowId = r.id.replace('description-row-', '');
            const itemRow = document.getElementById(`item-row-${rowId}`);
            if (itemRow) {
                itemRow.classList.remove('active');
            }
        }
    });
    
    // Alternar la descripción seleccionada
    if (descriptionRow) {
        if (descriptionRow.style.display === 'none') {
            // Mostrar la descripción
            descriptionRow.style.display = 'table-row';
            setTimeout(() => {
                descriptionRow.classList.add('show');
            }, 10);
            
            // Activar la fila
            row.classList.add('active');
        } else {
            // Ocultar la descripción
            descriptionRow.classList.remove('show');
            setTimeout(() => {
                descriptionRow.style.display = 'none';
            }, 300);
            
            // Desactivar la fila
            row.classList.remove('active');
        }
    }
}

// Modificar la función restaurarDescripcionExpandida para usar la nueva función
function restaurarDescripcionExpandida() {
    const expandedItemId = localStorage.getItem('expandedItemId');
    if (expandedItemId) {
        const row = document.getElementById(`item-row-${expandedItemId}`);
        if (row) {
            toggleDescriptionByRow(row, expandedItemId);
        }
    }
}

// Llamar a esta función cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Añadir efectos visuales a las filas con descripción
    document.querySelectorAll('.item-row[data-has-description="true"]').forEach(row => {
        row.classList.add('has-description-indicator');
    });
    
    // Restaurar descripción expandida si existe
    restaurarDescripcionExpandida();
});