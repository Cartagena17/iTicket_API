package iTicket.Douglas.Modelos.Service;

import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import iTicket.Douglas.Marcas.Repository.MarcaRepository;
import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import iTicket.Douglas.Modelos.Repository.ModeloRepository;
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
public class ModeloService {

    private final ModeloRepository repo;
    private final MarcaRepository marcaRepo;

    @Transactional
    public ModeloDTO nuevoModelo(@Valid ModeloDTO dto) {
        MarcaEntity marca = marcaRepo.findById(dto.getIdMarca())
                .orElseThrow(() -> new RecursoNoEncontradoException("La marca con id " + dto.getIdMarca() + " no existe"));

        ModeloEntity entity = convertirAEntity(dto, marca);
        ModeloEntity entitySave = repo.save(entity);
        log.info("Nuevo modelo registrado: " + entitySave.getIdModelo());
        return convertirADTO(entitySave);
    }

    public List<ModeloDTO> obtenerTodo() {
        List<ModeloEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ModeloDTO obtenerPorId(Long id) {
        ModeloEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un modelo con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public ModeloDTO actualizarData(Long id, @Valid ModeloDTO dto) {
        ModeloEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un modelo con id " + id));

        if (dto.getIdMarca() != null) {
            MarcaEntity marca = marcaRepo.findById(dto.getIdMarca())
                    .orElseThrow(() -> new RecursoNoEncontradoException("La marca con id " + dto.getIdMarca() + " no existe"));
            entidad.setMarca(marca);
        }

        entidad.setNombreModelo(dto.getNombreModelo());
        ModeloEntity datosGuardados = repo.save(entidad);
        log.info("Modelo con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarModelo(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private ModeloDTO convertirADTO(@Valid ModeloEntity entity) {
        ModeloDTO objDTO = new ModeloDTO();
        objDTO.setIdModelo(entity.getIdModelo());
        objDTO.setNombreModelo(entity.getNombreModelo());
        objDTO.setIdMarca(entity.getMarca().getIdMarca());
        objDTO.setNombreMarca(entity.getMarca().getNombreMarca());
        return objDTO;
    }

    private ModeloEntity convertirAEntity(@Valid ModeloDTO dto, MarcaEntity marca) {
        ModeloEntity objEntity = new ModeloEntity();
        objEntity.setNombreModelo(dto.getNombreModelo());
        objEntity.setMarca(marca);
        return objEntity;
    }
}