package iTicket.Douglas.DetalleTS.Entity;


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
    @Column (name = "UBICACION")
    private String ubicacion;
    //Quitar comentario cuando se unan las demas partes
//    @ManyToOne (fetch = FetchType.LAZY)
//    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
//    private TicketsEntity ticket;
}
