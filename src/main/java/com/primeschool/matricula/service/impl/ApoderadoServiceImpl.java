package com.primeschool.matricula.service.impl;

import com.primeschool.matricula.dto.ApoderadoRequest;
import com.primeschool.matricula.exception.RecursoNoEncontradoException;
import com.primeschool.matricula.exception.ReglaNegocioException;
import com.primeschool.matricula.model.entity.Apoderado;
import com.primeschool.matricula.model.entity.Rol;
import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.ApoderadoRepository;
import com.primeschool.matricula.repository.RolRepository;
import com.primeschool.matricula.repository.UsuarioRepository;
import com.primeschool.matricula.service.ApoderadoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de apoderados.
 */
@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea el servicio con sus repositorios y el codificador de contraseñas.
     *
     * @param apoderadoRepository repositorio de apoderados
     * @param usuarioRepository repositorio de usuarios
     * @param rolRepository repositorio de roles
     * @param passwordEncoder codificador de contraseñas
     */
    public ApoderadoServiceImpl(ApoderadoRepository apoderadoRepository,
                                UsuarioRepository usuarioRepository,
                                RolRepository rolRepository,
                                PasswordEncoder passwordEncoder) {
        this.apoderadoRepository = apoderadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Apoderado> listar() {
        return apoderadoRepository.findAllByOrderByUsuario_ApellidosAscUsuario_NombresAsc();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ReglaNegocioException si el correo, DNI o rol no son válidos
     */
    @Override
    @Transactional
    public Apoderado crear(ApoderadoRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe un usuario con ese correo");
        }
        if (apoderadoRepository.existsByDni(request.getDni())) {
            throw new ReglaNegocioException("Ya existe un apoderado con ese DNI");
        }
        Rol rol = rolRepository.findByNombreRol("APODERADO")
                .orElseThrow(() -> new ReglaNegocioException("No existe el rol APODERADO"));

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        Apoderado apoderado = new Apoderado();
        apoderado.setUsuario(usuario);
        apoderado.setDni(request.getDni());
        apoderado.setTelefono(request.getTelefono());
        return apoderadoRepository.save(apoderado);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RecursoNoEncontradoException si el apoderado no existe
     */
    @Override
    public Apoderado obtenerPorUsuario(Integer idUsuario) {
        return apoderadoRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Apoderado no encontrado"));
    }
}
