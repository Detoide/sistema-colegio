<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="error" value=""/>
<c:set var="pageTitle" value="Error"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="card">
    <div class="card-body">
        <h4 class="card-title">Ocurrió un error</h4>
        <p class="text-muted">
            <c:out value="${empty message ? 'No se pudo procesar la solicitud.' : message}"/>
        </p>
        <c:if test="${not empty status}">
            <p class="small text-muted">Código de estado: <c:out value="${status}"/></p>
        </c:if>
        <a class="btn btn-primary" href="${ctx}/">Volver al inicio</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
