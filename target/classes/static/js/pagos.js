document.addEventListener('DOMContentLoaded', function() {
    initializePagosForm();
    initializeDataTable();
});

function initializePagosForm() {
    const searchForm = document.getElementById('searchPagosForm');
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        buscarPagos();
    });

    // Inicializar select2 para búsqueda de alumnos
    $('#alumnoSelect').select2({
        placeholder: 'Seleccionar alumno',
        allowClear: true
    });
}

function buscarPagos() {
    const formData = new FormData(document.getElementById('searchPagosForm'));
    const params = new URLSearchParams(formData);
    
    fetch(`/api/pagos/buscar?${params.toString()}`)
        .then(response => response.json())
        .then(data => actualizarTablaPagos(data))
        .catch(error => mostrarError('Error al buscar pagos'));
}

function registrarPago(id) {
    if (confirm('¿Confirma el registro del pago?')) {
        fetch(`/api/pagos/${id}/registrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('Error al registrar pago');
            }
        })
        .catch(error => mostrarError('Error al registrar el pago'));
    }
}

function verDetalles(id) {
    fetch(`/api/pagos/${id}`)
        .then(response => response.json())
        .then(data => {
            const modal = new bootstrap.Modal(document.getElementById('detallePagoModal'));
            llenarDetallesPago(data);
            modal.show();
        })
        .catch(error => mostrarError('Error al cargar detalles del pago'));
} 