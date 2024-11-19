document.addEventListener('DOMContentLoaded', function() {
    initializeExamenesForm();
    initializeDataTable();
});

function initializeExamenesForm() {
    const searchForm = document.getElementById('searchExamenesForm');
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        buscarExamenes();
    });

    // Inicializar select2 para búsquedas mejoradas
    $('.form-select').select2({
        placeholder: 'Seleccionar...',
        allowClear: true
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
    fetch(`/api/examenes/${id}`)
        .then(response => response.json())
        .then(data => {
            const modal = new bootstrap.Modal(document.getElementById('evaluacionModal'));
            llenarFormularioEvaluacion(data);
            modal.show();
        })
        .catch(error => mostrarError('Error al cargar datos del examen'));
}

function guardarEvaluacion(id) {
    const formData = new FormData(document.getElementById('evaluacionForm'));
    const data = Object.fromEntries(formData.entries());
    
    fetch(`/api/examenes/${id}/evaluar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            throw new Error('Error al guardar evaluación');
        }
    })
    .catch(error => mostrarError('Error al guardar la evaluación'));
} 