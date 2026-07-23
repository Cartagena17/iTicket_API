package iTicket.Douglas.DetalleGeneral.Entity;


import iTicket.Douglas.Tickets.Entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table (name = "DETALLE_GENERAL")
public class DetalleGEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DETALLE_GENERAL")
    @SequenceGenerator(name = "SEQ_DETALLE_GENERAL", sequenceName = "SEQ_DETALLE_GENERAL", allocationSize = 1)
    @Column (name = "ID_DETALLE_GENERAL")
    private Long idDetalleGeneral;
    @Column (name = "DESCRIPCION_UBICACION")
    private String descripcionUbicacion;
    //Quitar comentario cuando se unan las demas partes
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
    private TicketEntity ticket;
}
