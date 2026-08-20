package iTicket.Douglas.Bitacoras.Service;

import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BitacoraService {

    private final BitacoraRepository bitacoraRepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    public BitacoraDTO nuevaBitacora(@Valid BitacoraDTO dto) {
        BitacoraEntity entity = convertirAEntity(dto);
        BitacoraEntity entitySave = bitacoraRepo.save(entity);
        log.info("Nueva bitácora registrada: " + entitySave.getIdBitacora());
        return convertirADTO(entitySave);
    }

    public BitacoraEntity convertirAEntity(BitacoraDTO dto) {
        BitacoraEntity entity = new BitacoraEntity();
        entity.setUsuario(buscarUsuario(dto.getUsuario()));
        entity.setIdTicket(dto.getIdTicket());
        entity.setCodigoTicket(dto.getCodigoTicket());
        entity.setAsuntoTicket(dto.getAsuntoTicket());
        entity.setNuevoEstado(dto.getNuevoEstado());
        return entity;
    }

    public BitacoraDTO convertirADTO(BitacoraEntity entity) {
        BitacoraDTO dto = new BitacoraDTO();
        dto.setIdBitacora(entity.getIdBitacora());
        dto.setUsuario(entity.getUsuario().getIdUsuario());
        dto.setNombreUsuario(entity.getUsuario().getNombreUsuario());
        dto.setCorreoUsuario(entity.getUsuario().getCorreo());
        dto.setIdTicket(entity.getIdTicket());
        dto.setCodigoTicket(entity.getCodigoTicket());
        dto.setAsuntoTicket(entity.getAsuntoTicket());
        dto.setNuevoEstado(entity.getNuevoEstado());
        dto.setFechaHora(entity.getFechaHora());
        return dto;
    }

    private UsuarioEntity buscarUsuario(Long id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún usuario con id: " + id));
    }

    public List<BitacoraDTO> obtenerBitacoras() {
        List<BitacoraEntity> lista = bitacoraRepo.findAll();
        List<BitacoraDTO> dto = new ArrayList<>();
        for (BitacoraEntity entity : lista) {
            dto.add(convertirADTO(entity));
        }
        return dto;
    }

    public List<BitacoraDTO> obtenerBitacorasIdTicket(Long idTicket) {
        List<BitacoraEntity> lista = bitacoraRepo.findByIdTicketOrderByFechaHoraAsc(idTicket);
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}