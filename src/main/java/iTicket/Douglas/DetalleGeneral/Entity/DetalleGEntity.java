package iTicket.Douglas.DetalleGeneral.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table (name = "DETALLE_TNA")
public class DetalleGEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEC_DETALLE_GENERAL")
    @SequenceGenerator(name = "SEC_DETALLE_GENERAL", sequenceName = "SEC_DETALLE_GENERAL", allocationSize = 1)
    @Column (name = "ID_DETALLE_GENERAL")
    private Long idDetalleGeneral;
    @Column (name = "DESCRIPCION_UBICACION")
    private String descripcionUbicacion;
    //Quitar comentario cuando se unan las demas partes
//    @ManyToOne (fetch = FetchType.LAZY)
//    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
//    private TicketsEntity ticket;
}
