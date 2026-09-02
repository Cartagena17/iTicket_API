package iTicket.Douglas.Tickets.Job;

import iTicket.Douglas.Tickets.Service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VencerTicketsJob {

    private final TicketService ticketService;

    //Corre cada 5 minutos
    @Scheduled(cron = "0 */5 * * * *")
    public void ejecutar() {
        ticketService.marcarVencidos();
    }
}