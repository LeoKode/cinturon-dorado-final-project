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
    const cantidad = button.getAttribute('data-cantidad');
    document.getElementById('itemId').value = id;
    document.getElementById('cantidad').value = cantidad;
    new bootstrap.Modal(document.getElementById('actualizarCantidadModal')).show();
}

function actualizarCantidad() {
    const id = document.getElementById('itemId').value;
    const cantidad = document.getElementById('cantidad').value;
    
    fetch(`/inventario/${id}/cantidad`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `cantidad=${cantidad}`
    })
    .then(() => window.location.reload());
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

function guardarItem() {
    const form = document.getElementById('itemForm');
    form.submit();
}