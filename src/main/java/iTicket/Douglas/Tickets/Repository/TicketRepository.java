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

    @Query("SELECT t.estado, COUNT(t) FROM TicketEntity t WHERE LOWER(t.departamento.nombreDepartamento) = LOWER(:nombreDepartamento) GROUP BY t.estado")
    List<Object[]> contarTicketsPorEstadoYDepartamento(@Param("nombreDepartamento") String nombreDepartamento);

    @Query("SELECT t.estado, COUNT(t) FROM TicketEntity t WHERE t.creador.idUsuario = :idUsuario GROUP BY t.estado")
    List<Object[]> contarTicketsPorEstadoUsuario(@Param("idUsuario") Long idUsuario);

    //Metodo para obtener el siguiente valor de la secuencia del código
    @Query(value = "SELECT Seq_codigo_ticket_diario.NEXTVAL FROM dual", nativeQuery = true)
    Long obtenerSiguienteCodigo();

    //Metodo para obtener un numero limitado de tickets "Nuevos" segun el departamento
    Page<TicketEntity> findByEstadoAndDepartamento_NombreDepartamentoIgnoreCase(String estado, String nombreDepartamento, Pageable pageable);

    List<TicketEntity> findByCreador_IdUsuarioAndFechaCreacionBetween(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
}
