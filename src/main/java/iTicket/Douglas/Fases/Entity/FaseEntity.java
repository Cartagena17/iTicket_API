package iTicket.Douglas.Fases.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@Table (name = "FASES")
public class FaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FASES")
    @SequenceGenerator(name = "SEQ_FASES", sequenceName = "SEQ_FASES", allocationSize = 1)
    @Column (name = "ID_FASE")
    private Long idFase;

    @Column (name = "NOMBRE_FASE")
    private String nombreFase;

    @Column (name = "FASE_DESCRIPCION")
    private String faseDescripcion;

    @Column (name = "FECHA_INICIO_ESTIMADA")
    private LocalDate fechaInicioEstimada;

    @Column (name = "FECHA_INICIO_REAL")
    private LocalDate fechaInicioReal;

    @Column (name = "FECHA_FINAL_ESTIMADA")
    private LocalDate fechaFinalEstimada;

    @Column (name = "FECHA_FINAL_REAL")
    private LocalDate fechaFinalReal;

    @Column (name = "NOMBRE_PROVEEDOR")
    private String nombreProveedor;

    @Column (name = "PRESUPUESTO_ESTIMADO")
    private Double presupuestoEstimado;

    @Column (name = "GASTO_TOTAL")
    private Double gastoTotal;

    @Column (name = "FINALIZADO")
//    @Convert(converter = BooleanToCharConverter.class)
    private Boolean finalizado;

    @Column (name = "DEPARTAMENTO_ENCARGADO")
    private String departamentoEncargado;

//    @ManyToOne (fetch = FetchType.LAZY)
//    @JoinColumn (name = "ID_PROYECTO", referencedColumnName = "ID_PROYECTO")
//    private ProyectoEntity proyecto;
}
