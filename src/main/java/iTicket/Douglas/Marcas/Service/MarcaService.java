package iTicket.Douglas.Marcas.Service;

import iTicket.Douglas.Marcas.DTO.MarcaDTO;
import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import iTicket.Douglas.Marcas.Repository.MarcaRepository;
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
public class MarcaService {

    private final MarcaRepository repo;

    public MarcaDTO nuevaMarca(@Valid MarcaDTO dto){
        try {
            MarcaEntity entity = convertirAEntity(dto);
            MarcaEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la informacion de la marca" + e.getMessage());
            return null;
        }
    }

    public List<MarcaDTO> obtenerTodo(){
        List<MarcaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public MarcaDTO obtenerporId(Long id){
        Optional<MarcaEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminar(Long id){
        if (repo.existsById(id)){
            repo.deleteById(id);
            return  true;
        }
        return false;
    }

    public MarcaDTO actualizar (Long id, @Valid MarcaDTO dto){
        try {
            Optional<MarcaEntity> registroExiste = repo.findById(id);
            if (registroExiste.isPresent()){
                MarcaEntity entidad = registroExiste.get();
                entidad.setNombreMarca(dto.getNombreMarca());
                MarcaEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops ocurrio un error al procesar la informacion");
            return null;
        }
    }

    private MarcaEntity convertirAEntity (@Valid MarcaDTO dto){
        MarcaEntity objEntity = new MarcaEntity();
        objEntity.setNombreMarca(dto.getNombreMarca());
        return objEntity;
    }
    private MarcaDTO convertirADTO (@Valid MarcaEntity entity){
        MarcaDTO objDTO = new MarcaDTO();
        objDTO.setIdMarca(entity.getIdMarca());
        objDTO.setNombreMarca(entity.getNombreMarca());
        return objDTO;
    }
}
