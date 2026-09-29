package iTicket.Douglas.Categoria.Entity;


import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table (name = "CATEGORIAS")
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CATEGORIAS")
    @SequenceGenerator(name = "SEQ_CATEGORIAS", sequenceName = "SEQ_CATEGORIAS", allocationSize = 1)
    @Column (name = "ID_CATEGORIA")
    private Long idCategoria;
    @Column (name = "NOMBRE_CATEGORIA")
    private String nombreCategoria;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<ArticuloEntity> articulos;
}
