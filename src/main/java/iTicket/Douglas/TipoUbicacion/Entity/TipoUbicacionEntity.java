package iTicket.Douglas.TipoUbicacion.Entity;

import iTicket.Douglas.Ubicaciones.Entity.UbicacionesEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "Tipo_ubicacion")
public class TipoUbicacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_tipo_ubicacion")
    @SequenceGenerator(name = "Seq_tipo_ubicacion",sequenceName = "Seq_tipo_ubicacion", allocationSize = 1)
    @Column(name = "id_tipo_ubicacion")
    private Long id;
    @Column (name = "nombre_tipo_ubicacion")
    private String nombreTipoUbicacion;
    @OneToMany(mappedBy = "tipoUbicacion")
    private List<UbicacionesEntity> ubicaciones;
}
