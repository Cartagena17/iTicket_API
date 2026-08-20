package iTicket.Douglas.Proyectos.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoPaginaDTO {

    private List<ProyectoDTO> proyectos;
    private long totalElementos;
    private int totalPaginas;
    private int paginaActual;
}
