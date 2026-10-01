package iTicket.Douglas.Bitacoras.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraPaginaDTO {

    private List<BitacoraDTO> bitacoras;
    private long totalElementos;
    private int totalPaginas;
    private int paginaActual;
}
