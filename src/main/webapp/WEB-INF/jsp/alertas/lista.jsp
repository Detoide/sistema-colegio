<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Alertas"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Alertas tempranas</h3>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Estudiante</th>
                    <th>Tipo</th>
                    <th>Descripción</th>
                    <th>Fecha</th>
                    <th>Estado</th>
                    <th class="text-end">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${alertas}" var="a">
                    <tr>
                        <td><c:out value="${a.estudianteNombre}"/></td>
                        <td>
                            <span class="badge text-bg-warning"><c:out value="${a.tipoRiesgo}"/></span>
                        </td>
                        <td><c:out value="${a.descripcion}"/></td>
                        <td class="small"><c:out value="${a.fechaGeneradaTexto}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${a.atendida}">
                                    <span class="badge text-bg-secondary">Atendida</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-bg-danger">Activa</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:if test="${puedeAtender && !a.atendida}">
                                <form method="post" action="${ctx}/alertas/${a.idAlerta}/atender" class="d-inline">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <button class="btn btn-sm btn-outline-success" type="submit">Atender</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty alertas}">
                    <tr>
                        <td colspan="6" class="text-muted">No hay alertas registradas.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
