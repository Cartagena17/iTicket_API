package iTicket.Douglas.Prioridades.Repository;

import iTicket.Douglas.Prioridades.Entity.PrioridadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioridadRepository extends JpaRepository<PrioridadEntity, Long> {

}
