package iTicket.Douglas.DetalleTS.Repository;

import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.atomic.LongAccumulator;

@Repository
public interface DetalleTSRepository extends JpaRepository<DetalleTSEntity, Long> {
    //Metodos personalizados

    //Metodo personalizado para obtener detalle de ticket por id de ticket, quitar comentario al unir las demas partes
//    Optional<DetalleTSEntity> findByDetalleTSIdTicket(TicketsEntity ticket);
}
