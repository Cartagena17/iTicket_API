package iTicket.Douglas.Tickets.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPaginaDTO {

    private List<TicketDTO> tickets;
    private long totalElementos;
    private int totalPaginas;
    private int paginaActual;
}
