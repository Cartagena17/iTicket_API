package iTicket.Douglas.Notificaciones.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionDTO {

    private Long idNotificacion;

    @NotNull(message = "El id del usuario destino es obligatorio.")
    @Positive
    private Long idUsuarioDestino;

    @NotBlank(message = "El tipo de notificación es obligatorio.")
    @Pattern(regexp = "TICKET_CREADO|TICKET_ELIMINADO|TICKET_ASIGNADO|TICKET_RESUELTO|TICKET_VENCIDO|PROYECTO_CREADO|FASE_CREADA", message = "Tipo de notificación no válido.")
    private String tipo;

    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres.")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio.")
    @Size(max = 300, message = "El mensaje no puede exceder los 300 caracteres.")
    private String mensaje;

    @Size(max = 20, message = "El tipo de entidad no puede exceder los 20 caracteres.")
    private String tipoEntidad;

    private Long idEntidad;

    private Boolean leida;

    private LocalDateTime fechaHora;
}