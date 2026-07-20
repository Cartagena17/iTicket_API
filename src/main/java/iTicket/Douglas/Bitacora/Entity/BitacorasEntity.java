package iTicket.Douglas.Bitacora.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@Table (name = "BITACORAS")
public class BitacorasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BITACORAS")
    @SequenceGenerator(name = "SEQ_BITACORAS", sequenceName = "SEQ_BITACORAS", allocationSize = 1)
    @Column (name = "ID_BITACORA")
    private Long idBitacora;

    @Column (name = "FECHA_HORA")
    private LocalDateTime fechaHora;

    @Column (name = "NUEVO_ESTADO")
    private String nuevoEstado;

    //Quitar el comentario cuando se unan las demás partes
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "NOMBRE_USUARIO")
//    private UsuariosEntity usuario;
//
//    @ManyToOne (fetch = FetchType.LAZY)
//    @JoinColumn (name = "ID_TICKET", referencedColumnName = "ID_TICKET")
//    private TicketsEntity ticket;
}
