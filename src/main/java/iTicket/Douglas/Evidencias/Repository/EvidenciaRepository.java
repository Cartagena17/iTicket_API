package iTicket.Douglas.Evidencias.Repository;

import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<EvidenciaEntity, Long> {

    //Sirve para decir: SELECT * FROM evidencias WHERE id_ticket = ?
    List<EvidenciaEntity> findByTicket(TicketEntity ticket);
}
