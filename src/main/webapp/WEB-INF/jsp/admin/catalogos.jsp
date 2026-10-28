<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Catálogos"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Grados, cursos y secciones</h3>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary" href="${ctx}/admin/usuarios">Usuarios</a>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/periodos">Periodos</a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card mb-3">
            <div class="card-body">
                <h5 class="card-title">Nuevo grado</h5>
                <form method="post" action="${ctx}/admin/grados">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label" for="nombreGrado">Nombre</label>
                        <input class="form-control" id="nombreGrado" name="nombreGrado" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="nivel">Nivel</label>
                        <input class="form-control" id="nivel" name="nivel" required>
                    </div>
                    <button class="btn btn-primary btn-sm" type="submit">Crear grado</button>
                </form>
            </div>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Nuevo curso</h5>
                <form method="post" action="${ctx}/admin/cursos">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label" for="nombreCurso">Nombre</label>
                        <input class="form-control" id="nombreCurso" name="nombreCurso" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="competencia">Competencia</label>
                        <input class="form-control" id="competencia" name="competencia">
                    </div>
                    <button class="btn btn-primary btn-sm" type="submit">Crear curso</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Nueva sección</h5>
                <form method="post" action="${ctx}/admin/secciones" class="row g-2 align-items-end">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="col-md-4">
                        <label class="form-label" for="gradoId">Grado</label>
                        <select class="form-select" id="gradoId" name="gradoId" required>
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${grados}" var="g">
                                <option value="${g.idGrado}">
                                    <c:out value="${g.descripcion}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="tutorId">Tutor</label>
                        <select class="form-select" id="tutorId" name="tutorId" required>
                            <option value="">-- Seleccione --</option>
                            <c:forEach items="${docentes}" var="d">
                                <option value="${d.idUsuario}">
                                    <c:out value="${d.nombreCompleto}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label" for="nombreSeccion">Sección</label>
                        <input class="form-control" id="nombreSeccion" name="nombreSeccion" maxlength="5" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label" for="vacantesTotales">Vacantes</label>
                        <input class="form-control" id="vacantesTotales" name="vacantesTotales" type="number" min="1" required>
                    </div>
                    <div class="col-12">
                        <button class="btn btn-primary btn-sm" type="submit">Crear sección</button>
                        <c:if test="${empty docentes}">
                            <span class="text-danger small ms-2">
                                Cree primero un usuario con rol DOCENTE.
                            </span>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>

        <div class="card mt-3">
            <div class="card-body">
                <h5 class="card-title">Secciones</h5>
                <div class="table-responsive">
                    <table class="table table-sm align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Grado</th>
                            <th>Sección</th>
                            <th>Tutor</th>
                            <th>Vacantes</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${secciones}" var="s">
                            <tr>
                                <td><c:out value="${s.grado.nombreGrado}"/></td>
                                <td><c:out value="${s.nombreSeccion}"/></td>
                                <td><c:out value="${s.tutor.nombreCompleto}"/></td>
                                <td>${s.vacantesDisponibles} / ${s.vacantesTotales}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty secciones}">
                            <tr>
                                <td colspan="4" class="text-muted">No hay secciones registradas.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mt-1">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Grados</h5>
                <table class="table table-sm mb-0">
                    <thead>
                    <tr><th>Grado</th><th>Nivel</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${grados}" var="g">
                        <tr>
                            <td><c:out value="${g.nombreGrado}"/></td>
                            <td><c:out value="${g.nivel}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty grados}">
                        <tr><td colspan="2" class="text-muted">No hay grados.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Cursos</h5>
                <table class="table table-sm mb-0">
                    <thead>
                    <tr><th>Curso</th><th>Competencia</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${cursos}" var="c">
                        <tr>
                            <td><c:out value="${c.nombreCurso}"/></td>
                            <td><c:out value="${c.competencia}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty cursos}">
                        <tr><td colspan="2" class="text-muted">No hay cursos.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
