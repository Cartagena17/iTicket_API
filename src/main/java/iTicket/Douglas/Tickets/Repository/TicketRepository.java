package iTicket.Douglas.Tickets.Repository;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    //Metodo personalizado para buscar tickets por medio del codigo
    Optional<TicketEntity> findByCodigo(String codigo);

    //Metodo personalizado para buscar tickets por medio del asunto, es una lista ya que pueden haber muchas coincidencias
    List<TicketEntity> findByAsuntoContainingIgnoreCase(String asunto);
    //Containing es como un LIKE
    //IgnoreCase ignora mayúsculas y minúscula

    //Metodo personalizado para buscar tickets por prioridad
    List<TicketEntity> findByPrioridad(String prioridad);

    //Indicadores del departamento del admin. Agrupa por TIPO, no por nombre
    @Query("SELECT t.estado, COUNT(t) FROM TicketEntity t WHERE t.departamento.tipoDepartamento = :tipoDepartamento GROUP BY t.estado")
    List<Object[]> contarTicketsPorEstadoYDepartamento(@Param("tipoDepartamento") String tipoDepartamento);

    @Query("SELECT t.estado, COUNT(t) FROM TicketEntity t WHERE t.creador.idUsuario = :idUsuario GROUP BY t.estado")
    List<Object[]> contarTicketsPorEstadoUsuario(@Param("idUsuario") Long idUsuario);

    //Metodo para obtener el siguiente valor de la secuencia del código
    @Query(value = "SELECT Seq_codigo_ticket_diario.NEXTVAL FROM dual", nativeQuery = true)
    Long obtenerSiguienteCodigo();

    //Tickets "Nuevos" pendientes de aprobacion para un tipo de departamento.
    Page<TicketEntity> findByEstadoAndDepartamento_TipoDepartamento(String estado, String tipoDepartamento, Pageable pageable);

    List<TicketEntity> findByCreador_IdUsuarioAndFechaCreacionBetween(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);

// MÉTODOS PARA EL DASHBOARD DE ESTADÍSTICAS

    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE " +
           "(:inicio IS NULL OR t.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR t.fechaCreacion <= :fin)")
    Long contarTicketsTotalesRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT t.estado, COUNT(t) FROM TicketEntity t WHERE " +
           "t.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR t.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR t.fechaCreacion <= :fin) " +
           "GROUP BY t.estado")
    List<Object[]> contarTicketsPorEstadoRangoFechas(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT t.prioridad, COUNT(t) FROM TicketEntity t WHERE " +
           "t.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR t.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR t.fechaCreacion <= :fin) " +
           "GROUP BY t.prioridad")
    List<Object[]> contarTicketsPorPrioridadRangoFechas(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE " +
           "t.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "t.fechaVencimiento < CURRENT_TIMESTAMP AND LOWER(t.estado) NOT IN ('resuelto', 'cerrado', 'cancelado') " +
           "AND (:inicio IS NULL OR t.fechaCreacion >= :inicio) " +
           "AND (:fin IS NULL OR t.fechaCreacion <= :fin)")
    Long contarTicketsVencidosRangoFechas(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // LEFT JOIN en modelo: un articulo sin modelo no debe desaparecer del ranking solo porque no tiene marca/modelo asignado.
    @Query(value = "SELECT a.codigoArticulo, m.nombreModelo, COUNT(d.ticket) " +
           "FROM iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity d " +
           "JOIN d.articulo a " +
           "LEFT JOIN a.modelo m " +
           "WHERE d.ticket.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR d.ticket.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR d.ticket.fechaCreacion <= :fin) " +
           "GROUP BY a.codigoArticulo, m.nombreModelo " +
           "ORDER BY COUNT(d.ticket) DESC",
           countQuery = "SELECT COUNT(DISTINCT d.articulo.idArticulo) FROM iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity d WHERE " +
           "d.ticket.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR d.ticket.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR d.ticket.fechaCreacion <= :fin)")
    Page<Object[]> obtenerArticulosMasReportadosRangoFechas(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, Pageable pageable);

    // Consulta paginada de verdad para el endpoint dedicado /api/estadisticas/equipos-reportados
    // (antes era una lista completa y el frontend paginaba en JS, mandando siempre el payload entero).
    // Trae: codigoArticulo, ubicacion, modelo + marca, categoria, cantidadTickets
    // Igual que arriba: LEFT JOIN en modelo/marca para no perder articulos sin modelo asignado.
    @Query(value = "SELECT a.codigoArticulo, " +
           "u.nombreUbicacion, " +
           "CASE WHEN m IS NULL THEN NULL ELSE CONCAT(COALESCE(ma.nombreMarca, ''), ' ', m.nombreModelo) END, " +
           "c.nombreCategoria, " +
           "COUNT(d.ticket) " +
           "FROM iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity d " +
           "JOIN d.articulo a " +
           "JOIN a.ubicacion u " +
           "JOIN a.categoria c " +
           "LEFT JOIN a.modelo m " +
           "LEFT JOIN m.marca ma " +
           "WHERE d.ticket.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR d.ticket.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR d.ticket.fechaCreacion <= :fin) " +
           "GROUP BY a.codigoArticulo, " +
           "u.nombreUbicacion, " +
           "m, ma.nombreMarca, m.nombreModelo, " +
           "c.nombreCategoria " +
           "ORDER BY COUNT(d.ticket) DESC",
           countQuery = "SELECT COUNT(DISTINCT d.articulo.idArticulo) FROM iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity d WHERE " +
           "d.ticket.departamento.tipoDepartamento = :tipoDepartamento AND " +
           "(:inicio IS NULL OR d.ticket.fechaCreacion >= :inicio) AND " +
           "(:fin IS NULL OR d.ticket.fechaCreacion <= :fin)")
    Page<Object[]> obtenerArticulosReportadosCompleto(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, Pageable pageable);

    //Tickets que ya vencieron y siguen en un estado activo
    List<TicketEntity> findByEstadoInAndFechaVencimientoBefore(List<String> estados, LocalDateTime fecha);

    //Version por departamento del resumen mensual, para el panel del dashboard admin
    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.departamento.tipoDepartamento = :tipoDepartamento AND " + "(:inicio IS NULL OR t.fechaCreacion >= :inicio) AND " + "(:fin IS NULL OR t.fechaCreacion <= :fin)")
    Long contarTicketsTotalesRangoFechasPorDepartamento(@Param("tipoDepartamento") String tipoDepartamento, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
