package iTicket.Douglas.Notificaciones.Event;

//idUsuarioDestino ya viene resuelto (el otro participante del ticket: creador <-> tecnico asignado)
public record ComentarioCreadoEvent(Long idTicket, Long idUsuarioDestino) {

}
