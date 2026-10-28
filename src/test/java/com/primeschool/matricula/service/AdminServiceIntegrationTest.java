package com.primeschool.matricula.service;

import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Grado;
import com.primeschool.matricula.model.entity.Seccion;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.GradoRepository;
import com.primeschool.matricula.repository.SeccionRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AdminServiceIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CatalogoService catalogoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GradoRepository gradoRepository;
    @Autowired
    private SeccionRepository seccionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario docente;
    private Usuario apoderado;
    private Grado grado;

    @BeforeEach
    void prepararDatos() {
        docente = usuarioRepository.findByCorreo("docente@colegio.test").orElseThrow();
        apoderado = usuarioRepository.findByCorreo("apoderado@correo.test").orElseThrow();
        grado = gradoRepository.findAllByOrderByNivelAscNombreGradoAsc().get(0);
    }

    @Test
    void creaUsuarioDocenteConCorreoNormalizadoYClaveCifrada() {
        String correo = "Nuevo.Docente." + (System.nanoTime() % 1_000_000L) + "@Test.Local";
        UsuarioRequest request = usuarioRequest(correo, "DOCENTE");

        Usuario creado = usuarioService.crear(request);

        assertThat(creado.getCorreo()).isEqualTo(correo.toLowerCase());
        assertThat(creado.getRol().getNombreRol()).isEqualTo("DOCENTE");
        assertThat(passwordEncoder.matches("clave123", creado.getContrasenaHash())).isTrue();
        assertThat(usuarioRepository.findByCorreo(correo.toLowerCase())).isPresent();
    }

    @Test
    void rechazaRolApoderadoEnFormularioDeUsuarios() {
        UsuarioRequest request = usuarioRequest(
                "apoderado." + (System.nanoTime() % 1_000_000L) + "@test.local", "APODERADO");

        assertThatThrownBy(() -> usuarioService.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Rol no permitido");
    }

    @Test
    void rechazaCorreoDuplicado() {
        UsuarioRequest request = usuarioRequest("admin@colegio.test", "ADMINISTRADOR");

        assertThatThrownBy(() -> usuarioService.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Ya existe un usuario");
    }

    @Test
    void creaSeccionConVacantesYValidaReglas() {
        catalogoService.crearSeccion(grado.getIdGrado(), docente.getIdUsuario(), "Z9", 5);

        Seccion seccion = seccionRepository
                .findByTutor_IdUsuarioOrderByGrado_NombreGradoAscNombreSeccionAsc(docente.getIdUsuario())
                .stream()
                .filter(s -> "Z9".equals(s.getNombreSeccion()))
                .findFirst()
                .orElseThrow();

        assertThat(seccion.getVacantesTotales()).isEqualTo(5);
        assertThat(seccion.getVacantesDisponibles()).isEqualTo(5);

        assertThatThrownBy(() -> catalogoService.crearSeccion(
                grado.getIdGrado(), docente.getIdUsuario(), "Z9", 3))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Ya existe una sección");

        assertThatThrownBy(() -> catalogoService.crearSeccion(
                grado.getIdGrado(), docente.getIdUsuario(), "Z8", 0))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("mayores a cero");

        assertThatThrownBy(() -> catalogoService.crearSeccion(
                grado.getIdGrado(), apoderado.getIdUsuario(), "Z7", 4))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("rol DOCENTE");
    }

    private UsuarioRequest usuarioRequest(String correo, String rol) {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombres("Usuario");
        request.setApellidos("Prueba");
        request.setCorreo(correo);
        request.setPassword("clave123");
        request.setRol(rol);
        return request;
    }
}
