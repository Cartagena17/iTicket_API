package iTicket.Douglas.Modelos.Service;

import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import iTicket.Douglas.Marcas.Repository.MarcaRepository;
import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import iTicket.Douglas.Modelos.Repository.ModeloRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeloService {

    private final ModeloRepository repo;
    private final MarcaRepository marcaRepo;

    public ModeloDTO nuevoModelo(@Valid ModeloDTO dto) {
        try {
            Optional<MarcaEntity> marcaOpcional = marcaRepo.findById(dto.getIdMarca());
            if (marcaOpcional.isEmpty()) {
                log.warn("La marca con id " + dto.getIdMarca() + " no existe");
                return null;
            }

            ModeloEntity entity = convertirAEntity(dto, marcaOpcional.get());
            ModeloEntity entitySave = repo.save(entity);
            log.info("Nuevo modelo registrado: " + entitySave.getIdModelo());
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la información del modelo: " + e.getMessage());
            return null;
        }
    }

    public List<ModeloDTO> obtenerTodo() {
        List<ModeloEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ModeloDTO obtenerPorId(Long id) {
        Optional<ModeloEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

//    public List<ModeloDTO> obtenerPorMarca(Long idMarca) {
//        List<ModeloEntity> data = repo.findByMarca_IdMarca(idMarca);
//        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
//    }

    public ModeloDTO actualizarData(Long id,@Valid ModeloDTO dto) {
        try {
            Optional<ModeloEntity> registroExistente = repo.findById(id);
            if (registroExistente.isEmpty()) {
                return null;
            }
            ModeloEntity entidad = registroExistente.get();

            if (dto.getIdMarca() != null) {
                Optional<MarcaEntity> marcaOpcional = marcaRepo.findById(dto.getIdMarca());
                if (marcaOpcional.isEmpty()) {
                    log.warn("La marca con id " + dto.getIdMarca() + " no existe");
                    return null;
                }
                entidad.setMarca(marcaOpcional.get());
            }

            entidad.setNombreModelo(dto.getNombreModelo());
            ModeloEntity datosGuardados = repo.save(entidad);
            log.info("Modelo con id " + id + " actualizado");
            return convertirADTO(datosGuardados);
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la info: " + e.getMessage());
            return null;
        }
    }

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
