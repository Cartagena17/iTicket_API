package iTicket.Douglas.Notificaciones.Event;

public record TicketEliminadoEvent(Long idTicket, Long idDepartamento, Long idCreador, Long idUsuarioAccion) {

}