<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Periodos académicos"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Periodos académicos</h3>
    <div class="d-flex gap-2">
        <c:if test="${esAdminUsuarios}">
            <a class="btn btn-outline-secondary" href="${ctx}/admin/usuarios">Usuarios</a>
        </c:if>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/catalogos">Catálogos</a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Nuevo periodo</h5>
                <form:form modelAttribute="periodo" method="post" action="${ctx}/admin/periodos">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label" for="nombre">Nombre</label>
                        <form:input path="nombre" cssClass="form-control" id="nombre"
                                    placeholder="Ej. Año 2026 o Bimestre I - 2026"/>
                        <form:errors path="nombre" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-2">
                        <label class="form-label" for="fechaInicio">Fecha de inicio</label>
                        <form:input path="fechaInicio" type="date" cssClass="form-control" id="fechaInicio"/>
                        <form:errors path="fechaInicio" cssClass="text-danger small"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="fechaFin">Fecha de fin</label>
                        <form:input path="fechaFin" type="date" cssClass="form-control" id="fechaFin"/>
                        <form:errors path="fechaFin" cssClass="text-danger small"/>
                    </div>
                    <button type="submit" class="btn btn-primary">Crear periodo</button>
                </form:form>
                <div class="alert alert-info small mt-3 mb-0">
                    El periodo anual contiene a los bimestres. Las matrículas se registran en el periodo anual y
                    las notas en el bimestre.
                </div>
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
                            <th>Periodo</th>
                            <th>Inicio</th>
                            <th>Fin</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${periodos}" var="p">
                            <tr>
                                <td><c:out value="${p.nombrePeriodo}"/></td>
                                <td><c:out value="${p.fechaInicio}"/></td>
                                <td><c:out value="${p.fechaFin}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty periodos}">
                            <tr>
                                <td colspan="3" class="text-muted">No hay periodos registrados.</td>
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
