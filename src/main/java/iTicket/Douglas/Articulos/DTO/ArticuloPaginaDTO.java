package iTicket.Douglas.Articulos.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloPaginaDTO {

    private List<ArticuloDTO> articulos;
    private long totalElementos;
    private int totalPaginas;
    private int paginaActual;
}
