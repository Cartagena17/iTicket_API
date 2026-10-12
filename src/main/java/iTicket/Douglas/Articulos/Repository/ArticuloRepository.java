package iTicket.Douglas.Articulos.Repository;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, Long>, JpaSpecificationExecutor<ArticuloEntity> {
    List<ArticuloEntity> findByUbicacion_IdAndCodigoArticuloContainingIgnoreCase(Long idUbicacion, String fragmento, Pageable pageable);
    Optional<ArticuloEntity> findByCodigoArticuloAndUbicacion_Id(String codigoArticulo, Long idUbicacion);
    boolean existsByCodigoArticuloIgnoreCaseAndUbicacion_Id(String codigoArticulo, Long idUbicacion);
    boolean existsByCodigoArticuloIgnoreCaseAndUbicacion_IdAndIdArticuloNot(String codigoArticulo, Long idUbicacion, Long idArticulo);
}
