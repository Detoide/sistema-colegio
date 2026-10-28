

-- =====================================================================
-- Sistema de Matrículas y Seguimiento Académico
-- Ejecutar conectado a la base de datos colegio_db
-- =====================================================================

-- Limpieza 
DROP TABLE IF EXISTS alerta_temprana, asistencia, nota, matricula,
    estudiante, seccion, apoderado, usuario, curso, periodo_academico,
    grado, rol CASCADE;

-- ---------------------------------------------------------------------
--  ROL
-- ---------------------------------------------------------------------
CREATE TABLE rol (
    id_rol      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol  VARCHAR(30) NOT NULL UNIQUE
);
COMMENT ON TABLE rol IS
 'Almacena los roles de acceso del sistema (Administrador, Docente, Directivo, Apoderado) para el control de permisos.';

-- ---------------------------------------------------------------------
--  USUARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_rol           INT NOT NULL REFERENCES rol(id_rol),
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    correo           VARCHAR(120) NOT NULL UNIQUE,
    contrasena_hash  VARCHAR(255) NOT NULL,
    activo           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE usuario IS
 'Almacena las cuentas de acceso al sistema (personal y apoderados): datos personales, correo, contraseña cifrada (hash), rol y estado.';

-- ---------------------------------------------------------------------
--  APODERADO
-- ---------------------------------------------------------------------
CREATE TABLE apoderado (
    id_apoderado  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario    INT NOT NULL UNIQUE REFERENCES usuario(id_usuario),
    dni           CHAR(8) NOT NULL UNIQUE,
    telefono      VARCHAR(15)
);
COMMENT ON TABLE apoderado IS
 'Almacena los datos de contacto (DNI, teléfono) de los padres o apoderados, vinculados a una cuenta de usuario para el portal de seguimiento.';

-- ---------------------------------------------------------------------
--  GRADO
-- ---------------------------------------------------------------------
CREATE TABLE grado (
    id_grado      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_grado  VARCHAR(30) NOT NULL,
    nivel         VARCHAR(20) NOT NULL,
    CONSTRAINT uq_grado UNIQUE (nombre_grado, nivel)
);
COMMENT ON TABLE grado IS
 'Almacena los grados académicos de la institución (ej. 1° Secundaria) y el nivel al que pertenecen (Primaria, Secundaria).';

-- ---------------------------------------------------------------------
--  SECCION
-- ---------------------------------------------------------------------
CREATE TABLE seccion (
    id_seccion            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_grado              INT NOT NULL REFERENCES grado(id_grado),
    id_tutor              INT NOT NULL REFERENCES usuario(id_usuario),
    nombre_seccion        VARCHAR(5) NOT NULL,
    vacantes_totales      INT NOT NULL CHECK (vacantes_totales > 0),
    vacantes_disponibles  INT NOT NULL CHECK (vacantes_disponibles >= 0),
    CONSTRAINT uq_seccion UNIQUE (id_grado, nombre_seccion),
    CONSTRAINT ck_vacantes CHECK (vacantes_disponibles <= vacantes_totales)
);
COMMENT ON TABLE seccion IS
 'Almacena las secciones (aulas) de cada grado, su tutor docente y el control de vacantes totales y disponibles.';

-- ---------------------------------------------------------------------
--  ESTUDIANTE
-- ---------------------------------------------------------------------
CREATE TABLE estudiante (
    id_estudiante     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_apoderado      INT NOT NULL REFERENCES apoderado(id_apoderado),
    codigo            VARCHAR(15) NOT NULL UNIQUE,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE NOT NULL
);
COMMENT ON TABLE estudiante IS
 'Almacena los datos personales de los alumnos (código, nombres, apellidos, fecha de nacimiento) y su apoderado responsable.';

