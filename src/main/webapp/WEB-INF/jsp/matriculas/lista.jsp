<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Matrículas"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Matrículas</h3>
    <c:if test="${esAdmin}">
        <div class="d-flex gap-2">
            <a class="btn btn-outline-secondary" href="${ctx}/vacantes">Vacantes</a>
            <a class="btn btn-primary" href="${ctx}/matriculas/nueva">Nueva matrícula</a>
        </div>
    </c:if>
</div>

<div class="card">
    <div class="card-body">
        <c:choose>
            <c:when test="${empty matriculas}">
                <p class="text-muted mb-0">No hay matrículas registradas.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Código</th>
                            <th>Estudiante</th>
                            <th>Sección</th>
                            <th>Periodo</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th class="text-end">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${matriculas}" var="m">
                            <tr>
                                <td><c:out value="${m.estudiante.codigo}"/></td>
                                <td><c:out value="${m.estudiante.nombreCompleto}"/></td>
                                <td><c:out value="${m.seccion.descripcion}"/></td>
                                <td><c:out value="${m.periodo.nombrePeriodo}"/></td>
                                <td><c:out value="${m.fechaMatriculaTexto}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${m.estado.name() == 'PENDIENTE'}">
                                            <span class="badge text-bg-warning">Pendiente</span>
                                        </c:when>
                                        <c:when test="${m.estado.name() == 'CONFIRMADA'}">
                                            <span class="badge text-bg-success">Confirmada</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge text-bg-secondary">Anulada</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${ctx}/boletas?matriculaId=${m.idMatricula}">Boleta</a>
                                    <c:if test="${esAdmin}">
                                        <c:if test="${m.estado.name() == 'PENDIENTE'}">
                                            <form method="post" action="${ctx}/matriculas/${m.idMatricula}/confirmar" class="d-inline">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button class="btn btn-sm btn-success" type="submit">Confirmar</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${m.estado.name() != 'ANULADA'}">
                                            <form method="post" action="${ctx}/matriculas/${m.idMatricula}/anular" class="d-inline"
                                                  onsubmit="return confirm('¿Anular esta matrícula? La vacante será liberada.');">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button class="btn btn-sm btn-outline-danger" type="submit">Anular</button>
                                            </form>
                                        </c:if>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
