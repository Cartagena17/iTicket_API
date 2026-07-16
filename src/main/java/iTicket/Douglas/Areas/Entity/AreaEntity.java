package iTicket.Douglas.Areas.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table (name = "Areas")
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_areas")
    @SequenceGenerator(name = "Seq_areas",sequenceName = "Seq_areas", allocationSize = 1)
    @Column(name = "id_area")
    private Long idArea;
    @Column(name = "nombre_area")
    private String nombreArea;
}
