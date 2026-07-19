package iTicket.Douglas.Ubicaciones.Service;

import iTicket.Douglas.Marcas.DTO.MarcaDTO;
import iTicket.Douglas.Marcas.Entity.MarcaEntity;
import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionesDTO;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionesEntity;
import iTicket.Douglas.Ubicaciones.Repository.UbicacionesRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UbicacionesService {

    private final UbicacionesRepository repo;

    public UbicacionesService(UbicacionesRepository repo) {
        this.repo = repo;
    }

    public UbicacionesDTO nuevaUbicacion(@Valid UbicacionesDTO dto){
        try {
            UbicacionesEntity entity = convertirAEntity(dto);
            UbicacionesEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar los datos de la Ubicacion: " + e.getMessage());
            return null;
        }
    }

    private UbicacionesEntity convertirAEntity(@Valid UbicacionesDTO dto){
        UbicacionesEntity objEntity = new UbicacionesEntity();
        objEntity.setNombreUbicacion(dto.getNombreUbicacion());
        TipoUbicacionEntity tipo = new TipoUbicacionEntity();
        tipo.setId(dto.getIdTipoUbicacion());
        objEntity.setTipoUbicacion(tipo);
        return objEntity;
    }

    private UbicacionesDTO convertirADTO(@Valid UbicacionesEntity entity){
        UbicacionesDTO objDTO = new UbicacionesDTO();
        objDTO.setId(entity.getId());
        objDTO.setNombreUbicacion(entity.getNombreUbicacion());
        objDTO.setIdTipoUbicacion(entity.getTipoUbicacion().getId());
        return objDTO;
    }

    public List<UbicacionesDTO> obtenerTodo() {
        List<UbicacionesEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UbicacionesDTO buscarUbicacionPorId(Long id) {
        Optional<UbicacionesEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarData(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public UbicacionesDTO actualizar (Long id, @Valid UbicacionesDTO dto){
        try {
            Optional<UbicacionesEntity> registroExiste = repo.findById(id);
            if (registroExiste.isPresent()){
                UbicacionesEntity entidad = registroExiste.get();
                entidad.setNombreUbicacion(dto.getNombreUbicacion());
                UbicacionesEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops ocurrio un error al procesar la informacion");
            return null;
        }
    }

    public UbicacionesDTO buscarUbicacionPorNombre(String nombreUbicacion) {
        try{
            Optional<UbicacionesEntity> registro = repo.findByNombreUbicacion(nombreUbicacion);
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
