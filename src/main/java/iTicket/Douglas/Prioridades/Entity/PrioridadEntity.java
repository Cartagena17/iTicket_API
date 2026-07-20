package iTicket.Douglas.Prioridades.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "PRIORIDADES")
public class PrioridadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_prioridades")
    @SequenceGenerator(name = "seq_prioridades", sequenceName = "SEQ_PRIORIDADES", allocationSize = 1)
    @Column(name = "ID_PRIORIDAD")
    private Long idPrioridad;
    @Column(name = "NOMBRE_PRIORIDAD")
    private String nombrePrioridad;
}
