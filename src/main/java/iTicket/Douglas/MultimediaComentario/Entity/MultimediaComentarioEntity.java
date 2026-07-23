package iTicket.Douglas.MultimediaComentario.Entity;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MultimediaComentarios")
public class MultimediaComentarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_multimedia_comentarios")
    @SequenceGenerator(
            name = "Seq_multimedia_comentarios",
            sequenceName = "Seq_multimedia_comentarios",
            allocationSize = 1
    )
    @Column(name = "id_multimedia")
    private Long id;

    @Column(name = "multimedia_url")
    private String multimediaUrl;

    @ManyToOne
    @JoinColumn(name = "id_comentario")
    private ComentarioEntity comentario;
}
