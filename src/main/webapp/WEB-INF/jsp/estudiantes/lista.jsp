<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Estudiantes"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Estudiantes</h3>
    <form method="get" action="${ctx}/estudiantes" class="d-flex gap-2">
        <input type="search" class="form-control" name="q" value="${fn:escapeXml(q)}"
               placeholder="Código, nombres o apellidos">
        <button class="btn btn-outline-primary" type="submit">Buscar</button>
    </form>
</div>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">
                    <c:choose>
                        <c:when test="${estudiante.idEstudiante != null}">Editar estudiante</c:when>
                        <c:otherwise>Nuevo estudiante</c:otherwise>
                    </c:choose>
                </h5>
                <form:form modelAttribute="estudiante" method="post" action="${ctx}/estudiantes">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <form:hidden path="idEstudiante"/>
                    <div class="mb-2">
                        <label class="form-label" for="codigo">Código</label>
                        <form:input path="codigo" cssClass="form-control" id="codigo"/>
                        <form:errors path="codigo" cssClass="text-danger small"/>
                    </div>
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
                        <label class="form-label" for="fechaNacimiento">Fecha de nacimiento</label>
                        <form:input path="fechaNacimiento" type="date" cssClass="form-control" id="fechaNacimiento"/>
                        <form:errors path="fechaNacimiento" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="apoderadoId">Apoderado</label>
                        <form:select path="apoderadoId" cssClass="form-select" id="apoderadoId">
                            <form:option value="" label="-- Seleccione --"/>
                            <c:forEach items="${apoderados}" var="a">
                                <form:option value="${a.idApoderado}"
                                             label="${a.nombreCompleto} - DNI ${a.dni}"/>
                            </c:forEach>
                        </form:select>
                        <form:errors path="apoderadoId" cssClass="text-danger small"/>
                        <c:if test="${empty apoderados}">
                            <div class="form-text text-danger">
                                Primero registre un <a href="${ctx}/apoderados">apoderado</a>.
                            </div>
                        </c:if>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Guardar</button>
                        <c:if test="${estudiante.idEstudiante != null}">
                            <a class="btn btn-outline-secondary" href="${ctx}/estudiantes">Limpiar</a>
                        </c:if>
                    </div>
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
                            <th>Código</th>
                            <th>Estudiante</th>
                            <th>Nacimiento</th>
                            <th>Apoderado</th>
                            <th class="text-end">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${estudiantes}" var="e">
                            <tr>
                                <td><c:out value="${e.codigo}"/></td>
                                <td><c:out value="${e.nombreCompleto}"/></td>
                                <td><c:out value="${e.fechaNacimiento}"/></td>
                                <td><c:out value="${e.apoderado.nombreCompleto}"/></td>
                                <td class="text-end">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${ctx}/estudiantes/${e.idEstudiante}/editar">Editar</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty estudiantes}">
                            <tr>
                                <td colspan="5" class="text-muted">No se encontraron estudiantes.</td>
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
