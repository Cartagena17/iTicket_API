package iTicket.Douglas.Tickets.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDTO {

    private Long idTicket;
    @Size(max = 15, message = "Longitud inválida del código del ticket [15 caracteres]")
    private String codigo; //Se genera automáticamente

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 100, message = "Longitud inválida del asunto del ticket [100 caracteres]")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "Longitud inválida de la descripción del ticket [500 caracteres]")
    private String descripcion;

    @NotNull(message = "El departamento es obligatorio")
    @Positive
    private Long departamento;
    private String nombreDepartamento;//Este atributo solo se mostrará en los GET

    @Size(max = 500, message = "Longitud inválida de la descripción de la falla [500 caracteres]")
    private String descripcionFalla;

    @Size (max = 500, message = "Longitud inválida de la descripción de la solución [500 caracteres]")
    private String descripcionSolucion;

    @FutureOrPresent(message = "La fecha de vencimiento debe ser futura o actual")
    private LocalDateTime fechaVencimiento;

    @NotNull
    @Positive
    private Long creador;
    private String nombreCreador;//Este atributo solo se mostrará en los GET
    private String correoCreador;

    @Positive
    private Long tecnicoAsignado;
    private String nombreTecnico;//Este atributo solo se mostrará en los GET
    private String correoTecnico;

    @Size(max = 10, message = "Longitud inválida de la prioridad del ticket [10 caracteres]")
    private String prioridad;

    @NotBlank(message = "Es obligatorio seleccionar el tipo de ticket")
    @Size(max = 10, message = "Longitud inválida en el tipo de ticket [10 caracteres]")
    private String tipoTicket;

    @Size(max = 10, message = "Longitud inválida en el estado del ticket [10 caracteres]")
    private String estado;

    private LocalDateTime fechaCreacion;

    //Atributos que se muestran según el tipo de ticket
    private String descripcionUbicacion; //Si tipoTicket = "General"
    private List<String> codigosArticulos; //Si tipoTicket = "Articulo"
    private Long idUbicacionSoftware; //Si tipoTicket = "Software"
    private List<DetalleTSDTO> detallesSoftware; //Si tipoTicket = "Software"

    private List<String> evidencias;
    private String ubicacion;
}
