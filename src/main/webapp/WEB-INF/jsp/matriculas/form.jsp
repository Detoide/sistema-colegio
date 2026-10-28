<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nueva matrícula"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <div>
        <h3 class="mb-0">Formulario de matrícula de estudiante</h3>
        <span class="text-muted small">Complete la información académica y confirme la matrícula.</span>
    </div>
    <a class="btn btn-outline-secondary" href="${ctx}/matriculas">Volver</a>
</div>

<div class="wizard-steps" aria-hidden="true">
    <div class="wizard-step" data-step-indicator="1">
        <span class="step-number">1</span>Período / Grado
    </div>
    <div class="wizard-step" data-step-indicator="2">
        <span class="step-number">2</span>Formulario
    </div>
    <div class="wizard-step" data-step-indicator="3">
        <span class="step-number">3</span>Confirmar
    </div>
</div>

<div class="card">
    <div class="card-body">
        <form:form modelAttribute="matricula" method="post" action="${ctx}/matriculas"
                   id="matriculaForm" data-paso="${empty paso ? 1 : paso}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <section class="wizard-panel" data-step="1">
                <h5 class="mb-3">1. Información académica</h5>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label" for="periodoId">Periodo académico</label>
                        <select class="form-select" id="periodoId" name="periodoId">
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${periodos}" var="p">
                                <option value="${p.idPeriodo}"
                                        <c:if test="${matricula.periodoId == p.idPeriodo}">selected</c:if>>
                                    <c:out value="${p.nombrePeriodo}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback" id="errorPeriodo">Seleccione el periodo académico.</div>
                        <form:errors path="periodoId" cssClass="text-danger small d-block"/>
                        <c:if test="${empty periodos}">
                            <div class="form-text text-danger">
                                No hay periodos anuales. Cree uno en Administración &gt; Periodos.
                            </div>
                        </c:if>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="gradoId">Grado</label>
                        <select class="form-select" id="gradoId">
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${grados}" var="g">
                                <option value="${g.idGrado}"><c:out value="${g.descripcion}"/></option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback" id="errorGrado">Seleccione el grado.</div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="seccionId">Sección</label>
                        <select class="form-select" id="seccionId" name="seccionId">
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${secciones}" var="s">
                                <option value="${s.idSeccion}"
                                        data-grado="${s.grado.idGrado}"
                                        data-descripcion="${fn:escapeXml(s.descripcion)}"
                                        data-vacantes="${s.vacantesDisponibles}"
                                        <c:if test="${matricula.seccionId == s.idSeccion}">selected</c:if>>
                                    <c:out value="${s.descripcion}"/> - ${s.vacantesDisponibles} vacantes
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback" id="errorSeccion">Seleccione la sección.</div>
                        <form:errors path="seccionId" cssClass="text-danger small d-block"/>
                    </div>
                </div>
                <div class="alert alert-info small mt-3 mb-0">
                    Una matrícula pendiente ocupa vacante. Al anularla, la vacante se libera.
                </div>
                <div class="d-flex justify-content-end mt-4">
                    <button type="button" class="btn btn-primary" id="btnPaso1Siguiente">Continuar</button>
                </div>
            </section>

            <section class="wizard-panel" data-step="2">
                <h5 class="mb-3">2. Datos personales y del apoderado</h5>
                <div class="row g-3">
                    <div class="col-md-8">
                        <label class="form-label" for="estudianteId">Estudiante</label>
                        <select class="form-select" id="estudianteId" name="estudianteId">
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${estudiantes}" var="e">
                                <option value="${e.idEstudiante}"
                                        data-nombres="${fn:escapeXml(e.nombres)}"
                                        data-apellidos="${fn:escapeXml(e.apellidos)}"
                                        data-nombre="${fn:escapeXml(e.nombreCompleto)}"
                                        data-codigo="${fn:escapeXml(e.codigo)}"
                                        data-nacimiento="${fn:escapeXml(e.fechaNacimientoTexto)}"
                                        data-apoderado="${fn:escapeXml(e.apoderado.nombreCompleto)}"
                                        data-dni="${fn:escapeXml(e.apoderado.dni)}"
                                        data-telefono="${fn:escapeXml(e.apoderado.telefono)}"
                                        <c:if test="${matricula.estudianteId == e.idEstudiante}">selected</c:if>>
                                    <c:out value="${e.nombreCompleto}"/> (<c:out value="${e.codigo}"/>)
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback" id="errorEstudiante">Seleccione el estudiante.</div>
                        <form:errors path="estudianteId" cssClass="text-danger small d-block"/>
                        <c:if test="${empty estudiantes}">
                            <div class="form-text text-danger">
                                No hay estudiantes registrados. <a href="${ctx}/estudiantes">Registre uno primero</a>.
                            </div>
                        </c:if>
                    </div>
                </div>

                <h6 class="text-muted mt-4 mb-3">Datos del estudiante</h6>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label" for="estNombres">Nombres</label>
                        <input type="text" class="form-control" id="estNombres" readonly>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="estApellidos">Apellidos</label>
                        <input type="text" class="form-control" id="estApellidos" readonly>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label" for="estNacimiento">Fecha de nacimiento</label>
                        <input type="text" class="form-control" id="estNacimiento" readonly>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label" for="estCodigo">Código</label>
                        <input type="text" class="form-control" id="estCodigo" readonly>
                    </div>
                </div>

                <h6 class="text-muted mt-4 mb-3">Datos del padre / apoderado</h6>
                <div class="row g-3">
                    <div class="col-md-3">
                        <label class="form-label" for="apoDni">DNI</label>
                        <input type="text" class="form-control" id="apoDni" readonly>
                    </div>
                    <div class="col-md-5">
                        <label class="form-label" for="apoNombre">Nombre completo</label>
                        <input type="text" class="form-control" id="apoNombre" readonly>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="apoTelefono">Número de teléfono</label>
                        <input type="text" class="form-control" id="apoTelefono" readonly>
                    </div>
                </div>
                <div class="alert alert-info small mt-3 mb-0">
                    Los datos se toman del registro del estudiante. Para editar al estudiante o su apoderado,
                    use <a href="${ctx}/estudiantes">Estudiantes</a> o <a href="${ctx}/apoderados">Apoderados</a>.
                </div>
                <div class="d-flex justify-content-between mt-4">
                    <button type="button" class="btn btn-outline-secondary" id="btnPaso2Atras">Atrás</button>
                    <button type="button" class="btn btn-primary" id="btnPaso2Siguiente">Continuar</button>
                </div>
            </section>

            <section class="wizard-panel" data-step="3">
                <h5 class="mb-3">3. Confirmar matrícula</h5>
                <dl class="row summary-list mb-4">
                    <dt class="col-sm-4">Periodo académico</dt>
                    <dd class="col-sm-8" id="resumenPeriodo">—</dd>
                    <dt class="col-sm-4">Grado y sección</dt>
                    <dd class="col-sm-8" id="resumenSeccion">—</dd>
                    <dt class="col-sm-4">Estudiante</dt>
                    <dd class="col-sm-8" id="resumenEstudiante">—</dd>
                    <dt class="col-sm-4">Apoderado</dt>
                    <dd class="col-sm-8" id="resumenApoderado">—</dd>
                </dl>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="terminos">
                    <label class="form-check-label" for="terminos">
                        Acepto los términos y condiciones de la matrícula.
                    </label>
                    <div class="text-danger small d-none" id="errorTerminos">
                        Debe aceptar los términos y condiciones.
                    </div>
                </div>
                <div class="d-flex justify-content-between mt-4">
                    <button type="button" class="btn btn-outline-secondary" id="btnPaso3Atras">Atrás</button>
                    <button type="submit" class="btn btn-primary">Registrar matrícula</button>
                </div>
            </section>
        </form:form>
    </div>
