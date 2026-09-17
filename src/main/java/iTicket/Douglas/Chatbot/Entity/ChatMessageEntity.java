package iTicket.Douglas.Chatbot.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CHATBOT_MENSAJES") // La tabla
public class ChatMessageEntity {

    /**
     * Esta clase es basicamente para asignar los mensajes al chat que pertenecen o
     * simplemente la tabla chatbot_mensajes
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chatbot_mensajes")
    @SequenceGenerator(
            name = "seq_chatbot_mensajes",
            sequenceName = "SEQ_CHATBOT_MENSAJES",
            allocationSize = 1
    )
    @Column(name = "ID_MENSAJE")
    private Long idMensaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVERSACION", nullable = false)
    private ChatConversationEntity conversacion;

    @Column(name = "ROL", nullable = false, length = 16)
    private String rol;

    @Lob
    @Column(name = "CONTENIDO", nullable = false)
    private String contenido;

    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;
}
