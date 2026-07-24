package iTicket.Douglas.DetalleTS.Entity;


import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table (name = "Detalle_TS")
public class DetalleTSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DETALLE_TS")
    @SequenceGenerator(name = "SEQ_DETALLE_TS", sequenceName = "SEQ_DETALLE_TS", allocationSize = 1)
    @Column (name = "ID_DETALLE_TS")
    private Long idDetalleTS;

    @Column (name = "NOMBRE_SOFTWARE")
    private String nombreSoftware;

    @Column (name = "VERSION")
    private String version;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
    private TicketEntity ticket;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_UBICACION", referencedColumnName = "ID_UBICACION")
    private UbicacionEntity ubicacion;
}
