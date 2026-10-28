package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Seccion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

    List<Seccion> findAllByOrderByGrado_NombreGradoAscNombreSeccionAsc();

    List<Seccion> findByTutor_IdUsuarioOrderByGrado_NombreGradoAscNombreSeccionAsc(Integer idTutor);

    boolean existsByGrado_IdGradoAndNombreSeccion(Integer idGrado, String nombreSeccion);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seccion s where s.idSeccion = :id")
    Optional<Seccion> findByIdForUpdate(@Param("id") Integer id);
}
