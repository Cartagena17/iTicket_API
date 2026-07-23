package iTicket.Douglas.Ubicaciones.Repository;

import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<UbicacionEntity, Long> {

    Optional<UbicacionEntity> findByNombreUbicacion(String nombreUbicacion);
}
