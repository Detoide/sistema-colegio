package com.primeschool.matricula.repository;

import com.primeschool.matricula.model.entity.Seccion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a las secciones y sus vacantes.
 */
public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

    /**
     * Lista las secciones ordenadas por grado y nombre.
     *
     * @return secciones ordenadas alfabéticamente
     */
    List<Seccion> findAllByOrderByGrado_NombreGradoAscNombreSeccionAsc();

    /**
     * Lista las secciones asignadas a un docente tutor.
     *
     * @param idTutor identificador del usuario tutor
     * @return secciones del tutor
     */
    List<Seccion> findByTutor_IdUsuarioOrderByGrado_NombreGradoAscNombreSeccionAsc(Integer idTutor);

    /**
     * Comprueba si existe una sección con el grado y nombre indicados.
     *
     * @param idGrado identificador del grado
     * @param nombreSeccion nombre de la sección
     * @return {@code true} si la sección ya está registrada
     */
    boolean existsByGrado_IdGradoAndNombreSeccion(Integer idGrado, String nombreSeccion);

    /**
     * Obtiene una sección bloqueada para actualizar sus vacantes de forma segura.
     *
     * @param id identificador de la sección
     * @return sección bloqueada, si existe
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seccion s where s.idSeccion = :id")
    Optional<Seccion> findByIdForUpdate(@Param("id") Integer id);
}
