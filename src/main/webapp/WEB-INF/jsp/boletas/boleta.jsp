<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Boletas"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Boleta de notas</h3>
    <c:if test="${not empty boleta}">
        <button class="btn btn-outline-secondary" type="button" onclick="window.print()">Imprimir</button>
    </c:if>
</div>

<div class="card mb-3">
    <div class="card-body">
        <form method="get" action="${ctx}/boletas" class="row g-2 align-items-end">
            <div class="col-md-6">
                <label class="form-label" for="matriculaId">Matrícula</label>
                <select class="form-select" id="matriculaId" name="matriculaId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${matriculas}" var="m">
                        <option value="${m.idMatricula}" <c:if test="${matriculaId == m.idMatricula}">selected</c:if>>
                            <c:out value="${m.estudiante.nombreCompleto}"/> -
                            <c:out value="${m.seccion.descripcion}"/> -
                            <c:out value="${m.periodo.nombrePeriodo}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label" for="periodoId">Bimestre</label>
                <select class="form-select" id="periodoId" name="periodoId" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach items="${periodos}" var="p">
                        <option value="${p.idPeriodo}" <c:if test="${periodoId == p.idPeriodo}">selected</c:if>>
                            <c:out value="${p.nombrePeriodo}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary w-100" type="submit">Generar</button>
            </div>
        </form>
    </div>
</div>

<c:if test="${not empty boleta}">
    <div class="card">
        <div class="card-body">
            <div class="d-flex flex-wrap justify-content-between align-items-start mb-3">
                <div>
                    <h4 class="mb-1">Boleta de notas</h4>
                    <div><strong>Estudiante:</strong> <c:out value="${boleta.estudiante}"/></div>
                    <div><strong>Código:</strong> <c:out value="${boleta.codigo}"/></div>
                    <div><strong>Grado y sección:</strong> <c:out value="${boleta.grado}"/> <c:out value="${boleta.seccion}"/></div>
                </div>
                <div class="text-end">
                    <span class="badge text-bg-primary fs-6"><c:out value="${boleta.periodo}"/></span>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty boleta.notas}">
                    <div class="alert alert-warning">No hay notas registradas para este bimestre.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-striped align-middle">
                            <thead>
                            <tr>
                                <th>Curso</th>
                                <th class="text-end">Calificación</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${boleta.notas}" var="n">
                                <tr>
                                    <td><c:out value="${n.curso}"/></td>
                                    <td class="text-end"><c:out value="${n.calificacion}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                            <tfoot>
                            <tr class="table-light">
                                <th>Promedio</th>
                                <th class="text-end">
                                    <c:choose>
                                        <c:when test="${empty boleta.promedio}">-</c:when>
                                        <c:otherwise><c:out value="${boleta.promedio}"/></c:otherwise>
                                    </c:choose>
                                </th>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>

            <h6 class="mt-4">Resumen de asistencia del periodo</h6>
            <div class="row g-2">
                <div class="col-6 col-md-3">
                    <div class="border rounded p-2 text-center">
                        <div class="text-muted small">Asistió</div>
                        <div class="fw-bold">${boleta.asistio}</div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="border rounded p-2 text-center">
                        <div class="text-muted small">Falta</div>
                        <div class="fw-bold text-danger">${boleta.falta}</div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="border rounded p-2 text-center">
                        <div class="text-muted small">Tardanza</div>
                        <div class="fw-bold">${boleta.tardanza}</div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="border rounded p-2 text-center">
                        <div class="text-muted small">Justificada</div>
                        <div class="fw-bold">${boleta.justificada}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
