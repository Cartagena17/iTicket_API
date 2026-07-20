package iTicket.Douglas.Bitacora.Repository;

import iTicket.Douglas.Bitacora.Entity.BitacorasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacorasRepository  extends JpaRepository<BitacorasEntity, Long>{

    //Metodos personalizados

    //Metodo para obtener bitacora por asunto de ticket, quitar comentario cuando se unan las demas partes
//    Optional<BitacorasEntity> findByIdTicket(TicketsEntity ticket);
}
