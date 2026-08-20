package iTicket.Douglas.Estadisticas;

import iTicket.Douglas.Estadisticas.Service.EstadisticasService;
import iTicket.Douglas.Response.MetricasResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class TestQueryRunner implements CommandLineRunner {

    @Autowired
    private EstadisticasService service;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("TESTING ESTADISTICAS CON NULL DATES");
        try {
            MetricasResponseDTO dto = service.obtenerMetricas(null, null, PageRequest.of(0, 5), PageRequest.of(0, 5));
            System.out.println("Alertas Count: " + dto.getAlertas().getTotalElements());
            System.out.println("Equipos Count: " + dto.getEquiposMasReportados().getTotalElements());
            System.out.println("Total Tickets: " + dto.getTickets().getTotalTickets());
            System.out.println("CSAT Avg: " + dto.getEvaluaciones().getPromedioCsat());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("==================================================");
    }
}
