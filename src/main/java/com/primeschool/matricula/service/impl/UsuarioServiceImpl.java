package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.UsuarioRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Rol;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.RolRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import com.primeschool.matricula.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final List<String> ROLES_PERMITIDOS =
            List.of("ADMINISTRADOR", "DIRECTIVO", "DOCENTE");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario obtenerPorCorreo(String correo) {
        String normalizado = correo == null ? "" : correo.trim().toLowerCase();
        return usuarioRepository.findByCorreo(normalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @Override
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByApellidosAscNombresAsc();
    }

    @Override
    public List<Usuario> listarDocentes() {
        return usuarioRepository.findByRol_NombreRolOrderByApellidosAsc("DOCENTE");
    }

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
}
