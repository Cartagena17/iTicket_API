package iTicket.Douglas.Evaluaciones.Repository;

import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EvaluacionesRepository extends JpaRepository<EvaluacionesEntity, Long> {

    Optional<EvaluacionesEntity> findByTicket_IdTicket(Long idTicket);

    @Query("SELECT e FROM EvaluacionesEntity e WHERE " +
            "(:busqueda IS NULL OR :busqueda = '' OR " +
            " UPPER(e.ticket.codigo) LIKE UPPER(CONCAT('%', :busqueda, '%')) OR " +
            " UPPER(e.ticket.asunto) LIKE UPPER(CONCAT('%', :busqueda, '%'))) AND " +
            "(:calificacion IS NULL OR e.calificacion = :calificacion) AND " +
            "(:fechaInicio IS NULL OR (e.ticket.fechaCreacion >= :fechaInicio AND e.ticket.fechaCreacion <= :fechaFin))")
    Page<EvaluacionesEntity> buscarPorFiltrosPaginado(
            @Param("busqueda") String busqueda,
            @Param("calificacion") Double calificacion,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    @Query("SELECT COUNT(e), COALESCE(AVG(e.calificacion), 0.0), " +
            "SUM(CASE WHEN e.calificacion >= 4 THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN e.calificacion <= 2 THEN 1L ELSE 0L END) " +
            "FROM EvaluacionesEntity e WHERE " +
            "(:busqueda IS NULL OR :busqueda = '' OR " +
            " UPPER(e.ticket.codigo) LIKE UPPER(CONCAT('%', :busqueda, '%')) OR " +
            " UPPER(e.ticket.asunto) LIKE UPPER(CONCAT('%', :busqueda, '%'))) AND " +
            "(:calificacion IS NULL OR e.calificacion = :calificacion) AND " +
            "(:fechaInicio IS NULL OR (e.ticket.fechaCreacion >= :fechaInicio AND e.ticket.fechaCreacion <= :fechaFin))")
    Object[] obtenerMetricasRaw(
            @Param("busqueda") String busqueda,
            @Param("calificacion") Double calificacion,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT COUNT(e) FROM EvaluacionesEntity e")
    long countEvaluaciones();

    @Query("SELECT COUNT(e), COALESCE(AVG(e.calificacion), 0.0), " +
            "SUM(CASE WHEN e.calificacion = 5 THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN e.calificacion = 4 THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN e.calificacion = 3 THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN e.calificacion <= 2 THEN 1L ELSE 0L END) " +
            "FROM EvaluacionesEntity e WHERE " +
            "(:inicio IS NULL OR e.ticket.fechaCreacion >= :inicio) AND " +
            "(:fin IS NULL OR e.ticket.fechaCreacion <= :fin)")
    Object[] obtenerMetricasDashboard(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query(value = "SELECT e FROM EvaluacionesEntity e WHERE e.calificacion <= 2 AND " +
            "(:inicio IS NULL OR e.ticket.fechaCreacion >= :inicio) AND " +
            "(:fin IS NULL OR e.ticket.fechaCreacion <= :fin) " +
            "ORDER BY e.ticket.fechaCreacion DESC",
            countQuery = "SELECT COUNT(e) FROM EvaluacionesEntity e WHERE e.calificacion <= 2 AND " +
                    "(:inicio IS NULL OR e.ticket.fechaCreacion >= :inicio) AND " +
                    "(:fin IS NULL OR e.ticket.fechaCreacion <= :fin)")
    Page<EvaluacionesEntity> obtenerAlertasInsatisfaccion(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, Pageable pageable);

    @Query("SELECT e.ticket.tecnicoAsignado.nombreUsuario, AVG(e.calificacion) " +
            "FROM EvaluacionesEntity e " +
            "WHERE e.ticket.tecnicoAsignado IS NOT NULL " +
            "AND (:inicio IS NULL OR e.ticket.fechaCreacion >= :inicio) " +
            "AND (:fin IS NULL OR e.ticket.fechaCreacion <= :fin) " +
            "GROUP BY e.ticket.tecnicoAsignado.nombreUsuario " +
            "ORDER BY AVG(e.calificacion) DESC")
    java.util.List<Object[]> obtenerPromedioSatisfaccionPorTecnico(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Consulta dedicada para el endpoint /api/evaluaciones/alertas
    // JOIN FETCH para pre-cargar ticket, creador y técnico y evitar LazyInitializationException
    @Query("SELECT e FROM EvaluacionesEntity e " +
            "JOIN FETCH e.ticket t " +
            "LEFT JOIN FETCH t.creador " +
            "LEFT JOIN FETCH t.tecnicoAsignado " +
            "WHERE e.calificacion <= :umbral " +
            "AND (:inicio IS NULL OR t.fechaCreacion >= :inicio) " +
            "AND (:fin IS NULL OR t.fechaCreacion <= :fin) " +
            "ORDER BY t.fechaCreacion DESC")
    java.util.List<EvaluacionesEntity> obtenerAlertasConDetalle(
            @Param("umbral") Integer umbral,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}