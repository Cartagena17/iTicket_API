package iTicket.Douglas.DetalleTA.Entity;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Detalle_TA")
public class DetalleTAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_detalle_ta")
    @SequenceGenerator(name = "Seq_detalle_ta", sequenceName = "Seq_detalle_ta", allocationSize = 1)
    @Column(name = "id_detalle_TA")
    private Long idDetalleTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ticket", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo", nullable = false)
    private ArticuloEntity articulo;
}
