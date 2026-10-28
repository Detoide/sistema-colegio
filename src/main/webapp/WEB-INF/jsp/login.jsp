<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ingreso | Prime School</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctx}/css/app.css" rel="stylesheet">
</head>
<body class="login-page">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card p-4 login-card">
                <div class="text-center mb-3">
                    <img src="${ctx}/img/prime-school.svg" class="login-logo mb-2" alt="Prime School">
                    <h4 class="mb-1">Prime School</h4>
                    <p class="text-muted small mb-0">Bienvenido de nuevo</p>
                </div>
                <c:if test="${param.error != null}">
                    <div class="alert alert-danger py-2">Usuario o contraseña incorrectos.</div>
                </c:if>
                <c:if test="${param.logout != null}">
                    <div class="alert alert-success py-2">Sesión cerrada correctamente.</div>
                </c:if>
                <form method="post" action="${ctx}/login">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-3">
                        <label class="form-label" for="username">Nombre de Usuario</label>
                        <input type="email" class="form-control" id="username" name="username"
                               placeholder="Introduce tu correo" autocomplete="username" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Contraseña</label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Introduce tu contraseña" autocomplete="current-password" required>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="remember-me" name="remember-me">
                            <label class="form-check-label small" for="remember-me">Recordarme</label>
                        </div>
                        <span class="text-muted small">¿Olvidaste tu contraseña?</span>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 text-uppercase fw-semibold">Iniciar sesión</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
