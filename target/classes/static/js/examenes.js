document.addEventListener('DOMContentLoaded', function () {
    initializeExamenesForm();
    initializeDataTable();

    // Inicializar el manejo del modal
    const detallesModal = document.getElementById('detallesExamenModal');
    if (detallesModal) {
        detallesModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            verDetallesExamen(id);
        });
    }
});

function initializeExamenesForm() {
    const searchForm = document.getElementById('searchExamenesForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function (e) {
            e.preventDefault();
            buscarExamenes();
        });
    }
}

function initializeDataTable() {
    const table = document.querySelector('.table');
    if (table) {
        new DataTable(table, {
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json'
            },
            order: [[0, 'desc']], // Order by first column (fecha) descending
            pageLength: 10,
            responsive: true
        });
    }
}

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

