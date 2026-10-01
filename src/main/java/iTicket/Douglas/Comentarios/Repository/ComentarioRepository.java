package iTicket.Douglas.Comentarios.Repository;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
    Optional<ComentarioEntity> findByComentario(String comentario);

    //Metodo para obtener los comentarios de un ticket, ordenados del mas antiguo al mas reciente
    List<ComentarioEntity> findByTicket_IdTicketOrderByFechaHoraAsc(Long idTicket);

    //Verifica si otro usuario comento en el ticket despues de una fecha (se usa para no eliminar un comentario ya respondido)
    boolean existsByTicket_IdTicketAndFechaHoraAfterAndUsuario_IdUsuarioNot(Long idTicket, LocalDateTime fechaHora, Long idUsuario);
}
