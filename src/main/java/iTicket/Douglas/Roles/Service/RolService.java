package iTicket.Douglas.Roles.Service;

import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Roles.DTO.RolDTO;
import iTicket.Douglas.Roles.Entity.RolEntity;
import iTicket.Douglas.Roles.Repository.RolRepository;
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
public class RolService {

    private final RolRepository repo;

    @Transactional
    public RolDTO nuevoRol(@Valid RolDTO dto) {
        RolEntity entity = convertirAEntity(dto);
        RolEntity entitySave = repo.save(entity);
        log.info("Nuevo rol registrado: " + entitySave.getIdRol());
        return convertirADTO(entitySave);
    }

    public List<RolDTO> obtenerTodo() {
        List<RolEntity> lista = repo.findAll();
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public RolDTO obtenerPorId(Long id) {
        RolEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un rol con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarRol(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public RolDTO actualizarData(Long id, @Valid RolDTO dto) {
        RolEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un rol con id " + id));

        entidad.setNombreRol(dto.getNombreRol());
        RolEntity datosGuardados = repo.save(entidad);
        log.info("Rol con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    private RolEntity convertirAEntity(@Valid RolDTO dto) {
        RolEntity objEntity = new RolEntity();
        objEntity.setNombreRol(dto.getNombreRol());
        return objEntity;
    }

    private RolDTO convertirADTO(@Valid RolEntity entity) {
        RolDTO objDTO = new RolDTO();
        objDTO.setIdRol(entity.getIdRol());
        objDTO.setNombreRol(entity.getNombreRol());
        return objDTO;
    }
}