package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class EstadisticasEvaluacionesDTO {
    private long totalEvaluaciones;
    private double promedioCsat;
    private long estrellas5;
    private long estrellas4;
    private long estrellas3;
    private long estrellas1y2;
}
