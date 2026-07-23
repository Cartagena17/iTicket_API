package iTicket.Douglas.DetalleTA.Repository;

import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleTARepository extends JpaRepository<DetalleTAEntity, Long> {
}