-- ---------------------------------------------------------------------
--  PERIODO_ACADEMICO
-- ---------------------------------------------------------------------
CREATE TABLE periodo_academico (
    id_periodo      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_periodo  VARCHAR(30) NOT NULL UNIQUE,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE NOT NULL,
    CONSTRAINT ck_periodo_fechas CHECK (fecha_fin > fecha_inicio)
);
COMMENT ON TABLE periodo_academico IS
 'Almacena los periodos académicos (año escolar, bimestres) con sus fechas de inicio y fin.';

-- ---------------------------------------------------------------------
--  MATRICULA
-- ---------------------------------------------------------------------
CREATE TABLE matricula (
    id_matricula     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_estudiante    INT NOT NULL REFERENCES estudiante(id_estudiante),
    id_seccion       INT NOT NULL REFERENCES seccion(id_seccion),
    id_periodo       INT NOT NULL REFERENCES periodo_academico(id_periodo),
    fecha_matricula  DATE NOT NULL DEFAULT CURRENT_DATE,
    estado           VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                     CHECK (estado IN ('PENDIENTE','CONFIRMADA','ANULADA')),
    CONSTRAINT uq_matricula UNIQUE (id_estudiante, id_periodo)
);
COMMENT ON TABLE matricula IS
 'Almacena el registro de matrícula de un estudiante en una sección durante un periodo, con su fecha y estado (pendiente, confirmada o anulada).';

-- ---------------------------------------------------------------------
--  CURSO
-- ---------------------------------------------------------------------
CREATE TABLE curso (
    id_curso      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_curso  VARCHAR(80) NOT NULL UNIQUE,
    competencia   VARCHAR(150)
);
COMMENT ON TABLE curso IS
 'Almacena los cursos o áreas curriculares dictados en la institución y la competencia que evalúan.';

-- ---------------------------------------------------------------------
--  NOTA
-- ---------------------------------------------------------------------
CREATE TABLE nota (
    id_nota         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_matricula    INT NOT NULL REFERENCES matricula(id_matricula),
    id_curso        INT NOT NULL REFERENCES curso(id_curso),
    id_periodo      INT NOT NULL REFERENCES periodo_academico(id_periodo),
    id_docente      INT NOT NULL REFERENCES usuario(id_usuario),
    calificacion    NUMERIC(4,2) NOT NULL CHECK (calificacion BETWEEN 0 AND 20),
    fecha_registro  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_nota UNIQUE (id_matricula, id_curso, id_periodo)
);
COMMENT ON TABLE nota IS
 'Almacena las calificaciones (escala 0-20) de cada estudiante por curso y periodo, junto con el docente que las registró. El promedio se calcula por consulta.';

-- ---------------------------------------------------------------------
--  ASISTENCIA
-- ---------------------------------------------------------------------
CREATE TABLE asistencia (
    id_asistencia  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_matricula   INT NOT NULL REFERENCES matricula(id_matricula),
    fecha          DATE NOT NULL,
    estado         VARCHAR(15) NOT NULL
                   CHECK (estado IN ('ASISTIO','FALTA','TARDANZA','JUSTIFICADA')),
    CONSTRAINT uq_asistencia UNIQUE (id_matricula, fecha)
);
COMMENT ON TABLE asistencia IS
 'Almacena el registro diario de asistencia de cada estudiante matriculado (asistió, falta, tardanza o justificada).';

-- ---------------------------------------------------------------------
--  ALERTA_TEMPRANA
-- ---------------------------------------------------------------------
CREATE TABLE alerta_temprana (
    id_alerta       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_matricula    INT NOT NULL REFERENCES matricula(id_matricula),
    tipo_riesgo     VARCHAR(30) NOT NULL
                    CHECK (tipo_riesgo IN ('REPROBACION','DESERCION','INASISTENCIA')),
    descripcion     VARCHAR(255) NOT NULL,
    fecha_generada  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atendida        BOOLEAN NOT NULL DEFAULT FALSE
);
COMMENT ON TABLE alerta_temprana IS
 'Almacena las alertas generadas por riesgo de reprobación, deserción o inasistencia de un estudiante y si ya fueron atendidas.';

