package iTicket.Douglas.Bitacoras.Service;


import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BitacoraService {

    private final BitacoraRepository bitacoraRepo;
    private final UsuarioRepository usuarioRepo;
    private final TicketRepository ticketRepo;

    public BitacoraService(BitacoraRepository bitacoraRepo, UsuarioRepository usuarioRepo, TicketRepository ticketRepo) {
        this.bitacoraRepo = bitacoraRepo;
        this.usuarioRepo = usuarioRepo;
        this.ticketRepo = ticketRepo;
    }

    public BitacoraDTO nuevaBitacora(@Valid BitacoraDTO dto) {

        BitacoraEntity convertirDatos = convertirAEntity(dto);
        BitacoraEntity entity = bitacoraRepo.save(convertirDatos);
        return convertirADTO(entity);
    }

    public BitacoraEntity convertirAEntity(BitacoraDTO dto){
        BitacoraEntity entity = new BitacoraEntity();
        entity.setUsuario(buscarUsuario(dto.getUsuario()));
        entity.setTicket(buscarTicket(dto.getTicket()));
        entity.setNuevoEstado(dto.getNuevoEstado());
        return entity;
    }

    public BitacoraDTO convertirADTO (BitacoraEntity entity){

        BitacoraDTO dto = new BitacoraDTO();
        dto.setIdBitacora(entity.getIdBitacora());
        dto.setUsuario(entity.getUsuario().getIdUsuario());
        dto.setNombreUsuario(entity.getUsuario().getNombreUsuario());
        dto.setTicket(entity.getTicket().getIdTicket());
        dto.setAsunto(entity.getTicket().getAsunto());
        dto.setNuevoEstado(entity.getNuevoEstado());
        dto.setFechaHora(entity.getFechaHora());
        return dto;
    }

    private UsuarioEntity buscarUsuario(Long id){
        Optional<UsuarioEntity> usuario = usuarioRepo.findById(id);
        if (usuario.isPresent()){
            return usuario.get();
        }
        log.warn("No existe ningun usuario con id: " + id);
        throw new RuntimeException("No existe ningun usuario con id: " + id);
    }

    private TicketEntity buscarTicket (Long id){
        Optional<TicketEntity> ticket = ticketRepo.findById(id);
        if (ticket.isPresent()){
            return ticket.get();
        }
        log.warn("No existe ningun ticket con id: " + id);
        throw new RuntimeException("No existe ningun ticket con id: " + id);
    }

    public List<BitacoraDTO> obtenerBitacoras() {
        List<BitacoraEntity> lista = bitacoraRepo.findAll();
        List<BitacoraDTO> dto = new ArrayList<>();
        for (BitacoraEntity entity: lista){
            dto.add(convertirADTO(entity));
        }
        return dto;
    }

    public BitacoraDTO obtenerBitacoraIdTicket(Long idTicket) {
        try {
            Optional<BitacoraEntity> entidadOpcional = bitacoraRepo.findByTicket(idTicket);
            if (entidadOpcional.isPresent()){
                return convertirADTO(entidadOpcional.get());
            }
            log.warn("No existe ninguna bitacora con ticket: " + idTicket);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrio un error al obtener la bitacora con ticket: " + idTicket);
            return null;
        }
    }
}
