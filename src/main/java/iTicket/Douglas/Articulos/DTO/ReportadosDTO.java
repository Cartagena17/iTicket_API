package iTicket.Douglas.Articulos.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class    ReportadosDTO {
    private String codigoEquipo;
    private String ubicacion;
    private String modeloMarca;
    private String categoria;
    private Long numeroTickets;
    private String estadoGeneral;
}
