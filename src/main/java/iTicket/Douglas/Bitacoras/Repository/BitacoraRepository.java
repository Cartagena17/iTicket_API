package iTicket.Douglas.Bitacoras.Repository;

import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BitacoraRepository extends JpaRepository<BitacoraEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener bitacora por id de ticket
    Optional<BitacoraEntity> findByTicket (Long ticket);
}
