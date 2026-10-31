package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio de acceso a los estudiantes registrados.
 */
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    /**
     * Lista todos los estudiantes ordenados por apellidos y nombres.
     *
     * @return estudiantes ordenados alfabéticamente
     */
    List<Estudiante> findAllByOrderByApellidosAscNombresAsc();

    /**
     * Lista los estudiantes asociados al usuario de un apoderado.
     *
     * @param idUsuario identificador del usuario apoderado
     * @return estudiantes a cargo del apoderado
     */
    List<Estudiante> findByApoderado_Usuario_IdUsuarioOrderByApellidosAscNombresAsc(Integer idUsuario);

    /**
     * Comprueba si existe un estudiante con el código indicado.
     *
     * @param codigo código del estudiante
     * @return {@code true} si el código ya está registrado
     */
    boolean existsByCodigo(String codigo);

    /**
     * Comprueba si otro estudiante distinto ya usa el código indicado.
     *
     * @param codigo código a verificar
     * @param idEstudiante identificador del estudiante que se está editando
     * @return {@code true} si el código pertenece a otro estudiante
     */
    boolean existsByCodigoAndIdEstudianteNot(String codigo, Integer idEstudiante);

    /**
     * Busca estudiantes por coincidencia parcial en código, nombres o apellidos.
     *
     * @param q texto de búsqueda
     * @return estudiantes que coinciden con el texto
     */
    @Query("""
            select e from Estudiante e
            where lower(e.codigo) like lower(concat('%', :q, '%'))
               or lower(e.nombres) like lower(concat('%', :q, '%'))
               or lower(e.apellidos) like lower(concat('%', :q, '%'))
            order by e.apellidos, e.nombres
            """)
    List<Estudiante> buscar(@Param("q") String q);
}
