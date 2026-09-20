package iTicket.Douglas.Evaluaciones.DTO;

import jakarta.validation.constraints.*;
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
    @Size(max = 200, message = "Longitud Invalida en el comentario de la evaluacion [maximo 200]")
    private String comentario;

    @NotNull(message = "El ID del ticket es obligatorio")
    private Long idTicket;

    // Campos requeridos para la tabla de evaluaciones en el frontend
    private String codigoTicket;
    private String asuntoTicket;
    private String nombreTecnico;
    private LocalDateTime fechaEvaluacion;
    // La fecha viene directamente de la bitacora: un ticket solo se puede cerrar cuando ya fue evaluado,
    // así que la fecha de cierre del ticket es también la fecha de evaluación.
}