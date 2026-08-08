package iTicket.Douglas.DetalleTA.Service;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.DetalleTA.DTO.DetalleTADTO;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.DetalleTA.Repository.DetalleTARepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
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
        try {
            Optional<TicketEntity> ticketOpcional = ticketRepo.findById(dto.getIdTicket());
            if (ticketOpcional.isEmpty()) {
                log.warn("El ticket con id " + dto.getIdTicket() + " no existe");
                throw new RuntimeException("El ticket con id " + dto.getIdTicket() + " no existe");
            }

            Optional<ArticuloEntity> articuloOpcional = articuloRepo.findById(dto.getIdArticulo());
            if (articuloOpcional.isEmpty()) {
                log.warn("El artículo con id " + dto.getIdArticulo() + " no existe");
                throw new RuntimeException("El artículo con id " + dto.getIdArticulo() + " no existe");
            }

            DetalleTAEntity entity = convertirAEntity(ticketOpcional.get(), articuloOpcional.get());
            DetalleTAEntity entitySave = repo.save(entity);
            log.info("Nuevo detalle TA registrado: " + entitySave.getIdDetalleTA());
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la información del detalle: " + e.getMessage());
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException("Error al registrar el detalle de artículo", e);
        }
    }

    public List<DetalleTADTO> obtenerTodo() {
        List<DetalleTAEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleTADTO obtenerPorId(Long id) {
        Optional<DetalleTAEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    @Transactional
    public DetalleTADTO actualizarData(Long id, DetalleTADTO dto) {
        try {
            Optional<DetalleTAEntity> registroExistente = repo.findById(id);
            if (registroExistente.isEmpty()) {
                return null;
            }
            DetalleTAEntity entidad = registroExistente.get();

            if (dto.getIdTicket() != null) {
                Optional<TicketEntity> ticketOpcional = ticketRepo.findById(dto.getIdTicket());
                if (ticketOpcional.isEmpty()) {
                    log.warn("El ticket con id " + dto.getIdTicket() + " no existe");
                    return null;
                }
                entidad.setTicket(ticketOpcional.get());
            }

            if (dto.getIdArticulo() != null) {
                Optional<ArticuloEntity> articuloOpcional = articuloRepo.findById(dto.getIdArticulo());
                if (articuloOpcional.isEmpty()) {
                    log.warn("El artículo con id " + dto.getIdArticulo() + " no existe");
                    return null;
                }
                entidad.setArticulo(articuloOpcional.get());
            }

            DetalleTAEntity datosGuardados = repo.save(entidad);
            log.info("Detalle TA con id " + id + " actualizado");
            return convertirADTO(datosGuardados);
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la info: " + e.getMessage());
            return null;
        }
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
            ArticuloEntity articulo = articuloRepo.findByCodigoArticulo(codigo).orElseThrow(() -> new RuntimeException("No existe ningún artículo con código: " + codigo));

            DetalleTAEntity entity = new DetalleTAEntity();
            entity.setTicket(ticket);
            entity.setArticulo(articulo);
            repo.save(entity);
        }
    }
}
