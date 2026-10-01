package iTicket.Douglas.Tickets.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "TICKETS", indexes = {
        @Index(name = "idx_tickets_creador_estado", columnList = "id_creador, estado"),
        @Index(name = "idx_tickets_tecnico_estado", columnList = "id_tecnico_asignado, estado"),
        @Index(name = "idx_tickets_departamento", columnList = "id_departamento"),
        @Index(name = "idx_tickets_fecha_creacion", columnList = "fecha_creacion"),
        @Index(name = "idx_tickets_fecha_vencimiento", columnList = "fecha_vencimiento")
})
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tickets")
    @SequenceGenerator(name = "seq_tickets", sequenceName = "SEQ_TICKETS", allocationSize = 1)
    @Column(name = "ID_TICKET")
    private Long idTicket;

    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "ASUNTO")
    private String asunto;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEPARTAMENTO", referencedColumnName = "ID_DEPARTAMENTO")
    private DepartamentoEntity departamento;

    @Column(name = "DESCRIPCION_FALLA")
    private String descripcionFalla;

    @Column(name = "DESCRIPCION_SOLUCION")
    private String descripcionSolucion;

    @Column(name = "FECHA_VENCIMIENTO")
    private LocalDateTime fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CREADOR", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity creador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TECNICO_ASIGNADO", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity tecnicoAsignado;

    @Column(name = "PRIORIDAD")
    private String prioridad;

    //Campos adicionales
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EvidenciaEntity> evidencias = new ArrayList<>();

    @Column(name = "TIPO_TICKET")
    private String tipoTicket;

    @Column(name = "ESTADO")
    private String estado;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<ComentarioEntity> comentarios;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<DetalleGEntity> detallesGenerales;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<DetalleTSEntity> detallesSoftware;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<DetalleTAEntity> detallesArticulo;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<EvaluacionesEntity> evaluaciones;
}
