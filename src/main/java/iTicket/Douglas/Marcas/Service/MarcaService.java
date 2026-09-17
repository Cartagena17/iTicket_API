package iTicket.Douglas.Marcas.Service;

import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Marcas.DTO.MarcaDTO;
import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import iTicket.Douglas.Marcas.Repository.MarcaRepository;
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
public class MarcaService {

    private final MarcaRepository repo;

    @Transactional
    public MarcaDTO nuevaMarca(@Valid MarcaDTO dto) {
        String nombre = normalizar(dto.getNombreMarca());
        if (repo.existsByNombreMarcaIgnoreCase(nombre)) {
            throw new RecursoDuplicadoException("La marca '" + nombre + "' ya está registrada.");
        }
        dto.setNombreMarca(nombre);
        MarcaEntity entity = convertirAEntity(dto);
        MarcaEntity entitySave = repo.save(entity);
        log.info("Nueva marca registrada: " + entitySave.getIdMarca());
        return convertirADTO(entitySave);
    }

    public List<MarcaDTO> obtenerTodo() {
        List<MarcaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public MarcaDTO obtenerporId(Long id) {
        MarcaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una marca con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public MarcaDTO actualizar(Long id, @Valid MarcaDTO dto) {
        MarcaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una marca con id " + id));

        String nombre = normalizar(dto.getNombreMarca());
        if (repo.existsByNombreMarcaIgnoreCaseAndIdMarcaNot(nombre, id)) {
            throw new RecursoDuplicadoException("La marca '" + nombre + "' ya está registrada.");
        }
        entidad.setNombreMarca(nombre);
        MarcaEntity datosGuardados = repo.save(entidad);
        log.info("Marca con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    private MarcaEntity convertirAEntity(@Valid MarcaDTO dto) {
        MarcaEntity objEntity = new MarcaEntity();
        objEntity.setNombreMarca(dto.getNombreMarca());
        return objEntity;
    }

    private MarcaDTO convertirADTO(@Valid MarcaEntity entity) {
        MarcaDTO objDTO = new MarcaDTO();
        objDTO.setIdMarca(entity.getIdMarca());
        objDTO.setNombreMarca(entity.getNombreMarca());
        return objDTO;
    }

    private String normalizar(String valor) {
        return valor.trim().replaceAll("\\s+", " ");
    }
}
