package iTicket.Douglas.DetalleFases.Repository;

import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetalleFRepository extends JpaRepository<DetalleFEntity, Long> {

    //metodos personalizados

    //metodo para obtener Detalle de fase por id de fase
    Optional<DetalleFEntity> findByFase(FaseEntity idFase);
}
