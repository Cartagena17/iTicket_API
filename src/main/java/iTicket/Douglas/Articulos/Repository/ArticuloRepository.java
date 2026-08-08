package iTicket.Douglas.Articulos.Repository;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, Long> {

    Optional<ArticuloEntity> findByCodigoArticulo(String codigoArticulo);

    List<ArticuloEntity> findByCodigoArticuloContainingIgnoreCase(String codigo, Pageable pageable);
}
