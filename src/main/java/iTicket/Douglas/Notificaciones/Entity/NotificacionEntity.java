package iTicket.Douglas.Notificaciones.Entity;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Utils.BooleanToCharConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "NOTIFICACIONES")
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFICACIONES")
    @SequenceGenerator(name = "SEQ_NOTIFICACIONES", sequenceName = "SEQ_NOTIFICACIONES", allocationSize = 1)
    @Column(name = "ID_NOTIFICACION")
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_DESTINO", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity usuarioDestino;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "MENSAJE")
    private String mensaje;

    @Column(name = "TIPO_ENTIDAD")
    private String tipoEntidad;

    @Column(name = "ID_ENTIDAD")
    private Long idEntidad;

    @Convert(converter = BooleanToCharConverter.class)
    @Column(name = "LEIDA")
    private Boolean leida = false;

    @CreationTimestamp
    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora;
}