package com.primeschool.matricula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada de la aplicación web de matrículas.
 *
 * <p>Inicializa Spring Boot y permite el despliegue como archivo WAR en un
 * contenedor de servlets compatible.</p>
 */
@SpringBootApplication
public class MatriculaApplication extends SpringBootServletInitializer {

    /**
     * Inicia la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(MatriculaApplication.class, args);
    }

    /**
     * Configura la aplicación cuando se despliega como WAR.
     *
     * @param application constructor de la aplicación
     * @return el constructor configurado
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MatriculaApplication.class);
    }
}