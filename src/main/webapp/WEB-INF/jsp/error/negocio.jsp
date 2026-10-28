<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="error" value=""/>
<c:set var="pageTitle" value="No se pudo completar la operación"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="card">
    <div class="card-body">
        <h4 class="card-title">No se pudo completar la operación</h4>
        <p class="text-muted"><c:out value="${mensaje}"/></p>
        <a class="btn btn-primary" href="${ctx}/">Volver al inicio</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
