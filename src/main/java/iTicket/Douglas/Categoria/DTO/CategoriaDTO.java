package iTicket.Douglas.Categoria.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaDTO {

    private Long idCategoria;
    @NotBlank @Size (max = 20, message = "El nombre de la categoria no puede exceder los 20 caracteres.")
    private String nombreCategoria;
}
