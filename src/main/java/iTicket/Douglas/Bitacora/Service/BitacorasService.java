package iTicket.Douglas.Bitacora.Service;


import iTicket.Douglas.Bitacora.DTO.BitacorasDTO;
import iTicket.Douglas.Bitacora.Entity.BitacorasEntity;
import iTicket.Douglas.Bitacora.Repository.BitacorasRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BitacorasService {

    private final BitacorasRepository bitacorasRepo;
    //Quitar comentario cuando se unan las demas partes
//    private final UsuariosRepository usuarioRepo;
//    private final TicketsRepository ticketsRepo;


    public BitacorasService(BitacorasRepository bitacorasRepo) {
        this.bitacorasRepo = bitacorasRepo;
//        this.usuarioRepo = usuarioRepo;
//        this.ticketsRepo = ticketsRepo;
        //Agregar esto a los parametros del constructor cuando se unan las demas partes
        // , UsuariosRepository usuarioRepo, TicketsRepository ticketsRepo
    }

    public BitacorasDTO nuevaBitacora(@Valid BitacorasDTO dto){
        //Convertir a Entity
        BitacorasEntity convertirDatos = convertirAEntity(dto);
        //Realizar la insercion a la base de datos
        BitacorasEntity entidad = bitacorasRepo.save(convertirDatos);
        //Valida que el usuario exista en la base de datos
        //Quitar comentario cuando se unan las demas partes
//        UsuariosEntity usuario = usuarioRepo.findById(dto.getUsuario());
//          .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + dto.getIdUsuario() + " no existe."));
        //Validar que el ticket existe en la base de datos
        //Quitar comentario cuando se unan las demas partes
//        TicketsEntity ticket = ticketsRepo.findById(dto.getTicket());
//            .orElseThrow(() -> new IllegalArgumentException("El ticket con ID " + dto.getIdTicket() + " no existe."));
        return convertirADTO(entidad);
    }

    private BitacorasDTO convertirADTO(BitacorasEntity respuesta){
        BitacorasDTO dto = new BitacorasDTO();
        dto.setIdBitacora(respuesta.getIdBitacora());
        dto.setFechaHora(respuesta.getFechaHora());
        dto.setNuevoEstado(respuesta.getNuevoEstado());
        //Quitar comentario cuando se unan las demas partes
//        dto.setUsuario(respuesta.getUsuario().getIdUsuario());
//        dto.setTicket(respuesta.getTicket().getIdTicket());

        return dto;
    }

    private BitacorasEntity convertirAEntity(BitacorasDTO dto){
        //Dónde se encuentra la informacion al inicio?
        //1. Crear un objeto de tipo Entity
        BitacorasEntity entity = new BitacorasEntity();
        entity.setNuevoEstado(dto.getNuevoEstado());
        entity.setFechaHora(dto.getFechaHora());
        //Quitar el comentario cuando se unan las demas partes
//        entity.setUsuario(dto.getUsuario());
//        entity.setTicket(dto.getTicket());

        return entity;
    }

    public List<BitacorasDTO> obtenerBitacoras(){
        List<BitacorasEntity> entidades = bitacorasRepo.findAll();
        //Convertir entidades a dto
        List<BitacorasDTO> dtos = new ArrayList<>();
        for (BitacorasEntity entity: entidades){
            dtos.add(convertirADTO(entity));
        }
        return dtos;
    }

    //Metodo para obtener bitacoras por id de ticket, quitar comentario cuando se unan las demas partes
//    public BitacorasDTO obtenerBitacoraIdTicket(Long ticket){
//       try {
//           Optional<BitacorasEntity> entidadOpcional = bitacorasRepo.findByIdTicket(ticket);
//           if (entidadOpcional != null){
//               return convertirADTO(entidadOpcional.get());
//           }
//           log.warn("No existe ninguna bitacora con asunto de ticket: " + ticket);
//           return null;
//       }
//       catch (Exception e){
//           log.error("Ocurrio un error al obtener la bitacora con asunto de ticket: " + ticket);
//           return null;
//       }
//    }
}
