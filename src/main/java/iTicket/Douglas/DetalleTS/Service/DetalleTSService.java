package iTicket.Douglas.DetalleTS.Service;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import iTicket.Douglas.Ubicaciones.Repository.UbicacionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleTSService {

    private final DetalleTSRepository repo;
    private final TicketRepository ticketRepo;
    private final UbicacionRepository ubicacionRepo;

    @Transactional
    public DetalleTSDTO nuevoDetalleTS(@Valid DetalleTSDTO dto) {
        DetalleTSEntity entity = convertirAEntity(dto);
        DetalleTSEntity entitySave = repo.save(entity);
        log.info("Nuevo detalle TS registrado: " + entitySave.getIdDetalleTS());
        return convertirADTO(entitySave);
    }

    private DetalleTSEntity convertirAEntity(@Valid DetalleTSDTO dto) {
        DetalleTSEntity entity = new DetalleTSEntity();
        entity.setNombreSoftware(dto.getNombreSoftware());
        entity.setVersion(dto.getVersion());
        entity.setUbicacion(buscarUbicacion(dto.getUbicacion()));
        entity.setTicket(buscarTicket(dto.getTicket()));
        return entity;
    }

    private DetalleTSDTO convertirADTO(DetalleTSEntity entity) {
        DetalleTSDTO dto = new DetalleTSDTO();
        dto.setIdDetalleTs(entity.getIdDetalleTS());
        dto.setNombreSoftware(entity.getNombreSoftware());
        dto.setVersion(entity.getVersion());
        dto.setUbicacion(entity.getUbicacion().getId());
        dto.setTicket(entity.getTicket().getIdTicket());
        dto.setAsunto(entity.getTicket().getAsunto());
        return dto;
    }

    public List<DetalleTSDTO> obtenerTodos() {
        List<DetalleTSEntity> datos = repo.findAll();
        return datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public DetalleTSDTO actualizarDetalleTS(Long id, @Valid DetalleTSDTO dto) {
        DetalleTSEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un detalle TS con id " + id));

        entidad.setNombreSoftware(dto.getNombreSoftware());
        entidad.setVersion(dto.getVersion());
        entidad.setUbicacion(buscarUbicacion(dto.getUbicacion()));
        entidad.setTicket(buscarTicket(dto.getTicket()));
        DetalleTSEntity datosGuardados = repo.save(entidad);
        log.info("Detalle TS con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarDetalleTS(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DetalleTSDTO> obtenerDetalleTSIdTicket(Long idTicket) {
        List<DetalleTSEntity> registros = repo.findByTicket_IdTicket(idTicket);
        return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private TicketEntity buscarTicket(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún ticket con id: " + id));
    }

    private UbicacionEntity buscarUbicacion(Long id) {
        return ubicacionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ninguna ubicación con id: " + id));
    }

    @Transactional
    public void reemplazarDetalles(TicketEntity ticket, List<DetalleTSDTO> detalles) {
        repo.deleteByTicket(ticket);

        for (DetalleTSDTO dto : detalles) {
            DetalleTSEntity entity = new DetalleTSEntity();
            entity.setNombreSoftware(dto.getNombreSoftware());
            entity.setVersion(dto.getVersion());
            entity.setUbicacion(buscarUbicacion(dto.getUbicacion()));
            entity.setTicket(ticket);
            repo.save(entity);
        }
    }
}