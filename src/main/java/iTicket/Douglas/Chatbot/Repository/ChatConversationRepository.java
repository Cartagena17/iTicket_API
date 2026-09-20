package iTicket.Douglas.Chatbot.Repository;

import iTicket.Douglas.Chatbot.Entity.ChatConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversationEntity, Long> {

    // Aqui se busca el idUsuario
    List<ChatConversationEntity> findByUsuario_IdUsuarioOrderByFechaActualizacionDesc(
            Long idUsuario
    );

    // Aqui se enceuentran las conversaciones por usuario
    Optional<ChatConversationEntity> findByIdConversacionAndUsuario_IdUsuario(
            Long idConversacion,
            Long idUsuario
    );

    // Este es el total de conversaciones que tiene el usuario
    long countByUsuario_IdUsuario(Long idUsuario);

    // El orden depende de la fecha de creación
    Optional<ChatConversationEntity> findFirstByUsuario_IdUsuarioOrderByFechaCreacionAsc(
            Long idUsuario
    );
}
