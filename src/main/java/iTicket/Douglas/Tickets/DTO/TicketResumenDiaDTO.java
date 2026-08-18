package iTicket.Douglas.Tickets.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResumenDiaDTO {
    private String dia;
    private long cantidad;
}
