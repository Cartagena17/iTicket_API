package iTicket.Douglas.Bitacoras.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BitacoraDTO {

    private Long idBitacora;
    @NotNull(message = "El id del ticket es obligatorio.")
    @Positive
    private Long ticket;
    private String asunto;

    @NotNull (message = "El id del usuario es obligatorio.")
    @Positive
    private Long usuario;
    private String nombreUsuario;

    @NotBlank(message = "El nuevo estado es obligatorio.")
    @Size(max = 10, message = "El nuevo estado no puede exceder los 10 caracteres.")
    private String nuevoEstado;

    private LocalDateTime fechaHora;
}
