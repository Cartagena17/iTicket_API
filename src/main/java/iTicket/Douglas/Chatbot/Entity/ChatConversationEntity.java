package iTicket.Douglas.Chatbot.Entity;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CHATBOT_CONVERSACIONES") // La tabla en la bd
public class ChatConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chatbot_conversaciones")
    @SequenceGenerator(
            name = "seq_chatbot_conversaciones",
            sequenceName = "SEQ_CHATBOT_CONVERSACIONES",
            allocationSize = 1
    )
    @Column(name = "ID_CONVERSACION")
    private Long idConversacion; // id de la conversación autogenerado

    // Como muchas conversaciones pueden pertenecer a un solo usuario se usa el ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private UsuarioEntity usuario; // FK en la tabla

    @Column(name = "TITULO", nullable = false, length = 80)
    private String titulo; // Titulo de la conversación

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion; // Fecha de creación de la conversación

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    // Se usa OneToMany ya que una conversación tiene muchos mensajes
    @OneToMany(
            mappedBy = "conversacion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("fechaHora ASC, idMensaje ASC") // Estos mensajes se ordenan por la hora
    private List<ChatMessageEntity> mensajes = new ArrayList<>();
}
