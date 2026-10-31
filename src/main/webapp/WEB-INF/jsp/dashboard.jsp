<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Inicio"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <div>
        <h3 class="mb-0">Hola, <c:out value="${dto.nombreUsuario}"/></h3>
        <span class="text-muted">Rol: <c:out value="${dto.rol}"/></span>
    </div>
    <c:if test="${esAdmin}">
        <a class="btn btn-primary" href="${ctx}/matriculas/nueva">Registrar matrícula</a>
    </c:if>
</div>

<div class="row g-3 mb-4">
    <div class="col-6 col-lg-3">
        <div class="card stat-card">
            <div class="card-body">
                <div class="stat-label">
                    <c:choose>
                        <c:when test="${dto.rol == 'APODERADO'}">Hijos</c:when>
                        <c:when test="${dto.rol == 'DOCENTE'}">Estudiantes a cargo</c:when>
                        <c:otherwise>Estudiantes</c:otherwise>
                    </c:choose>
                </div>
                <div class="stat-value">${dto.estudiantes}</div>
            </div>
        </div>
    </div>
    <c:if test="${dto.rol != 'APODERADO'}">
        <div class="col-6 col-lg-3">
            <div class="card stat-card">
                <div class="card-body">
                    <div class="stat-label">
                        <c:choose>
                            <c:when test="${dto.rol == 'DOCENTE'}">Secciones a cargo</c:when>
                            <c:otherwise>Secciones</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-value">${dto.secciones}</div>
                </div>
            </div>
        </div>
    </c:if>
    <div class="col-6 col-lg-3">
        <div class="card stat-card">
            <div class="card-body">
                <div class="stat-label">Matrículas</div>
                <div class="stat-value">${dto.matriculas}</div>
            </div>
        </div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card stat-card">
            <div class="card-body">
                <div class="stat-label">Alertas activas</div>
                <div class="stat-value text-danger">${dto.alertasActivas}</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-5">
        <div class="card h-100">
            <div class="card-body">
                <h5 class="card-title">Accesos rápidos</h5>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-outline-primary btn-sm" href="${ctx}/matriculas">Matrículas</a>
                    <c:if test="${dto.rol != 'APODERADO'}">
                        <a class="btn btn-outline-primary btn-sm" href="${ctx}/registro-academico">Registro académico</a>
                    </c:if>
                    <a class="btn btn-outline-primary btn-sm" href="${ctx}/boletas">Boletas</a>
                    <a class="btn btn-outline-primary btn-sm" href="${ctx}/alertas">Alertas</a>
                    <c:if test="${esAdmin}">
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/estudiantes">Estudiantes</a>
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/apoderados">Apoderados</a>
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/vacantes">Vacantes</a>
                        <c:if test="${esAdminUsuarios}">
                            <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/usuarios">Usuarios</a>
                        </c:if>
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/periodos">Periodos</a>
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/catalogos">Catálogos</a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-7">
        <div class="card h-100">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Alertas</h5>
                    <a class="small" href="${ctx}/alertas">Ver todas</a>
                </div>
                <c:choose>
                    <c:when test="${empty dto.alertas}">
                        <p class="text-muted mb-0 mt-3">No hay alertas registradas.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive mt-2">
                            <table class="table table-sm align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>Estudiante</th>
                                    <th>Tipo</th>
                                    <th>Descripción</th>
                                    <th>Estado</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dto.alertas}" var="a" end="4">
                                    <tr>
                                        <td><c:out value="${a.estudianteNombre}"/></td>
                                        <td><span class="badge text-bg-warning"><c:out value="${a.tipoRiesgo}"/></span></td>
                                        <td class="small"><c:out value="${a.descripcion}"/></td>
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
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<c:if test="${dto.rol == 'DOCENTE' && not empty dto.seccionesDocente}">
    <div class="card mt-3">
        <div class="card-body">
            <h5 class="card-title">Mis secciones</h5>
            <div class="table-responsive">
                <table class="table table-sm mb-0">
                    <thead>
                    <tr>
                        <th>Sección</th>
                        <th>Vacantes disponibles</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${dto.seccionesDocente}" var="s">
                        <tr>
                            <td><c:out value="${s.descripcion}"/></td>
                            <td>${s.vacantesDisponibles} de ${s.vacantesTotales}</td>
                            <td>
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${ctx}/registro-academico?seccionId=${s.idSeccion}">Registro</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${dto.rol == 'APODERADO' && not empty dto.hijos}">
    <div class="card mt-3">
        <div class="card-body">
            <h5 class="card-title">Mis hijos</h5>
            <div class="table-responsive">
                <table class="table table-sm mb-0">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Estudiante</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${dto.hijos}" var="h">
                        <tr>
                            <td><c:out value="${h.codigo}"/></td>
                            <td><c:out value="${h.nombreCompleto}"/></td>
                            <td>
                                <a class="btn btn-sm btn-outline-primary" href="${ctx}/boletas">Ver boletas</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
