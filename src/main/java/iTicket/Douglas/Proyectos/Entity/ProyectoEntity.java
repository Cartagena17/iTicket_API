package iTicket.Douglas.Proyectos.Entity;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Utils.BooleanToCharConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@Table(name = "PROYECTOS")
public class ProyectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_proyectos")
    @SequenceGenerator(name = "seq_proyectos", sequenceName = "SEQ_PROYECTOS", allocationSize = 1)
    @Column(name = "ID_PROYECTO")
    private Long idProyecto;

    @Column(name = "NOMBRE_PROYECTO")
    private String nombreProyecto;

    @Column(name = "TIPO_PROYECTO")
    private String tipoProyecto;

    @Column(name = "UBICACION")
    private String ubicacion;

    @Column(name = "DESCRIPCION_PROYECTO")
    private String descripcionProyecto;

    @Column(name = "PRESUPUESTO_ESTIMADO")
    private BigDecimal presupuestoEstimado;

    @Column(name = "GASTO_TOTAL")
    private BigDecimal gastoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COORDINADOR", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity coordinador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUPERVISOR", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity supervisor;

    @Convert(converter = BooleanToCharConverter.class)//Se aplica el BooleanToCharConverter para traducir el boolean a String
    @Column(name = "FINALIZADO")
    private Boolean finalizado;
}
