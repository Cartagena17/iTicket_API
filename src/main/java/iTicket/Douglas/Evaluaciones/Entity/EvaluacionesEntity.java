package iTicket.Douglas.Evaluaciones.Entity;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table( name = "EVALUACIONES" )
public class EvaluacionesEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "SEQ_EVALUACIONES")
    @SequenceGenerator(name = "SEQ_EVALUACIONES", sequenceName = "SEQ_EVALUACIONES", allocationSize = 1)

    @Column(name = "ID_EVALUACION")
    private Long idEvaluacion;

    @Column(name = "CALIFICACION")
    private Double calificacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TICKET", referencedColumnName = "ID_TICKET")
    private TicketEntity ticket;
}
