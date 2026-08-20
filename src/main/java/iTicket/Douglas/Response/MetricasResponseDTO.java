package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter @Setter @ToString
public class MetricasResponseDTO {
    private EstadisticasTicketDTO tickets;
    private EstadisticasEvaluacionesDTO evaluaciones;
    private PaginatedResponseDTO<AlertaInsatisfaccionDTO> alertas;
    private PaginatedResponseDTO<EquipoReportadoDTO> equiposMasReportados;
    private List<TicketPrioridadDTO> ticketsPorPrioridad;
    private List<TicketMesDTO> ticketsPorMes;
    private List<SatisfaccionTecnicoDTO> satisfaccionPorTecnico;
}
