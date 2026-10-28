<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Vacantes"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Vacantes por sección</h3>
    <a class="btn btn-outline-secondary" href="${ctx}/matriculas">Volver a matrículas</a>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Grado</th>
                    <th>Sección</th>
                    <th>Tutor</th>
                    <th>Total</th>
                    <th>Disponibles</th>
                    <th>Ocupadas</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${vacantes}" var="v">
                    <tr>
                        <td><c:out value="${v.grado}"/></td>
                        <td><c:out value="${v.seccion}"/></td>
                        <td><c:out value="${v.tutor}"/></td>
                        <td>${v.total}</td>
                        <td>
                            <c:choose>
                                <c:when test="${v.disponibles > 0}">
                                    <span class="badge text-bg-success">${v.disponibles}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-bg-danger">0</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${v.total - v.disponibles}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty vacantes}">
                    <tr>
                        <td colspan="6" class="text-muted">No hay secciones registradas.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
