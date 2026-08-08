package iTicket.Douglas.DetalleTS.Repository;

import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleTSRepository extends JpaRepository<DetalleTSEntity, Long> {
    //Metodos personalizados

    //Metodo personalizado para obtener detalles de ticket por id de ticket
    List<DetalleTSEntity> findByTicket_IdTicket(Long idTicket);

    @Modifying
    @Query("DELETE FROM DetalleTSEntity d WHERE d.ticket = :ticket")
    void deleteByTicket(@Param("ticket") TicketEntity ticket);
}
