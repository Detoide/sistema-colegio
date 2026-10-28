<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Usuarios"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Usuarios del sistema</h3>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary" href="${ctx}/admin/periodos">Periodos</a>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/catalogos">Catálogos</a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Nuevo usuario</h5>
                <form:form modelAttribute="usuarioForm" method="post" action="${ctx}/admin/usuarios">
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
                        <label class="form-label" for="rol">Rol</label>
                        <form:select path="rol" cssClass="form-select" id="rol">
                            <form:option value="" label="-- Seleccione --"/>
                            <form:options items="${roles}"/>
                        </form:select>
                        <form:errors path="rol" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Contraseña</label>
                        <form:password path="password" cssClass="form-control" id="password"/>
                        <form:errors path="password" cssClass="text-danger small"/>
                    </div>
                    <button type="submit" class="btn btn-primary">Crear usuario</button>
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
                            <th>Usuario</th>
                            <th>Correo</th>
                            <th>Rol</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${usuarios}" var="u">
                            <tr>
                                <td><c:out value="${u.nombreCompleto}"/></td>
                                <td><c:out value="${u.correo}"/></td>
                                <td><span class="badge text-bg-primary"><c:out value="${u.rol.nombreRol}"/></span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.activo}">
                                            <span class="badge text-bg-success">Activo</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge text-bg-secondary">Inactivo</span>
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
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
