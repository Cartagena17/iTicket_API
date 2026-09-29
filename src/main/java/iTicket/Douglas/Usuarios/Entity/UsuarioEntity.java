package iTicket.Douglas.Usuarios.Entity;

import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import iTicket.Douglas.Chatbot.Entity.ChatConversationEntity;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Notificaciones.Entity.NotificacionEntity;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Roles.Entity.RolEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Utils.BooleanToCharConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "Usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_usuarios")
    @SequenceGenerator(name = "Seq_usuarios", sequenceName = "Seq_usuarios", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    @Column(name = "correo")
    private String correo;
    @Column(name = "clave")
    private String clave;
    @Column(name = "imagen_url")
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private DepartamentoEntity departamento;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<ComentarioEntity> comentarios;

    @OneToMany(mappedBy = "creador", fetch = FetchType.LAZY)
    private List<TicketEntity> ticketsCreados;

    @OneToMany(mappedBy = "tecnicoAsignado", fetch = FetchType.LAZY)
    private List<TicketEntity> ticketsAsignados;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<BitacoraEntity> bitacoras;

    @OneToMany(mappedBy = "coordinador", fetch = FetchType.LAZY)
    private List<ProyectoEntity> proyectosCoordinados;

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    private List<ProyectoEntity> proyectosSupervisados;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<ChatConversationEntity> conversaciones;

    @OneToMany(mappedBy = "usuarioDestino", fetch = FetchType.LAZY)
    private List<NotificacionEntity> notificaciones;

    @Convert(converter = BooleanToCharConverter.class)//Se aplica el BooleanToCharConverter para traducir el boolean a String
    @Column(name = "ESTADO")
    private Boolean estado;

    @Column(name = "cloudinary_id")
    private String cloudinaryId;
}
