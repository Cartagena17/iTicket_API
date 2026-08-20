package iTicket.Douglas.TipoUbicacion.Service;

import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.TipoUbicacion.Repository.TipoUbicacionRepository;
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
public class TipoUbicacionService {

    private final TipoUbicacionRepository repo;

    @Transactional
    public TipoUbicacionDTO nuevoTipoUbicacion(@Valid TipoUbicacionDTO dto) {
        TipoUbicacionEntity entity = convertirAEntity(dto);
        TipoUbicacionEntity entitySave = repo.save(entity);
        log.info("Nuevo tipo de ubicación registrado: " + entitySave.getId());
        return convertirADTO(entitySave);
    }

    public List<TipoUbicacionDTO> obtenerTodo() {
        List<TipoUbicacionEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TipoUbicacionDTO buscarNombreTipoUbicacion(Long id) {
        TipoUbicacionEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un tipo de ubicación con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarData(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public TipoUbicacionDTO actualizar(Long id, @Valid TipoUbicacionDTO dto) {
        TipoUbicacionEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un tipo de ubicación con id " + id));

        entidad.setNombreTipoUbicacion(dto.getNombre_tipo_ubicacion());
        TipoUbicacionEntity datosGuardados = repo.save(entidad);
        log.info("Tipo de ubicación con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    public TipoUbicacionDTO buscarNombreTipoUbicacion(String nombreTipoUbicacion) {
        TipoUbicacionEntity entidad = repo.findByNombreTipoUbicacion(nombreTipoUbicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún tipo de ubicación con nombre: " + nombreTipoUbicacion));
        return convertirADTO(entidad);
    }

    private TipoUbicacionEntity convertirAEntity(@Valid TipoUbicacionDTO dto) {
        TipoUbicacionEntity objEntity = new TipoUbicacionEntity();
        objEntity.setNombreTipoUbicacion(dto.getNombre_tipo_ubicacion());
        return objEntity;
    }

    private TipoUbicacionDTO convertirADTO(@Valid TipoUbicacionEntity entity) {
        TipoUbicacionDTO objDTO = new TipoUbicacionDTO();
        objDTO.setId(entity.getId());
        objDTO.setNombre_tipo_ubicacion(entity.getNombreTipoUbicacion());
        return objDTO;
    }
}