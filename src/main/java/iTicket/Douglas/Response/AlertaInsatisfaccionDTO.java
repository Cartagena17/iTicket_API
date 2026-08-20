package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class AlertaInsatisfaccionDTO {
    private String codigoTicket;
    private String asunto;
    private String usuario;
    private String tecnico;
    private Double calificacion;
    private String comentario;
    private LocalDateTime fechaEvaluacion;
}
