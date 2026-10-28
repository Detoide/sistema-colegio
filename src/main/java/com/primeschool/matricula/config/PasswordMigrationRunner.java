package com.primeschool.matricula.config;

import com.primeschool.matricula.model.entity.Usuario;
import com.primeschool.matricula.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean migrar;

    public PasswordMigrationRunner(UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${app.security.migrar-passwords:true}") boolean migrar) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.migrar = migrar;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!migrar) {
            return;
        }
        int migrados = 0;
        for (Usuario usuario : usuarioRepository.findAll()) {
            String hash = usuario.getContrasenaHash();
            if (hash != null && !hash.startsWith("$2")) {
                usuario.setContrasenaHash(passwordEncoder.encode(hash));
                usuarioRepository.save(usuario);
                migrados++;
            }
        }
        if (migrados > 0) {
            log.info("Se migraron {} contraseñas a BCrypt", migrados);
        }
    }
}
