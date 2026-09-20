package iTicket.Douglas.Modelos.Repository;

import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloRepository extends JpaRepository<ModeloEntity, Long> {
    boolean existsByNombreModeloIgnoreCaseAndMarca_IdMarca(String nombreModelo, Long idMarca);

    boolean existsByNombreModeloIgnoreCaseAndMarca_IdMarcaAndIdModeloNot(
            String nombreModelo,
            Long idMarca,
            Long idModelo
    );
}
