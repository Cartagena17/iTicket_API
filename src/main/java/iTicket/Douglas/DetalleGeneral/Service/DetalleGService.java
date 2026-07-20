package iTicket.Douglas.DetalleGeneral.Service;

import iTicket.Douglas.DetalleGeneral.Controller.DetalleGController;
import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.DetalleGeneral.Repository.DetalleGRepository;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DetalleGService {

    private final DetalleGRepository repo;

    public DetalleGService(DetalleGRepository repo) {
        this.repo = repo;
    }

    private DetalleGEntity convertirAEntity(@Valid DetalleGDTO dto){
        DetalleGEntity entity = new DetalleGEntity();
        entity.setDescripcionUbicacion(dto.getDescripcionUbicacion());
        return entity;
    }

    private DetalleGDTO convertirADTO(@Valid DetalleGEntity entity){
        DetalleGDTO dto = new DetalleGDTO();
        dto.setIdDetalleGeneral(entity.getIdDetalleGeneral());
        dto.setDescripcionUbicacion(entity.getDescripcionUbicacion());
        //Quitar comentario al unir las demas partes
//        dto.setTicket(entity.getTicket());
        return dto;
    }

    public DetalleGDTO nuevoDetalleG(@Valid DetalleGDTO dto){
        try {
            DetalleGEntity entity = convertirAEntity(dto);
            DetalleGEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }
        catch (Exception e){
            log.error("Error al ingresar el detalle del ticket" + e.getMessage());
            return null;
        }
    }

    public List<DetalleGDTO> obtenerDetallesG() {
        List<DetalleGEntity> datos = repo.findAll();
        return  datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleGDTO actualizarDetalleG(Long id, @Valid DetalleGDTO dto) {
        try {
            Optional<DetalleGEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                DetalleGEntity entidad = entidadOpcional.get();
                entidad.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                //Quitar comentario al unir las demas partes
//                entidad.setTicket(dto.getTicket());
                DetalleGEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }
        catch (Exception e){
            log.error("Error al procesar la informacion");
            return null;
        }
    }

    public boolean eliminarDetalleG(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public DetalleGDTO actualizarCampoDetalleG(Long id, DetalleGDTO dto) {
        try {
            Optional<DetalleGEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                DetalleGEntity entidad = entidadOpcional.get();

                if (dto.getDescripcionUbicacion() != null && !dto.getDescripcionUbicacion().trim().isEmpty()){
                    entidad.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                }

                //Quitar comentario cuando se unan las demas partes
//                if (dto.getIdTicket() != null){
//                    entidad.setTicket(dto.getIdTicket());
//                }

                DetalleGEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            log.warn("No se encontro el registro: " + id);
            return null;
        }
        catch (Exception e){
            log.error("Error al procesar la información");
            return null;
        }
    }

    //Metodo para obtener detalle de ticket general por id de ticket, quitar comentario cuando se unan las demas partes
//    public DetalleGDTO obtenerDetallesIdTicket(TicketsEntity ticket) {
//        try{
//            Optional<DetalleGEntity> registro = repo.findByDetalleGIdTicket(ticket);
//            if (registro != null){
//                return  convertirADTO(registro.get());
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
