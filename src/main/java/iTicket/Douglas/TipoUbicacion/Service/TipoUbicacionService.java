package iTicket.Douglas.TipoUbicacion.Service;

import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Entity.TipoUbicacionEntity;
import iTicket.Douglas.TipoUbicacion.Repository.TipoUbicacionRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TipoUbicacionService {

    private final TipoUbicacionRepository repo;

    public TipoUbicacionService(TipoUbicacionRepository repo) {
        this.repo = repo;
    }

    public TipoUbicacionDTO nuevoTipoUbicacion(@Valid TipoUbicacionDTO dto){

        try{
            TipoUbicacionEntity entity = convertirAEntity(dto);
            TipoUbicacionEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la informacion de el tipo de ubicacion" + e.getMessage());
            return null;
        }
    }

    private TipoUbicacionEntity convertirAEntity (@Valid TipoUbicacionDTO dto){

        TipoUbicacionEntity objEntity = new TipoUbicacionEntity();
        objEntity.setNombreTipoUbicacion(dto.getNombre_tipo_ubicacion());
        return objEntity;
    }

    private TipoUbicacionDTO convertirADTO(@Valid TipoUbicacionEntity entity){

        TipoUbicacionDTO objDTO = new TipoUbicacionDTO();
        objDTO.setId(entity.getId());
        objDTO.setNombre_tipo_ubicacion(entity.getNombreTipoUbicacion());
        return objDTO;
    }

    public List<TipoUbicacionDTO> obtenerTodo(){

        List<TipoUbicacionEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TipoUbicacionDTO buscarNombreTipoUbicacion(Long id) {

        Optional<TipoUbicacionEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarData(Long id){

        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public TipoUbicacionDTO actualizar(Long id, @Valid TipoUbicacionDTO dto){

        try{
            Optional<TipoUbicacionEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                TipoUbicacionEntity entidad = entidadOpcional.get();
                entidad.setNombreTipoUbicacion(dto.getNombre_tipo_ubicacion());
                TipoUbicacionEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        } catch (Exception e) {
            log.error("Oops, ocurrio un error al procesar la infromacion");
            return null;
        }

    }

    public TipoUbicacionDTO buscarNombreTipoUbicacion(String nombreTipoUbicacion) {
        try{
            Optional<TipoUbicacionEntity> registro = repo.findByNombreTipoUbicacion(nombreTipoUbicacion);
            if (registro.isPresent()){
                return convertirADTO((registro.get()));
            }
            log.warn("No existe ningun Tipo de Ubicacion con Nombre: " + nombreTipoUbicacion);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrio un error durante el proceso");
            return null;
        }
    }
}
