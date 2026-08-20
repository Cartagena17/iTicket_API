package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AlertaInsatisfaccionDTO {
    private String codigoTicket;
    private String usuario;
    private String tecnico;
    private String comentario;
    private Double calificacion;
}
