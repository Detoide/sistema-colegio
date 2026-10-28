package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    List<Estudiante> findAllByOrderByApellidosAscNombresAsc();

    List<Estudiante> findByApoderado_Usuario_IdUsuarioOrderByApellidosAscNombresAsc(Integer idUsuario);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdEstudianteNot(String codigo, Integer idEstudiante);

    @Query("""
            select e from Estudiante e
            where lower(e.codigo) like lower(concat('%', :q, '%'))
               or lower(e.nombres) like lower(concat('%', :q, '%'))
               or lower(e.apellidos) like lower(concat('%', :q, '%'))
            order by e.apellidos, e.nombres
            """)
    List<Estudiante> buscar(@Param("q") String q);
}
