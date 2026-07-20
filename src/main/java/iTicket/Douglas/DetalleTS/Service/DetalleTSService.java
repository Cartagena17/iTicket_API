package iTicket.Douglas.DetalleTS.Service;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DetalleTSService {

    private final DetalleTSRepository repo;

    public DetalleTSService(DetalleTSRepository repo) {
        this.repo = repo;
    }

    public DetalleTSDTO nuevoDetalleTS(@Valid DetalleTSDTO dto) {
        try{
            //convertir a entity
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
        entity.setUbicacion(dto.getUbicacion());
        return entity;
    }

    private DetalleTSDTO convertirADTO(DetalleTSEntity entity) {
        DetalleTSDTO dto = new DetalleTSDTO();
        dto.setIdDetalleTs(entity.getIdDetalleTS());
        dto.setNombreSoftware(entity.getNombreSoftware());
        dto.setVersion(entity.getVersion());
        dto.setUbicacion(entity.getUbicacion());
        //Quitar comentario al unir las demas partes
//        dto.setTicket(entity.getTicket());
        return dto;
    }

    public List<DetalleTSDTO> obtenerTodos() {
        List<DetalleTSEntity> datos = repo.findAll();
        return  datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleTSDTO actualizarDetalleTS(Long id, @Valid DetalleTSDTO dto) {
        try {
            Optional<DetalleTSEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                DetalleTSEntity entidad = entidadOpcional.get();
                //Convertir y asignar los nuevos valores
                entidad.setNombreSoftware(dto.getNombreSoftware());
                entidad.setVersion(dto.getVersion());
                entidad.setUbicacion(dto.getUbicacion());
                //Quitar comentaario al unir las demas partes
//                entidad.setTicket(dto.getTicket());
                DetalleTSEntity datosGuardados = repo.save(entidad);
                return  convertirADTO(datosGuardados);
            }
            return null;
        }
        catch (Exception e){
            log.error("Error al procesar la informacion");
            return null;
        }
    }

    public boolean eliminarDetalleTS(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public DetalleTSDTO actualizarCampoDetalleTS(Long id, DetalleTSDTO dto) {
        try{
            Optional<DetalleTSEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                DetalleTSEntity entidad = entidadOpcional.get();

                //Evaluar campo por campo la info nueva
                if (dto.getNombreSoftware() != null && !dto.getNombreSoftware().trim().isEmpty()){
                    entidad.setNombreSoftware(dto.getNombreSoftware());
                }

                if (dto.getVersion() != null && !dto.getVersion().trim().isEmpty()){
                    entidad.setVersion(dto.getVersion());
                }

                if (dto.getUbicacion() != null && !dto.getUbicacion().trim().isEmpty()){
                    entidad.setUbicacion(dto.getUbicacion());
                }

                //Quitar comentario cuando se unan las demas partes
//                if (dto.getIdTicket() != null){
//                    entidad.setTicket(dto.getIdTicket());
//                }

                DetalleTSEntity datosGuardados = repo.save(entidad);
                return  convertirADTO(datosGuardados);
            }
            log.warn("No se encontro el registro: " + id);
            return null;
        }
        catch (Exception e){
            log.error("Error al procesar la información del ticke: " + id);
            return null;

        }
    }

    //Metodo para obtener detalle por id de ticket, quitar comentario al unir las demas partes
//    public DetalleTSDTO obtenerDetalleTSIdTicket(TicketsEntity ticket) {
//        try {
//            Optional<DetalleTSEntity> registro = repo.findByDetalleTSIdTicket(ticket);
//            if (registro != null){
//                return convertirADTO(registro.get());
//            }
//            log.warn("No existe ningun detalle de ticket: " + ticket);
//            return null;
//        }
//        catch (Exception e){
//            log.error("Ocurrio un error en el proceso de obtencion");
//            return null;
//        }
//    }
}
