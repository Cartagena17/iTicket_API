package iTicket.Douglas.DetalleTS.Service;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
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
public class DetalleTSService {

    private final DetalleTSRepository repo;
    private final TicketRepository ticketRepo;
    private final UbicacionRepository ubicacionRepo;

    public DetalleTSService(DetalleTSRepository repo, TicketRepository ticketRepo, UbicacionRepository ubicacionRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
        this.ubicacionRepo = ubicacionRepo;
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
        entity.setUbicacion(buscarUbicacion(dto.getIdUbicacion()));
        entity.setTicket(buscarTicket(dto.getTicket()));
        return entity;
    }

    private DetalleTSDTO convertirADTO(DetalleTSEntity entity) {
        DetalleTSDTO dto = new DetalleTSDTO();
        dto.setIdDetalleTs(entity.getIdDetalleTS());
        dto.setNombreSoftware(entity.getNombreSoftware());
        dto.setVersion(entity.getVersion());
        dto.setIdUbicacion(entity.getUbicacion().getId());
        dto.setNombreUbicacion(entity.getUbicacion().getNombreUbicacion());
        //Quitar comentario al unir las demas partes
        dto.setTicket(entity.getTicket().getIdTicket());
        dto.setAsunto(entity.getTicket().getAsunto());
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
                entidad.setUbicacion(buscarUbicacion(dto.getIdUbicacion()));
                //Quitar comentaario al unir las demas partes
                entidad.setTicket(buscarTicket(dto.getTicket()));
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

    //Metodo para obtener detalle por id de ticket, quitar comentario al unir las demas partes
    public DetalleTSDTO obtenerDetalleTSIdTicket(TicketEntity ticket) {
        try {
            Optional<DetalleTSEntity> registro = repo.findByTicket(ticket);
            if (registro.isPresent()){
                return convertirADTO(registro.get());
            }
            log.warn("No existe ningun detalle de ticket: " + ticket);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrio un error en el proceso de obtencion");
            return null;
        }
    }

    private TicketEntity buscarTicket(Long id){
        Optional<TicketEntity> ticket = ticketRepo.findById(id);
        if (ticket.isPresent()){
            return ticket.get();
        }
        log.warn("No existe ningun ticket con id: " + id);
        throw new RuntimeException("No existe ningun ticket con id: " + id);
    }

    private UbicacionEntity buscarUbicacion(Long id){
        Optional<UbicacionEntity> ubicacion = ubicacionRepo.findById(id);
        if (ubicacion.isPresent()){
            return ubicacion.get();
        }
        log.warn("No existe ninguna ubicacion con id: " + id);
        throw new RuntimeException("No existe ninguna ubicacion con id: " + id);
    }
}
