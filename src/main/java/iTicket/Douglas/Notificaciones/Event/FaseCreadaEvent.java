package iTicket.Douglas.Notificaciones.Event;

import java.util.List;

public record FaseCreadaEvent(Long idFase, Long idProyecto, List<Long> idsUsuariosDepartamento) {

}