package iTicket.Douglas.Ubicaciones.Service;

import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.TipoUbicacion.Repository.TipoUbicacionRepository;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionDTO;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import iTicket.Douglas.Ubicaciones.Repository.UbicacionRepository;
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
public class UbicacionService {

    private final UbicacionRepository repo;
    private final TipoUbicacionRepository tipoUbicacionRepo;

    @Transactional
    public UbicacionDTO nuevaUbicacion(@Valid UbicacionDTO dto) {
        String nombre = normalizar(dto.getNombreUbicacion());
        if (repo.existsByNombreUbicacionIgnoreCase(nombre)) {
            throw new RecursoDuplicadoException("La ubicación '" + nombre + "' ya está registrada.");
        }
        TipoUbicacionEntity tipo = tipoUbicacionRepo.findById(dto.getIdTipoUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de ubicación con id " + dto.getIdTipoUbicacion()));

        dto.setNombreUbicacion(nombre);
        UbicacionEntity entity = convertirAEntity(dto, tipo);
        UbicacionEntity entitySave = repo.save(entity);
        log.info("Nueva ubicación registrada: " + entitySave.getId());
        return convertirADTO(entitySave);
    }

    public List<UbicacionDTO> obtenerTodo() {
        List<UbicacionEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UbicacionDTO buscarUbicacionPorId(Long id) {
        UbicacionEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una ubicación con id " + id));
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
    public UbicacionDTO actualizar(Long id, @Valid UbicacionDTO dto) {
        UbicacionEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una ubicación con id " + id));

        TipoUbicacionEntity tipo = tipoUbicacionRepo.findById(dto.getIdTipoUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de ubicación con id " + dto.getIdTipoUbicacion()));

        String nombre = normalizar(dto.getNombreUbicacion());
        if (repo.existsByNombreUbicacionIgnoreCaseAndIdNot(nombre, id)) {
            throw new RecursoDuplicadoException("La ubicación '" + nombre + "' ya está registrada.");
        }
        entidad.setNombreUbicacion(nombre);
        entidad.setTipoUbicacion(tipo);

        UbicacionEntity datosGuardados = repo.save(entidad);
        log.info("Ubicación con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    public UbicacionDTO buscarUbicacionPorNombre(String nombreUbicacion) {
        UbicacionEntity entidad = repo.findByNombreUbicacion(nombreUbicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ninguna ubicación con nombre: " + nombreUbicacion));
        return convertirADTO(entidad);
    }

    private UbicacionEntity convertirAEntity(@Valid UbicacionDTO dto, TipoUbicacionEntity tipo) {
        UbicacionEntity objEntity = new UbicacionEntity();
        objEntity.setNombreUbicacion(dto.getNombreUbicacion());
        objEntity.setTipoUbicacion(tipo);
        return objEntity;
    }

    private UbicacionDTO convertirADTO(UbicacionEntity entity) {
        UbicacionDTO objDTO = new UbicacionDTO();
        objDTO.setId(entity.getId());
        objDTO.setNombreUbicacion(entity.getNombreUbicacion());
        objDTO.setIdTipoUbicacion(entity.getTipoUbicacion().getId());
        objDTO.setNombreTipoUbicacion(entity.getTipoUbicacion().getNombreTipoUbicacion());
        return objDTO;
    }

    private String normalizar(String valor) {
        return valor.trim().replaceAll("\\s+", " ");
    }
}
