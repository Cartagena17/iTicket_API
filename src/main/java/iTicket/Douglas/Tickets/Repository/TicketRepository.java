package iTicket.Douglas.Tickets.Repository;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    //Los elementos que se manden al jpa se guardarán en TicketEntity
    //De tipo Long ya que la llave primaria de TicketEntity es de tipo Long

    //Metodo personalizado para buscar tickets por medio del codigo
    Optional<TicketEntity> findByCodigo(String codigo);

    //Metodo personalizado para buscar tickets por medio del asunto, es una lista ya que pueden haber muchas coincidencias
    List<TicketEntity> findByAsuntoContainingIgnoreCase(String asunto);
    //Containing es como un LIKE
    //IgnoreCase ignora mayúsculas y minúscula

    //Metodo personalizado para buscar tickets por prioridad
    List<TicketEntity> findByPrioridad(String prioridad);
}
