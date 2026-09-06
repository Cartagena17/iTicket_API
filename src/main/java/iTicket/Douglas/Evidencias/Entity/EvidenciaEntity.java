package iTicket.Douglas.Evidencias.Entity;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "EVIDENCIAS")
public class EvidenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evidencias_seq_id")
    @SequenceGenerator(name = "evidencias_seq_id", sequenceName = "SEQ_EVIDENCIAS", allocationSize = 1)
    @Column(name = "ID_EVIDENCIA")
    private Long idEvidencia;

    @Column(name = "EVIDENCIA_URL")
    private String evidenciaUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TICKET", referencedColumnName = "ID_TICKET")
    private TicketEntity ticket;

    @Column(name = "CLOUDINARY_ID")
    private String cloudinaryId;
}
