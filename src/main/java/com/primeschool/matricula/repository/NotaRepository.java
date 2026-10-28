package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    List<Nota> findByMatricula_IdMatriculaAndPeriodo_IdPeriodo(
            Integer idMatricula, Integer idPeriodo);

    Optional<Nota> findByMatricula_IdMatriculaAndCurso_IdCursoAndPeriodo_IdPeriodo(
            Integer idMatricula, Integer idCurso, Integer idPeriodo);

    List<Nota> findByMatricula_Seccion_IdSeccionAndPeriodo_IdPeriodo(
            Integer idSeccion, Integer idPeriodo);

    List<Nota> findByMatricula_IdMatriculaOrderByPeriodo_FechaInicioAscCurso_NombreCursoAsc(
            Integer idMatricula);

    @Query("""
            select avg(n.calificacion)
            from Nota n
            where n.matricula.idMatricula = :idMatricula
              and n.periodo.idPeriodo = :idPeriodo
            """)
    Double promedio(@Param("idMatricula") Integer idMatricula,
                    @Param("idPeriodo") Integer idPeriodo);
}
