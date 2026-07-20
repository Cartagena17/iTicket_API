package iTicket.Douglas.Prioridades.Service;

import iTicket.Douglas.Prioridades.DTO.PrioridadDTO;
import iTicket.Douglas.Prioridades.Entity.PrioridadEntity;
import iTicket.Douglas.Prioridades.Repository.PrioridadRepository;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrioridadService {

    //Inyectar dependencias
    private final PrioridadRepository repo;

    public PrioridadService(PrioridadRepository repo) {
        this.repo = repo;
    }

    //Método para crear prioridades
    public PrioridadDTO nuevaPrioridad(@Valid PrioridadDTO dto){
        try {
            PrioridadEntity entity = convertirAEntity(dto);
            PrioridadEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al registrar la prioridad " + e.getMessage());
            throw new RuntimeException("Error al registrar la prioridad");
        }
    }

    //Método para obtener todas las prioridades
    public List<PrioridadDTO> obtenerTodo(){
        List<PrioridadEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    //Método para obtener prioridades por id
    public PrioridadDTO buscarPrioridad(Long id) {
        Optional<PrioridadEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    //Método para eliminar prioridades
    public boolean eliminarData(Long id){
        //Verficiar si el registro existe
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    //Método para actializar prioridades
    public PrioridadDTO actualizarPrioridad(Long id, @Valid PrioridadDTO dto){
        try {
            //Verificar si existe la prioridad
            Optional<PrioridadEntity> prioridadOp = repo.findById(id);
            if (prioridadOp.isPresent()){
                  PrioridadEntity entidad = prioridadOp.get();

                  entidad.setNombrePrioridad(dto.getNombrePrioridad());

                  PrioridadEntity datosGuardados = repo.save(entidad);
                  return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops, ocurrió un error al procesar la información");
            return null;
        }
    }

    private PrioridadDTO convertirADTO(PrioridadEntity entitySave) {
        PrioridadDTO objDTO = new PrioridadDTO();
        objDTO.setIdPrioridad(entitySave.getIdPrioridad());
        objDTO.setNombrePrioridad(entitySave.getNombrePrioridad());
        return objDTO;
    }

    private PrioridadEntity convertirAEntity(@Valid PrioridadDTO dto) {
        PrioridadEntity objEntity = new PrioridadEntity();
        objEntity.setNombrePrioridad(dto.getNombrePrioridad());
        return objEntity;
    }
}
