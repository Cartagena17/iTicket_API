package iTicket.Douglas.Ubicaciones.Entity;

import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Ubicaciones")
public class UbicacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_ubicaciones")
    @SequenceGenerator(name = "Seq_ubicaciones",sequenceName = "Seq_ubicaciones", allocationSize = 1)
    @Column(name = "id_ubicacion")
    private Long id;
    @Column (name = "nombre_ubicacion")
    private String nombreUbicacion;
    @ManyToOne
    @JoinColumn(name = "id_tipo_ubicacion")
    private TipoUbicacionEntity tipoUbicacion;
}
