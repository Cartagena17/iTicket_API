package iTicket.Douglas.Chatbot.Repository;

import iTicket.Douglas.Chatbot.Entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // Trae todos los mensajes de una conversación ordenado por la fecha y por el id
    List<ChatMessageEntity> findByConversacion_IdConversacionOrderByFechaHoraAscIdMensajeAsc(
            Long idConversacion
    );
}
