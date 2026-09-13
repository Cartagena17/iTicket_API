package iTicket.Douglas.Comentarios.Service;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.MultimediaComentario.Service.MultimediaComentarioService;
import iTicket.Douglas.Notificaciones.Event.ComentarioCreadoEvent;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComentarioService {

    private final ComentarioRepository repo;
    private final TicketRepository ticketsRepo;
    private final UsuarioRepository usuarioRepo;
    private final MultimediaComentarioService multimediaComentarioService;
    private final ApplicationEventPublisher eventos;

    @Transactional
    public ComentarioDTO nuevoComentario(@Valid ComentarioDTO dto) {
        TicketEntity ticket = ticketsRepo.findById(dto.getIdTicket())
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID: " + dto.getIdTicket() + " no existe"));
        if (!usuarioRepo.existsById(dto.getIdUsuarioComentario())) {
            throw new RecursoNoEncontradoException("El usuario con ID: " + dto.getIdUsuarioComentario() + " no existe");
        }

        ComentarioEntity entity = convertirAEntity(dto);
        ComentarioEntity entitySave = repo.save(entity);
        log.info("Nuevo comentario registrado: " + entitySave.getId());

        notificarNuevoComentario(ticket, dto.getIdUsuarioComentario());

        return convertirADTO(entitySave);
    }

    //Si comenta el creador, se notifica al tecnico asignado (si no hay tecnico asignado, no se notifica a nadie)
    //Si comenta el tecnico asignado, se notifica al creador
    private void notificarNuevoComentario(TicketEntity ticket, Long idAutorComentario) {
        Long idCreador = ticket.getCreador() != null ? ticket.getCreador().getIdUsuario() : null;
        Long idTecnico = ticket.getTecnicoAsignado() != null ? ticket.getTecnicoAsignado().getIdUsuario() : null;

        Long idDestinatario = null;
        if (idCreador != null && idCreador.equals(idAutorComentario)) {
            idDestinatario = idTecnico;
        } else if (idTecnico != null && idTecnico.equals(idAutorComentario)) {
            idDestinatario = idCreador;
        }

        if (idDestinatario != null) {
            eventos.publishEvent(new ComentarioCreadoEvent(ticket.getIdTicket(), idDestinatario));
        }
    }

    private ComentarioEntity convertirAEntity(@Valid ComentarioDTO dto) {
        ComentarioEntity objEntity = new ComentarioEntity();
        objEntity.setComentario(dto.getComentario());

        TicketEntity ticket = new TicketEntity();
        ticket.setIdTicket(dto.getIdTicket());
        objEntity.setTicket(ticket);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(dto.getIdUsuarioComentario());
        objEntity.setUsuario(usuario);

        return objEntity;
    }

    private ComentarioDTO convertirADTO(@Valid ComentarioEntity entity) {
        ComentarioDTO objDTO = new ComentarioDTO();

        objDTO.setId(entity.getId());
        objDTO.setComentario(entity.getComentario());
        objDTO.setFechaHora(entity.getFechaHora());
        objDTO.setIdTicket(entity.getTicket().getIdTicket());
        objDTO.setIdUsuarioComentario(entity.getUsuario().getIdUsuario());

        usuarioRepo.findById(entity.getUsuario().getIdUsuario()).ifPresent(usuario -> objDTO.setCorreoUsuario(usuario.getCorreo()));

        List<MultimediaComentarioEntity> multimedia = entity.getMultimediaComentarios();
        if (multimedia != null) {
            objDTO.setMultimediaUrls(multimedia.stream().map(MultimediaComentarioEntity::getMultimediaUrl).collect(Collectors.toList()));
        }

        return objDTO;
    }

    public List<ComentarioDTO> obtenerTodo() {
        List<ComentarioEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ComentarioDTO> obtenerComentariosPorTicket(Long idTicket) {
        if (!ticketsRepo.existsById(idTicket)) {
            throw new RecursoNoEncontradoException("El ticket con ID: " + idTicket + " no existe");
        }
        List<ComentarioEntity> data = repo.findByTicket_IdTicketOrderByFechaHoraAsc(idTicket);
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ComentarioDTO buscarComentarioPorId(Long id) {
        ComentarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un comentario con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarData(Long id, Long idUsuarioSolicitante) {
        Optional<ComentarioEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        ComentarioEntity entidad = entidadOpcional.get();

        //Solo el autor del comentario puede eliminarlo
        if (!entidad.getUsuario().getIdUsuario().equals(idUsuarioSolicitante)) {
            throw new OperacionInvalidaException("Solo el autor del comentario puede eliminarlo");
        }

        List<MultimediaComentarioEntity> multimedia = entidad.getMultimediaComentarios();
        if (multimedia != null) {
            //Se elimina primero la multimedia asociada (y sus imagenes en Cloudinary) para no dejar archivos huerfanos
            multimedia.forEach(m -> multimediaComentarioService.eliminarData(m.getId()));
        }

        repo.deleteById(id);
        return true;
    }

    @Transactional
    public ComentarioDTO actualizar(Long id, @Valid ComentarioDTO dto) {
        ComentarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un comentario con id " + id));

        entidad.setComentario(dto.getComentario());

        TicketEntity ticket = new TicketEntity();
        ticket.setIdTicket(dto.getIdTicket());
        entidad.setTicket(ticket);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(dto.getIdUsuarioComentario());
        entidad.setUsuario(usuario);

        ComentarioEntity datosGuardados = repo.save(entidad);
        log.info("Comentario con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }
}