package iTicket.Douglas.Evaluaciones.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
public class EvaluacionesDTO {

    private Long id;
    @NotNull(message = "La calificación es obligatoria")
    @DecimalMin(value = "0.5", message = "La calificación mínima es 0.5 estrellas")
    @DecimalMax(value = "5.0", message = "La calificación máxima es 5 estrellas")
    private Double calificacion;

    @NotBlank
    @Size(max= 200, message = "Longitud Invalida en el comentario de la evaluacion [maximo 200]")
    private  String comentario;

    @NotNull(message = "El ID del ticket es obligatorio")
    private Long idTicket;

    //Agrego los campos requeridos para la tabla
    private String codigoTicket;
    private String asuntoTicket;
    private String nombreTecnico;
    private LocalDateTime fechaEvaluacion;
    // la fecha viene directamente del ticket, el ticket solo se puede cerrar cuando
    // fue evaluado, tomando esta logica la fecha de cierre de ticket también sería la fehca de evaluacion
}
