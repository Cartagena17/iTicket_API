package iTicket.Douglas.Bitacoras.Repository;

import iTicket.Douglas.Bitacoras.Entity.BitacorasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacorasRepository  extends JpaRepository<BitacorasEntity, Long>{
}
