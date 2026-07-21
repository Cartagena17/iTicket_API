package iTicket.Douglas.Departamentos.Service;

import iTicket.Douglas.Areas.Entity.AreaEntity;
import iTicket.Douglas.Areas.Repository.AreaRepository;
import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository repo;
    private final AreaRepository arearepo;

    public DepartamentoDTO nuevoDepartamento(@Valid DepartamentoDTO dto) {
        try {
            Optional<AreaEntity> areaOpcional = arearepo.findById(dto.getIdArea());
            if (areaOpcional.isEmpty()){
                log.warn("El area con id "+dto.getIdArea()+" no existe");
                return null;
            }
            DepartamentoEntity entity = convertirAEntity(dto, areaOpcional.get());
            DepartamentoEntity entitySave = repo.save(entity);
            log.info("Nuevo departamento registrado: " + entitySave.getIdDepartamento());
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la informacion del departamento " +e.getMessage());
            return null;
        }
    }

    public List<DepartamentoDTO> obetenerTodo(){
        List<DepartamentoEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
    
    public DepartamentoDTO obtenerPorId(Long id){
        Optional<DepartamentoEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public DepartamentoDTO actualizar(Long id, DepartamentoDTO dto){
        try {
            Optional<DepartamentoEntity> registroExistente = repo.findById(id);
            if (registroExistente.isPresent()){
                DepartamentoEntity entidad = registroExistente.get();

                if (dto.getIdArea() != null){
                    Optional<AreaEntity> areaOpcional = arearepo.findById(dto.getIdArea());
                    if (areaOpcional.isEmpty()){
                        log.warn("El área con id " + dto.getIdArea() + " no existe");
                        return null;
                    }
                    entidad.setArea(areaOpcional.get());
                }
                entidad.setNombreDepartamento(dto.getNombreDepartamento());
                DepartamentoEntity datosGuardados = repo.save(entidad);
                log.info("Departamento con id " + id + " actualizado");
                return  convertirADTO(datosGuardados);
            }
            return null;
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la informacion " + e.getMessage());
            return null;
        }
    }

    public boolean eliminar (Long id){
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private DepartamentoEntity convertirAEntity(@Valid DepartamentoDTO dto, AreaEntity area) {
        DepartamentoEntity objEntity = new DepartamentoEntity();
        objEntity.setNombreDepartamento(dto.getNombreDepartamento());
        objEntity.setArea(area);
        return objEntity;
    }

    private DepartamentoDTO convertirADTO(@Valid DepartamentoEntity entity) {
        DepartamentoDTO objDTO = new DepartamentoDTO();
        objDTO.setIdDepartamento(entity.getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getNombreDepartamento());
        objDTO.setIdArea(entity.getArea().getIdArea());
        objDTO.setNombreArea(entity.getArea().getNombreArea());
        return objDTO;
    }

}
