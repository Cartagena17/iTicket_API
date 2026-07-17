package iTicket.Douglas.Bitacoras.Service;


import iTicket.Douglas.Bitacoras.DTO.BitacorasDTO;
import iTicket.Douglas.Bitacoras.Entity.BitacorasEntity;
import iTicket.Douglas.Bitacoras.Repository.BitacorasRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
//        UsuariosEntity usuario = usuarioRepo.findById(dto.getId_usuario());
//          .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + dto.getIdUsuario() + " no existe."));
        //Validar que el ticket existe en la base de datos
        //Quitar comentario cuando se unan las demas partes
//        TicketsEntity ticket = ticketsRepo.findById(dto.getId_ticket());
//            .orElseThrow(() -> new IllegalArgumentException("El ticket con ID " + dto.getIdTicket() + " no existe."));
        return convertirADTO(entidad);
    }

    private BitacorasDTO convertirADTO(BitacorasEntity respuesta){
        BitacorasDTO dto = new BitacorasDTO();
        dto.setIdBitacora(respuesta.getIdBitacora());
        dto.setFechaHora(respuesta.getFechaHora());
        dto.setNuevoEstado(respuesta.getNuevoEstado());
        //Quitar comentario cuando se unan las demas partes
//        dto.setIdUsuario(respuesta.getUsuario());
//        dto.setIdTicket(respuesta.getTicket());

        return dto;
    }

    private BitacorasEntity convertirAEntity(BitacorasDTO dto){
        //Dónde se encuentra la informacion al inicio?
        //1. Crear un objeto de tipo Entity
        BitacorasEntity entity = new BitacorasEntity();
        entity.setNuevoEstado(dto.getNuevoEstado());
        entity.setFechaHora(dto.getFechaHora());
        //Quitar el comentario cuando se unan las demas partes
//        entity.setUsuario(dto.getId_usuario());
//        entity.setTicket(dto.getId_ticket());

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

    public BitacorasDTO obtenerBitacoraPorId(Long id){
        Optional<BitacorasEntity> entidadOpcional = bitacorasRepo.findById(id);
        //Si existe se convierte a dto y se retorna, si no existe se retorna null
        return  entidadOpcional.map(this::convertirADTO).orElse(null);
    }
}
