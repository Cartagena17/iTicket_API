package iTicket.Douglas.DetalleTS.Service;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DetalleTSService {

    private final DetalleTSRepository repo;

    public DetalleTSService(DetalleTSRepository repo) {
        this.repo = repo;
    }

    public DetalleTSDTO nuevoDetalleTS(@Valid DetalleTSDTO dto) {
        try{
            //convertir a dto
            DetalleTSEntity entity = convertirAEntity(dto);
            //Guardar en la base de datos
            DetalleTSEntity entitySave = repo.save(entity);
            //Devolver una respuesta
            return  convertirADTO(entitySave);
        }
        catch (Exception e){
            log.error("Error al ingresar los datos del detalle" + e.getMessage());
            return null;
        }
    }

    private DetalleTSEntity convertirAEntity(@Valid DetalleTSDTO dto){
        DetalleTSEntity entity = new DetalleTSEntity();
        entity.setNombreSoftware(dto.getNombreSoftware());
        entity.setVersion(dto.getVersion());
        entity.setUbicacion(dto.getVersion());
        return entity;
    }

    private DetalleTSDTO convertirADTO(DetalleTSEntity entity) {
        DetalleTSDTO dto = new DetalleTSDTO();
        dto.setIdDetalleTs(entity.getIdDetalleTS());
        dto.setNombreSoftware(entity.getNombreSoftware());
        dto.setVersion(entity.getVersion());
        dto.setUbicacion(entity.getUbicacion());
        return dto;
    }

    public List<DetalleTSDTO> obtenerTodos() {
        List<DetalleTSEntity> datos = repo.findAll();
        return  datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
