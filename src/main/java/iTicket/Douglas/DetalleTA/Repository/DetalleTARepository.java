package iTicket.Douglas.DetalleTA.Repository;

import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleTARepository extends JpaRepository<DetalleTAEntity, Long> {
    //Metodo para buscar detalle por ticket
    List<DetalleTAEntity> findByTicket_IdTicket(Long idTicket);

    @Modifying
    @Query("DELETE FROM DetalleTAEntity d WHERE d.ticket = :ticket")
    void deleteByTicket(@Param("ticket") TicketEntity ticket);
}
