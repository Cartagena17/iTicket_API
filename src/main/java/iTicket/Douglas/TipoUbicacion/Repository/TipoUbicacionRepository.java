package iTicket.Douglas.TipoUbicacion.Repository;

import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacionEntity, Long> {

    Optional<TipoUbicacionEntity> findByNombreTipoUbicacion(String nombre_tipo_ubicacion);
}
