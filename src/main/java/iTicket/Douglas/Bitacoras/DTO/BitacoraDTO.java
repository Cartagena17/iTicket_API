package iTicket.Douglas.Bitacoras.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraDTO {

    private Long idBitacora;
    @NotNull(message = "El id del ticket es obligatorio.")
    @Positive
    private Long idTicket;

    @NotBlank(message = "El el código del ticket es obligatorio.")
    @Size(max = 20, message = "El código del ticket no puede exceder los 20 caracteres.")
    private String codigoTicket;

    @NotBlank(message = "El el asunto del ticket es obligatorio.")
    @Size(max = 100, message = "El asunto del ticket no puede exceder los 100 caracteres.")
    private String asuntoTicket;

    @NotNull (message = "El id del usuario es obligatorio.")
    @Positive
    private Long usuario;
    private String nombreUsuario;
    private String correoUsuario;

    @NotBlank(message = "El nuevo estado es obligatorio.")
    @Size(max = 10, message = "El nuevo estado no puede exceder los 10 caracteres.")
    private String nuevoEstado;

    private LocalDateTime fechaHora;
}
