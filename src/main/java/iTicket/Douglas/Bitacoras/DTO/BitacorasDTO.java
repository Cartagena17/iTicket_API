package iTicket.Douglas.Bitacoras.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @ToString
public class BitacorasDTO {

private Long idBitacora;
private LocalDateTime fechaHora;
@NotBlank (message = "El nuevo estado es obligatorio.")
@Size (max = 10, message = "El estado no puede exceder los 10 caracteres.")
private String nuevoEstado;
@NotNull (message = "El id del usuario es obligatorio.")
@Positive (message = "EL id del usuario debe ser un numero positivo.")
private Long idUsuario;
@NotNull (message = "EL id de ticket es obligatorio.")
@Positive (message = "El id ticket debe ser un numero positivo.")
private Long idTicket;
}
