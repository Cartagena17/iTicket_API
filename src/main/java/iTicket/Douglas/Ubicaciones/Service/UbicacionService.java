package iTicket.Douglas.Ubicaciones.Service;

import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionDTO;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import iTicket.Douglas.Ubicaciones.Repository.UbicacionRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UbicacionService {

    private final UbicacionRepository repo;

    public UbicacionService(UbicacionRepository repo) {
        this.repo = repo;
    }

    public UbicacionDTO nuevaUbicacion(@Valid UbicacionDTO dto){
        try {
            UbicacionEntity entity = convertirAEntity(dto);
            UbicacionEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar los datos de la Ubicacion: " + e.getMessage());
            return null;
        }
    }

    private UbicacionEntity convertirAEntity(@Valid UbicacionDTO dto){
        UbicacionEntity objEntity = new UbicacionEntity();
        objEntity.setNombreUbicacion(dto.getNombreUbicacion());
        TipoUbicacionEntity tipo = new TipoUbicacionEntity();
        tipo.setId(dto.getIdTipoUbicacion());
        objEntity.setTipoUbicacion(tipo);
        return objEntity;
    }

    private UbicacionDTO convertirADTO(@Valid UbicacionEntity entity){
        UbicacionDTO objDTO = new UbicacionDTO();
        objDTO.setId(entity.getId());
        objDTO.setNombreUbicacion(entity.getNombreUbicacion());
        objDTO.setIdTipoUbicacion(entity.getTipoUbicacion().getId());
        return objDTO;
    }

    public List<UbicacionDTO> obtenerTodo() {
        List<UbicacionEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UbicacionDTO buscarUbicacionPorId(Long id) {
        Optional<UbicacionEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarData(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public UbicacionDTO actualizar (Long id, @Valid UbicacionDTO dto){
        try {
            Optional<UbicacionEntity> registroExiste = repo.findById(id);
            if (registroExiste.isPresent()){
                UbicacionEntity entidad = registroExiste.get();
                entidad.setNombreUbicacion(dto.getNombreUbicacion());
                UbicacionEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops ocurrio un error al procesar la informacion");
            return null;
        }
    }

    public UbicacionDTO buscarUbicacionPorNombre(String nombreUbicacion) {
        try{
            Optional<UbicacionEntity> registro = repo.findByNombreUbicacion(nombreUbicacion);
            if (registro.isPresent()){
                return convertirADTO((registro.get()));
            }
            log.warn("No existe ninguna Ubicacion con el nombre: " + nombreUbicacion);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrio un error durante el proceso");
            return null;
        }
    }
}
