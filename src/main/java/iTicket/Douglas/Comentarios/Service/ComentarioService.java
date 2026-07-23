package iTicket.Douglas.Comentarios.Service;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComentarioService {

    private final ComentarioRepository repo;

    public ComentarioService(ComentarioRepository repo) {
        this.repo = repo;
    }

    public ComentarioDTO nuevoComentario(@Valid ComentarioDTO dto){
        try{
            ComentarioEntity entity = convertirAEntity(dto);
            ComentarioEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al ingresar el comentario: " + e.getMessage());
            return null;
        }
    }

    private ComentarioEntity convertirAEntity(@Valid ComentarioDTO dto){
        ComentarioEntity objEntity = new ComentarioEntity();

        objEntity.setComentario(dto.getComentario());
        objEntity.setFechaHora(dto.getFechaHora());

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

        return objDTO;
    }

    public List<ComentarioDTO> obtenerTodo(){
        List<ComentarioEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ComentarioDTO buscarComentarioPorId(Long id){
        Optional<ComentarioEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarData(Long id){
        if(repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public ComentarioDTO actualizar(Long id, @Valid ComentarioDTO dto){
        try{
            Optional<ComentarioEntity> registroExiste = repo.findById(id);

            if(registroExiste.isPresent()){

                ComentarioEntity entidad = registroExiste.get();

                entidad.setComentario(dto.getComentario());
                entidad.setFechaHora(dto.getFechaHora());

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
