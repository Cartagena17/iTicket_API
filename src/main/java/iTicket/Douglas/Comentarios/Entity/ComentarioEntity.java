package iTicket.Douglas.Comentarios.Entity;

import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Comentarios")
public class ComentarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_comentarios")
    @SequenceGenerator(
            name = "Seq_comentarios",
            sequenceName = "Seq_comentarios",
            allocationSize = 1
    )
    @Column(name = "id_comentario")
    private Long id;

    @Column(name = "comentario")
    private String comentario;

    @OneToMany(mappedBy = "comentario")
    private List<MultimediaComentarioEntity> multimediaComentarios;

    @Column(name = "fecha_hora", insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_ticket")
    private TicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "id_usuario_comentario")
    private UsuarioEntity usuario;
}
