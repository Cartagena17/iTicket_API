package iTicket.Douglas.Ubicaciones.Repository;

import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionesRepository extends JpaRepository<UbicacionesEntity, Long> {

    Optional<UbicacionesEntity> findByNombreUbicacion(String nombreUbicacion);
}
