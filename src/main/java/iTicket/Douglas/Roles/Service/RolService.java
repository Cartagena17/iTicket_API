package iTicket.Douglas.Roles.Service;

import iTicket.Douglas.Roles.DTO.RolDTO;
import iTicket.Douglas.Roles.Entity.RolEntity;
import iTicket.Douglas.Roles.Repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository repo;

    public RolDTO nuevoRol(RolDTO dto) {
        try {
            RolEntity entity = convertirAEntity(dto);
            RolEntity entitySave = repo.save(entity);
            log.info("Nuevo rol registrado: " + entitySave.getIdRol());
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la información del rol: " + e.getMessage());
            return null;
        }
    }

    public List<RolDTO> obtenerTodo() {
        List<RolEntity> lista = repo.findAll();
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public RolDTO obtenerPorId(Long id) {
        Optional<RolEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarRol(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public RolDTO actualizarData(Long id, RolDTO dto) {
        try {
            Optional<RolEntity> registroExistente = repo.findById(id);
            if (registroExistente.isPresent()) {
                RolEntity entidad = registroExistente.get();
                entidad.setNombreRol(dto.getNombreRol());
                RolEntity datosGuardados = repo.save(entidad);
                log.info("Rol con id " + id + " actualizado");
                return convertirADTO(datosGuardados);
            }
            return null;
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la info: " + e.getMessage());
            return null;
        }
    }

    private RolEntity convertirAEntity(RolDTO dto) {
        RolEntity objEntity = new RolEntity();
        objEntity.setNombreRol(dto.getNombreRol());
        return objEntity;
    }

    private RolDTO convertirADTO(RolEntity entity) {
        RolDTO objDTO = new RolDTO();
        objDTO.setIdRol(entity.getIdRol());
        objDTO.setNombreRol(entity.getNombreRol());
        return objDTO;
    }
}
