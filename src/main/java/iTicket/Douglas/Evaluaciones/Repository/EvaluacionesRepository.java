package iTicket.Douglas.Evaluaciones.Repository;

import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluacionesRepository extends JpaRepository<EvaluacionesEntity,Long>{
    Optional<EvaluacionesEntity> findByTicket_IdTicket(Long idTicket);
}
