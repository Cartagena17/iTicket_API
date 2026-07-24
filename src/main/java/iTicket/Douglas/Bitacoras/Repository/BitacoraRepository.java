package iTicket.Douglas.Bitacoras.Repository;

import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BitacoraRepository extends JpaRepository<BitacoraEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener bitacora por id de ticket
    Optional<BitacoraEntity> findByIdTicket (TicketEntity Idticket);
}
