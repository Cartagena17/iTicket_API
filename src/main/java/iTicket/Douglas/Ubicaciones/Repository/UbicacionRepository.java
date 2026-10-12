package iTicket.Douglas.Ubicaciones.Repository;

import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<UbicacionEntity, Long> {

    Optional<UbicacionEntity> findByNombreUbicacion(String nombreUbicacion);
    boolean existsByNombreUbicacionIgnoreCase(String nombreUbicacion);
    boolean existsByNombreUbicacionIgnoreCaseAndIdNot(String nombreUbicacion, Long id);
    List<UbicacionEntity> findByNombreUbicacionContainingIgnoreCase(String fragmento, Pageable pageable);
    @Query("""
        SELECT u FROM UbicacionEntity u
        WHERE UPPER(u.nombreUbicacion) LIKE UPPER(CONCAT(:fragmento, '%'))
        OR UPPER(u.tipoUbicacion.nombreTipoUbicacion) LIKE UPPER(CONCAT('%', :fragmento, '%'))
        """)
    List<UbicacionEntity> buscarPorNombreOTipo(@Param("fragmento") String fragmento, Pageable pageable);
}
