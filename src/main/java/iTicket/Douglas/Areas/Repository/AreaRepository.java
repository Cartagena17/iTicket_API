package iTicket.Douglas.Areas.Repository;

import iTicket.Douglas.Areas.Entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<AreaEntity,Long> {
    boolean existsByNombreAreaIgnoreCase(String nombreArea);
    boolean existsByNombreAreaIgnoreCaseAndIdAreaNot(String nombreArea, Long idArea);
}
