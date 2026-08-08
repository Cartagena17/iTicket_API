package iTicket.Douglas.Tickets.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickteIndicadoresEstadoDTO {

    private long resueltos;
    private long asignados;
    private long enProgreso;
    private long enEspera;
    private long nuevos;
    private long cerrados;
    private long vencidos;
}
