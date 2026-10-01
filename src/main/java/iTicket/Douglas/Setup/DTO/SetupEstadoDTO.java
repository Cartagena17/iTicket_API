package iTicket.Douglas.Setup.DTO;

import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Lo unico que la pantalla de primer usuario necesita
 * Cuando ya hay usuarios la lista viaja vacia: no se filtra nada de la base
 * a quien todavia no ha iniciado sesion.
 */
@Data
@AllArgsConstructor
public class SetupEstadoDTO {

    private boolean hayUsuarios;
    private List<DepartamentoDTO> departamentos;
}
