package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class TicketMesDTO {
    private String mes;
    private int year;
    private long creados;
    private long resueltos;
    private long vencidos;
}
