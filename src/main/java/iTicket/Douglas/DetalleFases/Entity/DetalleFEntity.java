package iTicket.Douglas.DetalleFases.Entity;


import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Utils.BooleanToCharConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "DETALLE_FASES", indexes = {
        @Index(name = "idx_detalle_fases_fase", columnList = "id_fase")
})
public class DetalleFEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DETALLE_FASES")
    @SequenceGenerator(name = "SEQ_DETALLE_FASES", sequenceName = "SEQ_DETALLE_FASES", allocationSize = 1)
    private Long idDetalleFase;

    @Column (name = "DESCRIPCION_DETALLE")
    private String descripcionDetalle;

    @Column (name = "COMPLETADO")
    @Convert (converter = BooleanToCharConverter.class)
    private Boolean completado;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_FASE", referencedColumnName = "ID_FASE")
    private FaseEntity fase;
}
