package iTicket.Douglas.Bitacoras.Entity;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table (name = "BITACORAS")
public class BitacoraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BITACORAS")
    @SequenceGenerator(name = "SEQ_BITACORAS", sequenceName = "SEQ_BITACORAS", allocationSize = 1)
    @Column (name = "ID_BITACORA")
    private Long idBitacora;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_USUARIO", referencedColumnName = "ID_USUARIO")
    private UsuarioEntity usuario;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
    private TicketEntity ticket;
    @Column (name = "NUEVO_ESTADO")
    private String nuevoEstado;
    @Column (name = "FECHA_HORA")
    private LocalDateTime fechaHora;
}
