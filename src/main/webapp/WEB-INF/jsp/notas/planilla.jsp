<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Notas"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Registro de notas</h3>
    <span class="text-muted small">Las notas se registran por bimestre.</span>
</div>

<div class="card mb-3">
    <div class="card-body">
        <form method="get" action="${ctx}/notas" class="row g-2 align-items-end">
            <div class="col-md-3">
                <label class="form-label" for="seccionId">Sección</label>
                <select class="form-select" id="seccionId" name="seccionId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${secciones}" var="s">
                        <option value="${s.idSeccion}" <c:if test="${seccionId == s.idSeccion}">selected</c:if>>
                            <c:out value="${s.descripcion}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
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
            <div class="col-md-3">
                <button class="btn btn-primary w-100" type="submit">Consultar</button>
            </div>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${seccionId == null || periodoId == null || cursoId == null}">
        <div class="alert alert-info">Seleccione sección, bimestre y curso para cargar la planilla.</div>
    </c:when>
    <c:when test="${empty filas}">
        <div class="alert alert-warning">
            No hay estudiantes matriculados en la sección para el periodo anual que contiene al bimestre.
        </div>
    </c:when>
    <c:otherwise>
        <form method="post" action="${ctx}/notas">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="seccionId" value="${seccionId}"/>
            <input type="hidden" name="periodoId" value="${periodoId}"/>
            <input type="hidden" name="cursoId" value="${cursoId}"/>
            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Código</th>
                                <th>Estudiante</th>
                                <th style="max-width: 160px;">Calificación (0 - 20)</th>
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
                                               <c:if test="${not puedeEditar}">disabled</c:if>>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <c:if test="${puedeEditar}">
                <button type="submit" class="btn btn-primary mt-3">Guardar notas</button>
            </c:if>
            <c:if test="${not puedeEditar}">
                <div class="alert alert-secondary mt-3 mb-0">
                    Solo el docente tutor de la sección puede modificar las notas.
                </div>
            </c:if>
        </form>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