-- ---------------------------------------------------------------------
-- ÍNDICES
-- ---------------------------------------------------------------------
CREATE INDEX idx_usuario_rol         ON usuario(id_rol);
CREATE INDEX idx_estudiante_apod     ON estudiante(id_apoderado);
CREATE INDEX idx_seccion_grado       ON seccion(id_grado);
CREATE INDEX idx_matricula_seccion   ON matricula(id_seccion);
CREATE INDEX idx_matricula_periodo   ON matricula(id_periodo);
CREATE INDEX idx_nota_matricula      ON nota(id_matricula);
CREATE INDEX idx_nota_curso          ON nota(id_curso);
CREATE INDEX idx_asistencia_fecha    ON asistencia(fecha);
CREATE INDEX idx_alerta_pendientes   ON alerta_temprana(atendida) WHERE atendida = FALSE;

-- ---------------------------------------------------------------------
-- DATOS BASE roles
-- ---------------------------------------------------------------------
INSERT INTO rol (nombre_rol) VALUES
 ('ADMINISTRADOR'), ('DOCENTE'), ('DIRECTIVO'), ('APODERADO');


-- ---------------------------------------------------------------------
-- DATOS DE PRUEBA
-- ----------------------------------------------------------------------- USUARIOS 
INSERT INTO usuario (id_rol, nombres, apellidos, correo, contrasena_hash) VALUES
 (1, 'Ana',    'Torres Vega',   'admin@colegio.test',    '123'),
 (2, 'Carlos', 'Requejo Luna',  'docente@colegio.test',  '123'),
 (3, 'Luis',   'Paredes Rivas', 'directivo@colegio.test','123'),
 (4, 'Rosa',   'Quispe Huamán', 'apoderado@correo.test', '123');
 
-- APODERADO (usuario 4)
INSERT INTO apoderado (id_usuario, dni, telefono) VALUES
 (4, '40111222', '987654321');
 
-- GRADOS
INSERT INTO grado (nombre_grado, nivel) VALUES
 ('1° Secundaria', 'Secundaria'),
 ('2° Secundaria', 'Secundaria');
 
-- SECCIONES (la 1°B tiene 1 sola vacante y ya está llena: sirve para probar el control de vacantes)
INSERT INTO seccion (id_grado, id_tutor, nombre_seccion, vacantes_totales, vacantes_disponibles) VALUES
 (1, 2, 'A', 30, 28),
 (1, 2, 'B',  1,  0);
 
-- ESTUDIANTES
INSERT INTO estudiante (id_apoderado, codigo, nombres, apellidos, fecha_nacimiento) VALUES
 (1, 'E2026001', 'Lucía',  'Quispe Salas', '2013-04-12'),
 (1, 'E2026002', 'Mateo',  'Quispe Salas', '2012-08-30'),
 (1, 'E2026003', 'Diego',  'Quispe Salas', '2012-11-03');
 
-- PERIODOS (1 = año escolar para matrícula; 2 = bimestre para notas)
INSERT INTO periodo_academico (nombre_periodo, fecha_inicio, fecha_fin) VALUES
 ('Año Escolar 2026', '2026-03-16', '2026-12-18'),
 ('Bimestre 1',       '2026-03-16', '2026-05-22');
 
-- MATRÍCULAS
INSERT INTO matricula (id_estudiante, id_seccion, id_periodo, fecha_matricula, estado) VALUES
 (1, 1, 1, '2026-02-10', 'CONFIRMADA'),
 (2, 1, 1, '2026-02-10', 'PENDIENTE'),
 (3, 2, 1, '2026-02-11', 'CONFIRMADA');
 
-- CURSOS
INSERT INTO curso (nombre_curso, competencia) VALUES
 ('Matemática',   'Resuelve problemas de cantidad, regularidad y forma'),
 ('Comunicación', 'Se comunica oralmente y escribe diversos tipos de textos');
 
