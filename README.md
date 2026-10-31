# Sistema de Control de Seguimiento Académico y Matrículas

Aplicación web para **Prime School** que automatiza la matrícula de estudiantes, el registro de calificaciones y asistencia, la generación de boletas y la detección temprana de riesgos académicos.

## Módulos

- **Seguridad:** autenticación con sesión, contraseñas BCrypt, protección CSRF y autorización por roles.
- **Matrículas:** estudiantes, apoderados, periodos, grados, secciones, cursos y control transaccional de vacantes.
- **Registro académico:** notas por curso y periodo, cálculo de promedios y boletas.
- **Asistencia:** registro diario por sección y consulta de faltas o tardanzas.
- **Alertas tempranas:** detección de riesgo por reprobación, deserción o inasistencia.
- **Reportes:** dashboard y consultas consolidadas según el rol del usuario.
- **Administración:** usuarios, roles, periodos, grados, secciones y cursos.

## Tecnologías

- Java 17
- Spring Boot 3.2.12 (Web MVC, Data JPA, Security, Validation)
- JSP, JSTL y Bootstrap 5
- PostgreSQL 16 o superior
- Maven Wrapper 3.9.9 (no requiere Maven instalado)
- Empaquetado WAR para Tomcat 10.1

## Requisitos

- JDK 17 o superior
- PostgreSQL 16 o superior en ejecución
- Base de datos `colegio_db`
- Maven no es necesario: el proyecto incluye Maven Wrapper (`mvnw`)

## Configuración de la base de datos

1. Crear la base de datos:

   ```sql
   CREATE DATABASE colegio_db;
   ```

2. Ejecutar el esquema y los datos de prueba:

   ```bash
   psql -U postgres -d colegio_db -f docs/schema.sql
   ```

   También se puede ejecutar desde pgAdmin u otro cliente SQL.

> **Advertencia:** `docs/schema.sql` elimina y vuelve a crear las tablas (`DROP TABLE`). No debe ejecutarse sobre una base de datos de producción.

El script crea los roles de aplicación, los usuarios de conexión de PostgreSQL y datos de prueba para iniciar sesión. Debe ejecutarse con un superusuario de PostgreSQL (por ejemplo, `postgres`), ya que crea roles y permisos. Antes de ejecutarlo, reemplaza `CAMBIAR_CLAVE_LOCAL` por una contraseña local y define `DB_PASSWORD` con ese mismo valor. La aplicación se conecta con el rol `usr_admin`.

## Variables de configuración

