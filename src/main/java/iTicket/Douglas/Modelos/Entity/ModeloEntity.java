package iTicket.Douglas.Modelos.Entity;

import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Modelos")
public class ModeloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_modelos")
    @SequenceGenerator(name = "Seq_modelos", sequenceName = "Seq_modelos", allocationSize = 1)
    @Column(name = "id_modelo")
    private Long idModelo;

    @Column(name = "nombre_modelo")
    private String nombreModelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", nullable = false)
    private MarcaEntity marca;
}
