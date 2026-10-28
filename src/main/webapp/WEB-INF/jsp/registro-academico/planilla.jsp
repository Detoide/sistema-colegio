<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Registro académico"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <div>
        <h3 class="mb-0">Docente - Registro de notas y asistencia</h3>
        <span class="text-muted small">Registre la nota del bimestre y la asistencia del día en una sola planilla.</span>
    </div>
</div>

<div class="card mb-3">
    <div class="card-body">
        <form method="get" action="${ctx}/registro-academico" class="row g-2 align-items-end">
            <div class="col-md-3">
                <label class="form-label" for="seccionId">Grado y sección</label>
                <select class="form-select" id="seccionId" name="seccionId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${secciones}" var="s">
                        <option value="${s.idSeccion}" <c:if test="${seccionId == s.idSeccion}">selected</c:if>>
                            <c:out value="${s.descripcion}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label" for="periodoId">Bimestre</label>
                <select class="form-select" id="periodoId" name="periodoId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${periodos}" var="p">
                        <option value="${p.idPeriodo}" <c:if test="${periodoId == p.idPeriodo}">selected</c:if>>
                            <c:out value="${p.nombrePeriodo}"/>
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${empty periodos}">
                    <div class="form-text text-danger">No hay bimestres configurados.</div>
                </c:if>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="cursoId">Curso</label>
                <select class="form-select" id="cursoId" name="cursoId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${cursos}" var="c">
                        <option value="${c.idCurso}" <c:if test="${cursoId == c.idCurso}">selected</c:if>>
                            <c:out value="${c.nombreCurso}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label" for="fecha">Fecha de asistencia</label>
                <input type="date" class="form-control" id="fecha" name="fecha" value="${fecha}" required>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary w-100" type="submit">Consultar</button>
            </div>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${seccionId == null || periodoId == null || cursoId == null}">
        <div class="alert alert-info">
            Seleccione grado/sección, bimestre, curso y fecha para cargar la planilla.
        </div>
    </c:when>
    <c:when test="${empty filas}">
        <div class="alert alert-warning">
            No hay estudiantes matriculados en la sección para el periodo anual que contiene al bimestre.
        </div>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${puedeEditar}">
                <form method="post" action="${ctx}/registro-academico">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="seccionId" value="${seccionId}"/>
                    <input type="hidden" name="periodoId" value="${periodoId}"/>
                    <input type="hidden" name="cursoId" value="${cursoId}"/>
                    <input type="hidden" name="fecha" value="${fecha}"/>
                    <div class="card">
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>Código</th>
                                        <th>Apellidos y nombres</th>
                                        <th style="max-width: 170px;">Nota (0 - 20)</th>
                                        <th>Asistencia</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${filas}" var="f">
                                        <tr>
                                            <td><c:out value="${f.codigo}"/></td>
                                            <td><c:out value="${f.estudiante}"/></td>
                                            <td>
                                                <input type="number" class="form-control form-control-sm"
                                                       name="notas[${f.matriculaId}]"
                                                       value="${f.notaActual}"
                                                       step="0.01" min="0" max="20"
                                                       aria-label="Nota de ${f.estudiante}">
                                            </td>
                                            <td>
                                                <select class="form-select form-select-sm attendance-select"
                                                        name="estados[${f.matriculaId}]"
                                                        aria-label="Asistencia de ${f.estudiante}">
                                                    <option value="">--</option>
                                                    <c:forEach items="${estados}" var="e">
                                                        <option value="${e}"
                                                                <c:if test="${f.estadoActual == e}">selected</c:if>>
                                                            <c:out value="${e}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <span class="text-muted small">La fecha seleccionada debe pertenecer al periodo anual del bimestre.</span>
                        <button type="submit" class="btn btn-primary">Guardar registro</button>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <div class="card">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Apellidos y nombres</th>
                                    <th>Nota</th>
                                    <th>Asistencia</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${filas}" var="f">
                                    <tr>
                                        <td><c:out value="${f.codigo}"/></td>
                                        <td><c:out value="${f.estudiante}"/></td>
                                        <td>${f.notaActual == null ? '-' : f.notaActual}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${f.estadoActual == null}">—</c:when>
                                                <c:when test="${f.estadoActual.name() == 'ASISTIO'}">
                                                    <span class="badge text-bg-success">ASISTIÓ</span>
                                                </c:when>
                                                <c:when test="${f.estadoActual.name() == 'TARDANZA'}">
                                                    <span class="badge text-bg-warning">TARDANZA</span>
                                                </c:when>
                                                <c:when test="${f.estadoActual.name() == 'FALTA'}">
                                                    <span class="badge text-bg-danger">FALTA</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge text-bg-secondary">JUSTIFICADA</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="alert alert-secondary mt-3 mb-0">
                    Solo el docente tutor de la sección puede modificar las notas y la asistencia.
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

