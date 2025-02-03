document.addEventListener('DOMContentLoaded', function() {
    const tipoSelect = document.getElementById('tipoSelect');
    if (tipoSelect) {
        tipoSelect.addEventListener('change', function() {
            const tipo = this.value;
            if (tipo) {
                fetch(`/inventario/tipo/${tipo}`)
                    .then(response => response.text())
                    .then(html => {
                        document.querySelector('.table-responsive').innerHTML = html;
                    });
            }
        });
    }
});

function mostrarBajoStock() {
    fetch('/inventario/bajo-stock')
        .then(response => response.text())
        .then(html => {
            document.querySelector('.table-responsive').innerHTML = html;
        });
}

function abrirModalCantidad(button) {
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const tipo = button.getAttribute('data-tipo');
    const cantidad = button.getAttribute('data-cantidad');
    const descripcion = button.getAttribute('data-descripcion');
    const stockMinimo = button.getAttribute('data-stock-minimo');

    const modal = new bootstrap.Modal(document.getElementById('actualizarCantidadModal'));
    const form = document.getElementById('cantidadForm');

    // Establecer la acción del formulario
    form.action = `/inventario/${id}/cantidad`;

    // Rellenar los campos del formulario
    form.querySelector('input[name="id"]').value = id;
    form.querySelector('input[name="nombre"]').value = nombre;
    form.querySelector('input[name="tipo"]').value = tipo;
    form.querySelector('input[name="descripcion"]').value = descripcion;
    form.querySelector('input[name="stockMinimo"]').value = stockMinimo;
    form.querySelector('input[name="cantidad"]').value = cantidad;

    // Mostrar el modal
    modal.show();
}

function guardarCantidad() {
    const form = document.getElementById('cantidadForm');
    form.submit();
}

function actualizarCantidad() {
    const id = document.getElementById('itemId').value;
    const cantidad = document.getElementById('cantidad').value;
    
    if (!cantidad || cantidad < 0) {
        alert('Por favor ingrese una cantidad válida');
        return;
    }

    const form = document.getElementById('cantidadForm');
    form.action = `/inventario/${id}/cantidad`;

    // Cerrar el modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('actualizarCantidadModal'));
    modal.hide();

    // Enviar el formulario
    form.submit();
}

function nuevoItem() {
    const modal = new bootstrap.Modal(document.getElementById('nuevoItemModal'));
    const form = document.getElementById('itemForm');

    // Resetear el formulario
    form.reset();
    
    // Establecer la acción del formulario
    form.action = '/inventario';

    // Actualizar el título del modal
    modal._element.querySelector('.modal-title').textContent = 'Nuevo Item';

    // Mostrar el modal
    modal.show();
}