package iTicket.Douglas.Notificaciones.Listener;

import iTicket.Douglas.Notificaciones.Event.*;
import iTicket.Douglas.Notificaciones.Service.NotificacionService;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

//Escucha los eventos de dominio y crea las notificaciones correspondientes.
//Se ejecuta AFTER_COMMIT, quiere decir que si la transacción que originó el evento falla y hace rollback, nunca se crea la notificación
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionListener {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarTicketCreado(TicketCreadoEvent evento) {
        List<UsuarioEntity> admins = usuarioRepo.findByRol_NombreRolAndDepartamento_IdDepartamento("Administrador", evento.idDepartamento());
        admins.forEach(admin -> notificar(
                admin.getIdUsuario(), "TICKET_CREADO", "Nuevo ticket",
                "Se creó un nuevo ticket en tu departamento.",
                "Ticket", evento.idTicket()
        ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarTicketAsignado(TicketAsignadoEvent evento) {
        notificar(evento.idTecnicoAsignado(), "TICKET_ASIGNADO", "Ticket asignado",
                "Se te asignó un nuevo ticket.", "Ticket", evento.idTicket());

        notificar(evento.idCreador(), "TICKET_ASIGNADO", "Tu ticket fue asignado",
                "Tu ticket fue asignado a un técnico.", "Ticket", evento.idTicket());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarTicketResuelto(TicketResueltoEvent evento) {
        notificar(evento.idCreador(), "TICKET_RESUELTO", "Ticket resuelto",
                "Tu ticket fue marcado como resuelto, ya puedes evaluarlo.", "Ticket", evento.idTicket());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarTicketEliminado(TicketEliminadoEvent evento) {
        boolean loEliminoElCreador = evento.idUsuarioAccion().equals(evento.idCreador());

        if (loEliminoElCreador) {
            //El creador eliminó su propio ticket, asi que, se avisa al admin del departamento
            List<UsuarioEntity> admins = usuarioRepo.findByRol_NombreRolAndDepartamento_IdDepartamento("Administrador", evento.idDepartamento());
            admins.forEach(admin -> notificar(
                    admin.getIdUsuario(), "TICKET_ELIMINADO", "Ticket eliminado",
                    "El creador eliminó un ticket de tu departamento.",
                    "Ticket", evento.idTicket()
            ));
        } else {
            //Un admin eliminó el ticket, asi que se avisa al creador
            notificar(evento.idCreador(), "TICKET_ELIMINADO", "Tu ticket fue eliminado",
                    "Un administrador eliminó tu ticket.", "Ticket", evento.idTicket());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarTicketVencido(TicketVencidoEvent evento) {
        List<UsuarioEntity> admins = usuarioRepo.findByRol_NombreRolAndDepartamento_IdDepartamento("Administrador", evento.idDepartamento());
        admins.forEach(admin -> notificar(
                admin.getIdUsuario(), "TICKET_VENCIDO", "Ticket vencido",
                "Un ticket de tu departamento venció sin ser resuelto.",
                "Ticket", evento.idTicket()
        ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarProyectoCreado(ProyectoCreadoEvent evento) {
        notificar(evento.idCoordinador(), "PROYECTO_CREADO", "Nuevo proyecto",
                "Se te asignó como coordinador de un nuevo proyecto.", "Proyecto", evento.idProyecto());

        notificar(evento.idSupervisor(), "PROYECTO_CREADO", "Nuevo proyecto",
                "Se te asignó como supervisor de un nuevo proyecto.", "Proyecto", evento.idProyecto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarFaseCreada(FaseCreadaEvent evento) {
        // No existe una pagina de detalle de fase, por eso la notificacion apunta al proyecto (vistaProyecto.html)
        evento.idsUsuariosDepartamento().forEach(idUsuario -> notificar(
                idUsuario, "FASE_CREADA", "Nueva fase",
                "Se creó una nueva fase para tu departamento.",
                "Proyecto", evento.idProyecto()
        ));
    }

    private void notificar(Long idUsuarioDestino, String tipo, String titulo, String mensaje, String tipoEntidad, Long idEntidad) {
        try {
            notificacionService.crearNotificacion(idUsuarioDestino, tipo, titulo, mensaje, tipoEntidad, idEntidad);
        } catch (Exception e) {
            //Una notificación fallida no debe hacer que falle el flujo principal
            log.error("No se pudo crear la notificación tipo {} para usuario {}: {}", tipo, idUsuarioDestino, e.getMessage());
        }
    }
}