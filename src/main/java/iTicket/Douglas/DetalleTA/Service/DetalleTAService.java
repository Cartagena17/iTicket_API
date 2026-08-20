package iTicket.Douglas.DetalleTA.Service;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.DetalleTA.DTO.DetalleTADTO;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.DetalleTA.Repository.DetalleTARepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
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
public class DetalleTAService {

    private final DetalleTARepository repo;
    private final TicketRepository ticketRepo;
    private final ArticuloRepository articuloRepo;

    @Transactional
    public DetalleTADTO nuevoDetalle(@Valid DetalleTADTO dto) {
        TicketEntity ticket = ticketRepo.findById(dto.getIdTicket())
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con id " + dto.getIdTicket() + " no existe"));

        ArticuloEntity articulo = articuloRepo.findById(dto.getIdArticulo())
                .orElseThrow(() -> new RecursoNoEncontradoException("El artículo con id " + dto.getIdArticulo() + " no existe"));

        DetalleTAEntity entity = convertirAEntity(ticket, articulo);
        DetalleTAEntity entitySave = repo.save(entity);
        log.info("Nuevo detalle TA registrado: " + entitySave.getIdDetalleTA());
        return convertirADTO(entitySave);
    }

    public List<DetalleTADTO> obtenerTodo() {
        List<DetalleTAEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleTADTO obtenerPorId(Long id) {
        DetalleTAEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un detalle TA con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public DetalleTADTO actualizarData(Long id, DetalleTADTO dto) {
        DetalleTAEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un detalle TA con id " + id));

        if (dto.getIdTicket() != null) {
            TicketEntity ticket = ticketRepo.findById(dto.getIdTicket())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con id " + dto.getIdTicket() + " no existe"));
            entidad.setTicket(ticket);
        }

        if (dto.getIdArticulo() != null) {
            ArticuloEntity articulo = articuloRepo.findById(dto.getIdArticulo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El artículo con id " + dto.getIdArticulo() + " no existe"));
            entidad.setArticulo(articulo);
        }

        DetalleTAEntity datosGuardados = repo.save(entidad);
        log.info("Detalle TA con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarDetalle(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private DetalleTAEntity convertirAEntity(TicketEntity ticket, ArticuloEntity articulo) {
        DetalleTAEntity objEntity = new DetalleTAEntity();
        objEntity.setTicket(ticket);
        objEntity.setArticulo(articulo);
        return objEntity;
    }

    private DetalleTADTO convertirADTO(DetalleTAEntity entity) {
        DetalleTADTO objDTO = new DetalleTADTO();
        objDTO.setIdDetalleTA(entity.getIdDetalleTA());
        objDTO.setIdTicket(entity.getTicket().getIdTicket());
        objDTO.setCodigo(entity.getTicket().getCodigo());
        objDTO.setIdArticulo(entity.getArticulo().getIdArticulo());
        objDTO.setCodigoArticulo(entity.getArticulo().getCodigoArticulo());
        return objDTO;
    }

    @Transactional
    public void reemplazarDetalles(TicketEntity ticket, List<String> codigosArticulos) {
        repo.deleteByTicket(ticket);

        for (String codigo : codigosArticulos) {
            ArticuloEntity articulo = articuloRepo.findByCodigoArticulo(codigo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún artículo con código: " + codigo));

            DetalleTAEntity entity = new DetalleTAEntity();
            entity.setTicket(ticket);
            entity.setArticulo(articulo);
            repo.save(entity);
        }
    }
}