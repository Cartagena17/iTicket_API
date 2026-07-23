package iTicket.Douglas.Articulos.Repository;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, Long> {
}
