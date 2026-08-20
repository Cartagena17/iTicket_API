package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class EstadisticasTicketDTO {
    private long totalTickets;
    private long ticketsNuevos;
    private long ticketsEnProceso;
    private long ticketsResueltos;
    private long ticketsCancelados;
    private double tiempoMedioResolucionHoras;
}
