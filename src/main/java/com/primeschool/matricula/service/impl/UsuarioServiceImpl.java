package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Rol;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.RolRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import com.primeschool.matricula.service.UsuarioService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de usuarios.
 *
 * <p>Normaliza correos, cifra contraseñas y valida los roles permitidos
 * antes de persistir un usuario.</p>
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final List<String> ROLES_PERMITIDOS =
            List.of("ADMINISTRADOR", "DIRECTIVO", "DOCENTE");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea el servicio con sus repositorios y el codificador de contraseñas.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param rolRepository repositorio de roles
     * @param passwordEncoder codificador de contraseñas
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el correo no está registrado
     */
    @Override
    public Usuario obtenerPorCorreo(String correo) {
        String normalizado = correo == null ? "" : correo.trim().toLowerCase();
        return usuarioRepository.findByCorreo(normalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el usuario no existe
     */
    @Override
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarDocentes() {
        return usuarioRepository.findByRol_NombreRolOrderByApellidosAsc("DOCENTE");
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el correo o el rol no son válidos
     * @throws AccessDeniedException si se intenta crear un administrador sin serlo
     */
    @Override
    @Transactional
    public Usuario crear(UsuarioRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe un usuario con ese correo");
        }
        String nombreRol = request.getRol().trim().toUpperCase();
        if (!ROLES_PERMITIDOS.contains(nombreRol)) {
            throw new ReglaNegocioException("Rol no permitido en este formulario: " + request.getRol());
        }
        if ("ADMINISTRADOR".equals(nombreRol) && !esAdministradorActual()) {
            throw new AccessDeniedException("Solo un administrador puede crear usuarios con rol ADMINISTRADOR");
        }
        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new ReglaNegocioException("Rol inválido: " + request.getRol()));

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    private boolean esAdministradorActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(autoridad -> "ROLE_ADMINISTRADOR".equals(autoridad.getAuthority()));
    }
}