</div>

<script>
    (function () {
        var form = document.getElementById('matriculaForm');
        if (!form) {
            return;
        }

        var panels = Array.prototype.slice.call(form.querySelectorAll('.wizard-panel'));
        var indicators = Array.prototype.slice.call(document.querySelectorAll('.wizard-step'));
        var periodo = document.getElementById('periodoId');
        var grado = document.getElementById('gradoId');
        var seccion = document.getElementById('seccionId');
        var estudiante = document.getElementById('estudianteId');
        var terminos = document.getElementById('terminos');
        var errorTerminos = document.getElementById('errorTerminos');
        var pasoActual = parseInt(form.getAttribute('data-paso'), 10) || 1;
        var secciones = Array.prototype.slice.call(seccion.options).filter(function (opcion) {
            return opcion.value !== '';
        });

        function mostrarPaso(paso) {
            pasoActual = Math.min(3, Math.max(1, paso));
            panels.forEach(function (panel) {
                panel.classList.toggle('active', Number(panel.getAttribute('data-step')) === pasoActual);
            });
            indicators.forEach(function (indicador) {
                var numero = Number(indicador.getAttribute('data-step-indicator'));
                indicador.classList.toggle('active', numero === pasoActual);
                indicador.classList.toggle('done', numero < pasoActual);
            });
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        function marcarInvalido(campo, error, invalido) {
            campo.classList.toggle('is-invalid', invalido);
            if (error) {
                error.classList.toggle('d-none', !invalido);
            }
        }

        function filtrarSecciones() {
            var gradoSeleccionado = grado.value;
            secciones.forEach(function (opcion) {
                var visible = gradoSeleccionado === '' || opcion.getAttribute('data-grado') === gradoSeleccionado;
                opcion.hidden = !visible;
                opcion.disabled = !visible;
            });
            if (seccion.selectedOptions[0] && seccion.selectedOptions[0].disabled) {
                seccion.value = '';
            }
            actualizarResumen();
        }

        function actualizarEstudiante() {
            var opcion = estudiante.selectedOptions[0];
            var datos = opcion && opcion.value ? opcion.dataset : {};
            document.getElementById('estNombres').value = datos.nombres || '';
            document.getElementById('estApellidos').value = datos.apellidos || '';
            document.getElementById('estNacimiento').value = datos.nacimiento || '';
            document.getElementById('estCodigo').value = datos.codigo || '';
            document.getElementById('apoDni').value = datos.dni || '';
            document.getElementById('apoNombre').value = datos.apoderado || '';
            document.getElementById('apoTelefono').value = datos.telefono || '';
            actualizarResumen();
        }

        function actualizarResumen() {
            var periodoOpcion = periodo.selectedOptions[0];
            var seccionOpcion = seccion.selectedOptions[0];
            var estudianteOpcion = estudiante.selectedOptions[0];
            var datos = estudianteOpcion && estudianteOpcion.value ? estudianteOpcion.dataset : {};

            document.getElementById('resumenPeriodo').textContent =
                periodoOpcion && periodoOpcion.value ? periodoOpcion.textContent.trim() : '—';
            document.getElementById('resumenSeccion').textContent =
                seccionOpcion && seccionOpcion.value
                    ? seccionOpcion.getAttribute('data-descripcion') + ' (' +
                      seccionOpcion.getAttribute('data-vacantes') + ' vacantes)'
                    : '—';
            document.getElementById('resumenEstudiante').textContent =
                datos.nombre ? datos.nombre + ' (' + (datos.codigo || '') + ')' : '—';
            document.getElementById('resumenApoderado').textContent =
                datos.apoderado ? datos.apoderado + ' - DNI ' + (datos.dni || '') : '—';
        }

        function validarPaso1() {
            var periodoInvalido = periodo.value === '';
            var gradoInvalido = grado.value === '';
            var seccionInvalida = seccion.value === '';
            marcarInvalido(periodo, document.getElementById('errorPeriodo'), periodoInvalido);
            marcarInvalido(grado, document.getElementById('errorGrado'), gradoInvalido);
            marcarInvalido(seccion, document.getElementById('errorSeccion'), seccionInvalida);
            return !periodoInvalido && !gradoInvalido && !seccionInvalida;
        }

        function validarPaso2() {
            var invalido = estudiante.value === '';
            marcarInvalido(estudiante, document.getElementById('errorEstudiante'), invalido);
            return !invalido;
        }

        document.getElementById('btnPaso1Siguiente').addEventListener('click', function () {
            if (validarPaso1()) {
                mostrarPaso(2);
            }
        });

        document.getElementById('btnPaso2Atras').addEventListener('click', function () {
            mostrarPaso(1);
        });

        document.getElementById('btnPaso2Siguiente').addEventListener('click', function () {
            if (validarPaso2()) {
                actualizarResumen();
                mostrarPaso(3);
            }
        });

        document.getElementById('btnPaso3Atras').addEventListener('click', function () {
            mostrarPaso(2);
        });

        periodo.addEventListener('change', actualizarResumen);
        grado.addEventListener('change', filtrarSecciones);
        seccion.addEventListener('change', actualizarResumen);
        estudiante.addEventListener('change', actualizarEstudiante);
        terminos.addEventListener('change', function () {
            errorTerminos.classList.toggle('d-none', terminos.checked);
        });

        form.addEventListener('submit', function (evento) {
            if (pasoActual !== 3) {
                evento.preventDefault();
                mostrarPaso(3);
                return;
            }
            if (!terminos.checked) {
                evento.preventDefault();
                errorTerminos.classList.remove('d-none');
                return;
            }
        });

        if (seccion.value) {
            var opcionSeleccionada = seccion.selectedOptions[0];
            var gradoSeleccionado = opcionSeleccionada ? opcionSeleccionada.getAttribute('data-grado') : '';
            if (gradoSeleccionado) {
                grado.value = gradoSeleccionado;
            }
        }
        filtrarSecciones();
        actualizarEstudiante();
        mostrarPaso(pasoActual);
    })();
</script>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
