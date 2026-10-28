<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Asistencia"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Registro de asistencia</h3>
    <span class="text-muted small">Una falta acumulada de 3 genera una alerta de inasistencia.</span>
</div>

<div class="card mb-3">
    <div class="card-body">
        <form method="get" action="${ctx}/asistencia" class="row g-2 align-items-end">
            <div class="col-md-5">
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
            <div class="col-md-4">
                <label class="form-label" for="fecha">Fecha</label>
                <input type="date" class="form-control" id="fecha" name="fecha" value="${fecha}" required>
            </div>
            <div class="col-md-3">
                <button class="btn btn-primary w-100" type="submit">Consultar</button>
            </div>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${seccionId == null}">
        <div class="alert alert-info">Seleccione una sección para cargar la planilla de asistencia.</div>
    </c:when>
    <c:when test="${empty filas}">
        <div class="alert alert-warning">
            No hay estudiantes matriculados en la sección para el periodo que contiene la fecha seleccionada.
        </div>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${puedeEditar}">
                <form method="post" action="${ctx}/asistencia">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="seccionId" value="${seccionId}"/>
                    <input type="hidden" name="fecha" value="${fecha}"/>
                    <div class="card">
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>Código</th>
                                        <th>Estudiante</th>
                                        <c:forEach items="${estados}" var="e">
                                            <th class="text-center"><c:out value="${e}"/></th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${filas}" var="f">
                                        <tr>
                                            <td><c:out value="${f.codigo}"/></td>
                                            <td><c:out value="${f.estudiante}"/></td>
                                            <c:forEach items="${estados}" var="e">
                                                <td class="text-center">
                                                    <input class="form-check-input" type="radio"
                                                           name="estados[${f.matriculaId}]"
                                                           value="${e}"
                                                           <c:if test="${f.estadoActual == e}">checked</c:if>
                                                           <c:if test="${empty f.estadoActual && e.name() == 'ASISTIO'}">checked</c:if>>
                                                </td>
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-3">Guardar asistencia</button>
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
                                    <th>Estudiante</th>
                                    <c:forEach items="${estados}" var="e">
                                        <th class="text-center"><c:out value="${e}"/></th>
                                    </c:forEach>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${filas}" var="f">
                                    <tr>
                                        <td><c:out value="${f.codigo}"/></td>
                                        <td><c:out value="${f.estudiante}"/></td>
                                        <c:forEach items="${estados}" var="e">
                                            <td class="text-center">
                                                <c:if test="${f.estadoActual == e}">
                                                    <span class="badge text-bg-primary">X</span>
                                                </c:if>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="alert alert-secondary mt-3 mb-0">
                    Solo el docente tutor de la sección puede modificar la asistencia.
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
