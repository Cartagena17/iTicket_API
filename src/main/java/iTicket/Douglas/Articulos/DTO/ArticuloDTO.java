package iTicket.Douglas.Articulos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticuloDTO {

    private Long idArticulo;

    @NotBlank
    @Size(max = 20, message = "El código no puede exceder los 20 caracteres")
    private String codigoArticulo;

    private Long idModelo;
    private String nombreModelo;
    private String nombreMarca;

    @NotNull(message = "Debes indicar la categoria del articulo")
    private Long idCategoria;
    private String nombreCategoria;

    @NotNull(message = "Debe indicar la ubicacion del articulo")
    private Long idUbicacion;
    private String nombreUbicacion;
}
