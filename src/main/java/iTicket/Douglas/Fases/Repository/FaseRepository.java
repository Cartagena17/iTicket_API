package iTicket.Douglas.Fases.Repository;

import iTicket.Douglas.Fases.Entity.FaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaseRepository extends JpaRepository<FaseEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener fases por nombre fase
    Optional<FaseEntity> findByNombreFase(String nombreFase);
}
