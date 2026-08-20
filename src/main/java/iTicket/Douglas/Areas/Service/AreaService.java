package iTicket.Douglas.Areas.Service;

import iTicket.Douglas.Areas.DTO.AreaDTO;
import iTicket.Douglas.Areas.Entity.AreaEntity;
import iTicket.Douglas.Areas.Repository.AreaRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository repo;

    @Transactional
    public AreaDTO nuevaArea(@Valid AreaDTO dto) {
        AreaEntity entity = convertirAEntity(dto);
        AreaEntity entitySave = repo.save(entity);
        log.info("Nueva área registrada: " + entitySave.getIdArea());
        return convertirADTO(entitySave);
    }

    public List<AreaDTO> obtenerTodo() {
        List<AreaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public AreaDTO obtenerPorId(Long id) {
        AreaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un área con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public AreaDTO editarArea(Long id, @Valid AreaDTO dto) {
        AreaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un área con id " + id));

        entidad.setNombreArea(dto.getNombreArea());
        AreaEntity datosGuardados = repo.save(entidad);
        log.info("Área con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarArea(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private AreaEntity convertirAEntity(@Valid AreaDTO dto) {
        AreaEntity objEntity = new AreaEntity();
        objEntity.setNombreArea(dto.getNombreArea());
        return objEntity;
    }

    private AreaDTO convertirADTO(@Valid AreaEntity entity) {
        AreaDTO objDTO = new AreaDTO();
        objDTO.setIdArea(entity.getIdArea());
        objDTO.setNombreArea(entity.getNombreArea());
        return objDTO;
    }
}