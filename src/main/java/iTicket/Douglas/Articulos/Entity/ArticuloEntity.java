package iTicket.Douglas.Articulos.Entity;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "Articulos", indexes = {
        @Index(name = "idx_articulos_categoria", columnList = "id_categoria"),
        @Index(name = "idx_articulos_modelo", columnList = "id_modelo"),
        @Index(name = "idx_articulos_ubicacion", columnList = "id_ubicacion")
})
public class ArticuloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_articulos")
    @SequenceGenerator(name = "Seq_articulos", sequenceName = "Seq_articulos", allocationSize = 1)
    @Column(name = "id_articulo")
    private Long idArticulo;

    @Column(name = "codigo_articulo")
    private String codigoArticulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modelo")
    private ModeloEntity modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion",  nullable = false)
    private UbicacionEntity ubicacion;

    @OneToMany(mappedBy = "articulo", fetch = FetchType.LAZY)
    private List<DetalleTAEntity> detallesArticulo;
}
