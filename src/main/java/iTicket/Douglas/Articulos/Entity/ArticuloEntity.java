package iTicket.Douglas.Articulos.Entity;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Articulos")
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
}
