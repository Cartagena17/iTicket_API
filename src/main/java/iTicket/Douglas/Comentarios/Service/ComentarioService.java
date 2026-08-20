package iTicket.Douglas.Comentarios.Service;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComentarioService {

    private final ComentarioRepository repo;
    private final TicketRepository ticketsRepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    public ComentarioDTO nuevoComentario(@Valid ComentarioDTO dto) {
        if (!ticketsRepo.existsById(dto.getIdTicket())) {
            throw new RecursoNoEncontradoException("El ticket con ID: " + dto.getIdTicket() + " no existe");
        }
        if (!usuarioRepo.existsById(dto.getIdUsuarioComentario())) {
            throw new RecursoNoEncontradoException("El usuario con ID: " + dto.getIdUsuarioComentario() + " no existe");
        }

        ComentarioEntity entity = convertirAEntity(dto);
        ComentarioEntity entitySave = repo.save(entity);
        log.info("Nuevo comentario registrado: " + entitySave.getId());
        return convertirADTO(entitySave);
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
    public boolean eliminarData(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
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