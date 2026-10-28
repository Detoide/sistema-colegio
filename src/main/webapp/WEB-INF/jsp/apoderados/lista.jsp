<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Apoderados"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Apoderados</h3>
    <a class="btn btn-outline-secondary" href="${ctx}/estudiantes">Estudiantes</a>
</div>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Nuevo apoderado</h5>
                <form:form modelAttribute="apoderado" method="post" action="${ctx}/apoderados">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label" for="nombres">Nombres</label>
                        <form:input path="nombres" cssClass="form-control" id="nombres"/>
                        <form:errors path="nombres" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-2">
                        <label class="form-label" for="apellidos">Apellidos</label>
                        <form:input path="apellidos" cssClass="form-control" id="apellidos"/>
                        <form:errors path="apellidos" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-2">
                        <label class="form-label" for="correo">Correo</label>
                        <form:input path="correo" type="email" cssClass="form-control" id="correo"/>
                        <form:errors path="correo" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-2">
                        <label class="form-label" for="dni">DNI</label>
                        <form:input path="dni" cssClass="form-control" id="dni" maxlength="8"/>
                        <form:errors path="dni" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-2">
                        <label class="form-label" for="telefono">Teléfono</label>
                        <form:input path="telefono" cssClass="form-control" id="telefono"/>
                        <form:errors path="telefono" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Contraseña</label>
                        <form:password path="password" cssClass="form-control" id="password"/>
                        <form:errors path="password" cssClass="text-danger small"/>
                    </div>
                    <button type="submit" class="btn btn-primary">Registrar apoderado</button>
                </form:form>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead>
                        <tr>
                            <th>DNI</th>
                            <th>Apoderado</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${apoderados}" var="a">
                            <tr>
                                <td><c:out value="${a.dni}"/></td>
                                <td><c:out value="${a.nombreCompleto}"/></td>
                                <td><c:out value="${a.correo}"/></td>
                                <td><c:out value="${a.telefono}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty apoderados}">
                            <tr>
                                <td colspan="4" class="text-muted">No hay apoderados registrados.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
