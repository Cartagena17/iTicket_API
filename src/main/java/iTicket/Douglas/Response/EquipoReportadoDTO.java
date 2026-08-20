package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class EquipoReportadoDTO {
    private String codigoArticulo;
    private String modelo;
    private long cantidadReportes;
}
