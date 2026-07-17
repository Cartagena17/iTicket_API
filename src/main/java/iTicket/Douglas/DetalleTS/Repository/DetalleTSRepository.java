package iTicket.Douglas.DetalleTS.Repository;

import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.LongAccumulator;

@Repository
public interface DetalleTSRepository extends JpaRepository<DetalleTSEntity, Long> {
}
