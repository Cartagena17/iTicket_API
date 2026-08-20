package iTicket.Douglas.Marcas.Repository;

import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<MarcaEntity, Long> {
    boolean existsByNombreMarcaIgnoreCase(String nombreMarca);
}
