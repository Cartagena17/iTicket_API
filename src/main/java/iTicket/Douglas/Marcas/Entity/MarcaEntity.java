package iTicket.Douglas.Marcas.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Marcas")
public class MarcaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_marcas")
    @SequenceGenerator(name = "Seq_marcas",sequenceName = "Seq_marcas", allocationSize = 1)
    @Column(name = "id_marca")
    private Long idMarca;
    @Column(name = "nombre_marca")
    private String nombreMarca;
}
