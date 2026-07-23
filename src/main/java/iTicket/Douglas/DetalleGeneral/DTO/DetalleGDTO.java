package iTicket.Douglas.DetalleGeneral.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DetalleGDTO {

    private Long idDetalleGeneral;
    @NotBlank @Size (max = 200, message = "La descripcion de la ubicacion no puede exceder los 200 caracteres")
    private String descripcionUbicacion;
    //Quitar comentario al unir las demas partes
    @NotNull @Positive(message = "El id del ticket debe ser un numero positivo")
    private Long ticket;
    private String asunto;
}
