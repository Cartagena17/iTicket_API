package iTicket.Douglas.Modelos.Repository;

import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModeloRepository extends JpaRepository<ModeloEntity, Long> {
}
