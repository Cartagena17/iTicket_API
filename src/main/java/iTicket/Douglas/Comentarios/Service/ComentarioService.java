package iTicket.Douglas.Comentarios.Service;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComentarioService {

    private final ComentarioRepository repo;
    private final TicketRepository ticketsRepo; //Para validar que el ticket exista
    private final UsuarioRepository usuarioRepo; //Para validar que el usuario exista

    @Transactional
    public ComentarioDTO nuevoComentario(@Valid ComentarioDTO dto){
        try{
            if (!ticketsRepo.existsById(dto.getIdTicket())) {
                throw new RuntimeException("El ticket con ID: " + dto.getIdTicket() + " no existe");
            }
            if (!usuarioRepo.existsById(dto.getIdUsuarioComentario())) {
                throw new RuntimeException("El usuario con ID: " + dto.getIdUsuarioComentario() + " no existe");
            }

            ComentarioEntity entity = convertirAEntity(dto);
            ComentarioEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al ingresar el comentario: " + e.getMessage());
            if (e instanceof RuntimeException re) throw re;
            return null;
        }
    }

    private ComentarioEntity convertirAEntity(@Valid ComentarioDTO dto){
        ComentarioEntity objEntity = new ComentarioEntity();

        objEntity.setComentario(dto.getComentario());
        //fechaHora se genera automaticamente (@CreationTimestamp), no se recibe del cliente

        TicketEntity ticket = new TicketEntity();
        ticket.setIdTicket(dto.getIdTicket());
        objEntity.setTicket(ticket);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(dto.getIdUsuarioComentario());
        objEntity.setUsuario(usuario);

        return objEntity;
    }

    private ComentarioDTO convertirADTO(@Valid ComentarioEntity entity){
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

    public List<ComentarioDTO> obtenerTodo(){
        List<ComentarioEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    //Metodo para obtener los comentarios de un ticket, del mas antiguo al mas reciente
    public List<ComentarioDTO> obtenerComentariosPorTicket(Long idTicket) {
        if (!ticketsRepo.existsById(idTicket)) {
            throw new RuntimeException("El ticket con ID: " + idTicket + " no existe");
        }
        List<ComentarioEntity> data = repo.findByTicket_IdTicketOrderByFechaHoraAsc(idTicket);
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ComentarioDTO buscarComentarioPorId(Long id){
        Optional<ComentarioEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    @Transactional
    public boolean eliminarData(Long id){
        if(repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ComentarioDTO actualizar(Long id, @Valid ComentarioDTO dto){
        try{
            Optional<ComentarioEntity> registroExiste = repo.findById(id);

            if(registroExiste.isPresent()){

                ComentarioEntity entidad = registroExiste.get();

                entidad.setComentario(dto.getComentario());

                TicketEntity ticket = new TicketEntity();
                ticket.setIdTicket(dto.getIdTicket());
                entidad.setTicket(ticket);

                UsuarioEntity usuario = new UsuarioEntity();
                usuario.setIdUsuario(dto.getIdUsuarioComentario());
                entidad.setUsuario(usuario);

                ComentarioEntity datosGuardados = repo.save(entidad);

                return convertirADTO(datosGuardados);
            }

            return null;

        }catch (Exception e){
            log.error("Oops ocurrio un error al procesar la informacion");
            return null;
        }
    }
}