-- NOTAS (Bimestre 1; docente = usuario 2). La matrícula 3 tiene notas bajas.
INSERT INTO nota (id_matricula, id_curso, id_periodo, id_docente, calificacion) VALUES
 (1, 1, 2, 2, 16),
 (1, 2, 2, 2, 15),
 (3, 1, 2, 2,  8),
 (3, 2, 2, 2,  7);
 
-- ASISTENCIA
INSERT INTO asistencia (id_matricula, fecha, estado) VALUES
 (1, '2026-03-16', 'ASISTIO'),
 (1, '2026-03-17', 'TARDANZA'),
 (3, '2026-03-16', 'FALTA'),
 (3, '2026-03-17', 'FALTA');
 
-- ALERTA TEMPRANA
INSERT INTO alerta_temprana (id_matricula, tipo_riesgo, descripcion, atendida) VALUES
 (3, 'REPROBACION', 'Promedio del Bimestre 1 menor a 11 y faltas consecutivas', FALSE);

-- 1. Quitar permisos por defecto al público
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
 
-- 2. Roles de grupo (no inician sesión, solo agrupan permisos)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rol_admin')     THEN CREATE ROLE rol_admin     NOLOGIN; END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rol_docente')   THEN CREATE ROLE rol_docente   NOLOGIN; END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rol_directivo') THEN CREATE ROLE rol_directivo NOLOGIN; END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rol_apoderado') THEN CREATE ROLE rol_apoderado NOLOGIN; END IF;
END $$;
 
-- 3. Usuarios de conexión (los que usará la aplicación Java en pruebas)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'usr_admin')     THEN CREATE ROLE usr_admin     LOGIN PASSWORD 'Test123' IN ROLE rol_admin;     END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'usr_docente')   THEN CREATE ROLE usr_docente   LOGIN PASSWORD 'Test123' IN ROLE rol_docente;   END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'usr_directivo') THEN CREATE ROLE usr_directivo LOGIN PASSWORD 'Test123' IN ROLE rol_directivo; END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'usr_apoderado') THEN CREATE ROLE usr_apoderado LOGIN PASSWORD 'Test123' IN ROLE rol_apoderado; END IF;
END $$;
 
-- 4. Acceso a la base de datos y al esquema
GRANT CONNECT ON DATABASE colegio_db TO rol_admin, rol_docente, rol_directivo, rol_apoderado;
GRANT USAGE ON SCHEMA public TO rol_admin, rol_docente, rol_directivo, rol_apoderado;
 
-- 5. ADMINISTRADOR: gestión completa de datos (sin permisos de estructura)
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO rol_admin;
 
-- 6. DOCENTE: consulta datos académicos y registra notas y asistencia (no elimina)
GRANT SELECT ON grado, seccion, estudiante, matricula, curso,
                periodo_academico, nota, asistencia, alerta_temprana TO rol_docente;
GRANT SELECT (id_usuario, nombres, apellidos) ON usuario TO rol_docente;
GRANT INSERT, UPDATE ON nota, asistencia TO rol_docente;
 
-- 7. DIRECTIVO: solo lectura para reportes; puede marcar alertas como atendidas
GRANT SELECT ON grado, seccion, estudiante, apoderado, matricula, curso,
                periodo_academico, nota, asistencia, alerta_temprana TO rol_directivo;
GRANT SELECT (id_usuario, id_rol, nombres, apellidos, correo, activo) ON usuario TO rol_directivo;
GRANT UPDATE (atendida) ON alerta_temprana TO rol_directivo;
 
-- 8. APODERADO: solo lectura del portal de seguimiento (sin acceso a usuarios)
GRANT SELECT ON grado, seccion, estudiante, matricula, curso,
                periodo_academico, nota, asistencia, alerta_temprana TO rol_apoderado;


SELECT grantee, table_name, column_name, privilege_type
FROM information_schema.column_privileges
WHERE grantee LIKE 'rol_%' AND table_name = 'usuario'
ORDER BY grantee, column_name;