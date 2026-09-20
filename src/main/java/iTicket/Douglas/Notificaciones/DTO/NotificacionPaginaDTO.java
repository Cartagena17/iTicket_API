package iTicket.Douglas.Notificaciones.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionPaginaDTO {

    private List<NotificacionDTO> notificaciones;
    private long totalElementos;
    private int totalPaginas;
    private int paginaActual;
}
