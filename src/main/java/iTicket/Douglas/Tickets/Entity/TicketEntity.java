package iTicket.Douglas.Tickets.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "TICKETS")
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
    private LocalDate fechaVencimiento;

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

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ComentarioEntity> comentarios;
}
