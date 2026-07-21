package iTicket.Douglas.Roles.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Roles")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_roles")
    @SequenceGenerator(name = "Seq_roles",sequenceName = "Seq_roles", allocationSize = 1)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol")
    private String nombreRol;
}
