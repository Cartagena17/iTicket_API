package iTicket.Douglas.Comentarios.Repository;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
    Optional<ComentarioEntity> findByComentario(String comentario);

    //Metodo para obtener los comentarios de un ticket, ordenados del mas antiguo al mas reciente
    List<ComentarioEntity> findByTicket_IdTicketOrderByFechaHoraAsc(Long idTicket);
}