Las variables se pueden definir en el entorno o en la configuración de ejecución. `DB_PASSWORD` es obligatoria y no debe guardarse en el repositorio.

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/colegio_db` | URL JDBC de PostgreSQL |
| `DB_USER` | `usr_admin` | Usuario de conexión |
| `DB_PASSWORD` | Sin valor | Contraseña de conexión (obligatoria) |
| `SERVER_PORT` | `8080` | Puerto HTTP de la aplicación |
| `UMBRAL_NOTA` | `11` | Nota mínima para considerar riesgo de reprobación |
| `FALTAS_ALERTA` | `3` | Cantidad de faltas que genera una alerta |

> `DB_PASSWORD` no tiene valor por defecto; debe definirse antes de iniciar la aplicación o ejecutar las pruebas.

## Ejecución local

Con PostgreSQL en ejecución y el esquema aplicado, usar el Maven Wrapper incluido (no hace falta instalar Maven):

```powershell
.\mvnw.cmd spring-boot:run
```

En macOS/Linux:

```bash
./mvnw spring-boot:run
```

Si el archivo no tiene permisos de ejecución en macOS/Linux: `chmod +x mvnw`.

La aplicación queda disponible en:

```text
http://localhost:8080
```

En IntelliJ IDEA se puede abrir `MatriculaApplication.java` y usar el botón verde. El proyecto incluye la configuración `.run/MatriculaApplication.run.xml`, que agrega las dependencias `provided` (Tomcat y Jasper) al classpath, por lo que no es necesario ejecutar `spring-boot:run` ni abrir la ventana Maven. Si se crea una configuración propia, activar **Add dependencies with "provided" scope to classpath** y definir la variable de entorno `DB_PASSWORD`.

## Cuentas de acceso

### Usuarios de la aplicación

| Rol | Correo |
|---|---|
| Administrador | `admin@colegio.test` |
| Docente | `docente@colegio.test` |
| Directivo | `directivo@colegio.test` |
| Apoderado | `apoderado@correo.test` |

Las contraseñas de las cuentas semilla se almacenan como hashes BCrypt en `docs/schema.sql`; no se documentan en este repositorio.

### Cuenta de conexión a PostgreSQL

La aplicación se conecta con el rol `usr_admin`, creado por `docs/schema.sql`. La contraseña se define al preparar la base de datos y se pasa a la aplicación mediante `DB_PASSWORD`.

La contraseña de `usr_admin` no es la del superusuario `postgres`: ese usuario solo se utiliza para ejecutar el esquema. Si la base de datos ya existe, cambia la contraseña con `ALTER ROLE usr_admin PASSWORD '...'` y actualiza `DB_PASSWORD`. Las credenciales de conexión se pueden sobrescribir con las variables `DB_USER` y `DB_PASSWORD`.

## Rutas principales

| Ruta | Descripción |
|---|---|
| `/login` | Inicio de sesión |
| `/` | Dashboard |
| `/matriculas` | Listado y gestión de matrículas |
| `/vacantes` | Consulta de vacantes por sección |
| `/estudiantes` | Gestión de estudiantes |
| `/apoderados` | Gestión de apoderados |
| `/registro-academico` | Registro de notas y asistencia |
| `/boletas` | Consulta y generación de boletas |
| `/alertas` | Alertas tempranas |
| `/admin/usuarios` | Administración de usuarios |
| `/admin/periodos` | Administración de periodos |
| `/admin/catalogos` | Grados, secciones y cursos |

## Pruebas

```powershell
.\mvnw.cmd test
```

En macOS/Linux:

```bash
./mvnw test
```

Las pruebas son de integración (`@SpringBootTest`) y utilizan la base de datos configurada con los datos semilla de `docs/schema.sql`. Por ello, PostgreSQL debe estar en ejecución y `DB_PASSWORD` debe estar definida antes de lanzarlas.

## Build y despliegue

Generar el WAR:

```powershell
.\mvnw.cmd clean package
```

En macOS/Linux:

```bash
./mvnw clean package
```

El archivo se genera en:

```text
target/sistema-matricula.war
```

Para desplegarlo, copiarlo en el directorio `webapps` de Tomcat 10.1 y configurar las variables de conexión a PostgreSQL.

## Estructura del proyecto

```text
mvnw, mvnw.cmd      Maven Wrapper
.mvn/               Configuración del wrapper
.run/               Configuración de ejecución de IntelliJ

src/main/java/com/primeschool/matricula/
├── config/          Configuración de seguridad y web
├── web/             Controladores MVC
├── service/         Interfaces de negocio
│   └── impl/        Implementaciones de servicios
├── repository/      Repositorios Spring Data JPA
├── model/           Entidades y enumeraciones
├── dto/             Objetos de transferencia y validaciones
├── security/        Autenticación y sesión
└── exception/       Excepciones y manejo global de errores

src/main/resources/
├── application.properties
└── static/          CSS e imágenes

src/main/webapp/WEB-INF/jsp/
└── Vistas JSP

docs/
├── plan_de_implementacion.md
└── schema.sql
```

## Documentación adicional

- `docs/plan_de_implementacion.md`: alcance, arquitectura, requerimientos y plan del proyecto.
- `docs/schema.sql`: esquema de la base de datos, datos de prueba y permisos de PostgreSQL.
