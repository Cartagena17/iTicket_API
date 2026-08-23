package iTicket.Douglas.Departamentos.Entity;

import iTicket.Douglas.Areas.Entity.AreaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Departamentos")
public class DepartamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_departamentos")
    @SequenceGenerator(name = "Seq_departamentos", sequenceName = "Seq_departamentos", allocationSize = 1)
    @Column(name = "id_departamento")
    private Long idDepartamento;
    @Column(name = "nombre_departamento")
    private String nombreDepartamento;

    //Funcion del departamento: 'IT', 'Mantenimiento' u 'Otro'. 'Otro' no recibe tickets
    @Column(name = "tipo_departamento", nullable = false, length = 15)
    private String tipoDepartamento;

    @ManyToOne
    @JoinColumn(name = "id_area", nullable = false)
    private AreaEntity area;
}
