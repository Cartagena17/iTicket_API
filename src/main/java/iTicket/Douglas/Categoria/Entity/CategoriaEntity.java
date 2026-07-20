package iTicket.Douglas.Categoria.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
