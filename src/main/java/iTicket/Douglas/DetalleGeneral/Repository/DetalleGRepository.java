package iTicket.Douglas.DetalleGeneral.Repository;

import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetalleGRepository extends JpaRepository<DetalleGEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener detalle general por id de ticket, quitar comentario cuando se unan las demas partes
    Optional<DetalleGEntity> findByTicket_IdTicket(Long idTicket);

    Optional<DetalleGEntity> findByTicket(TicketEntity ticket);
}
