// Funcionalidad para la gestión de alumnos
document.addEventListener('DOMContentLoaded', function() {
    initializeSearchForm();
});

// Inicializar el formulario de búsqueda
function initializeSearchForm() {
    const searchForm = document.getElementById('searchForm');
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(searchForm);
        const params = new URLSearchParams(formData);
        
        fetch(`/alumnos/buscar?${params.toString()}`)
            .then(response => response.text())
            .then(data => actualizarTablaAlumnos(data))
            .catch(error => mostrarError('Error al buscar alumnos'));
    });
}

// Función para actualizar la tabla de alumnos después de una búsqueda
function actualizarTablaAlumnos(html) {
    document.querySelector('.table-responsive').innerHTML = html;
    // Re-inicializar los eventos de los nuevos botones "Editar" y "Eliminar"
    document.querySelectorAll('button.btn-outline-primary').forEach(button => {
        button.onclick = function() { editarAlumno(this); };
    });
    document.querySelectorAll('button.btn-outline-danger').forEach(button => {
        const id = button.getAttribute('data-id');
        button.onclick = function() { eliminarAlumno(id); };
    });
}

// Función para guardar un alumno (crear o editar)
function guardarAlumno() {
    const form = document.getElementById('alumnoForm');
    form.submit();
}

// Función para editar un alumno
function editarAlumno(button) {
    const id = button.getAttribute('data-id');
    const nombre = button.getAttribute('data-nombre');
    const edad = button.getAttribute('data-edad');
    const email = button.getAttribute('data-email');
    const nivelCinturon = button.getAttribute('data-nivel-cinturon');

    const modal = new bootstrap.Modal(document.getElementById('nuevoAlumnoModal'));
    const form = document.getElementById('alumnoForm');

    // Establecer la acción del formulario para actualizar
    form.action = `/alumnos`;

    // Rellenar los campos del formulario con los datos del alumno
    form.querySelector('input[name="id"]').value = id;
    form.querySelector('input[name="nombre"]').value = nombre;
    form.querySelector('input[name="edad"]').value = edad;
    form.querySelector('input[name="email"]').value = email;
    form.querySelector('select[name="nivelCinturon"]').value = nivelCinturon;

    // Actualizar el título del modal
    modal._element.querySelector('.modal-title').textContent = 'Editar Alumno';

    // Mostrar el modal
    modal.show();
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

// Función para buscar alumnos (no utilizada actualmente)
function buscarAlumnos(event) {
    event.preventDefault();
    const form = document.getElementById('searchForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);
    
    fetch(`/alumnos/buscar?${params}`)
    .then(response => response.text())
    .then(html => {
        document.querySelector('.table-responsive').innerHTML = html;
    });
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('.container').prepend(alertDiv);
}
