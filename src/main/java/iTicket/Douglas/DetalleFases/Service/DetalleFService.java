package iTicket.Douglas.DetalleFases.Service;

import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.DetalleFases.Repository.DetalleFRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
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
public class DetalleFService {

    private final DetalleFRepository repo;
    private final FaseRepository faseRepo;

    private DetalleFEntity convertirAEntity(DetalleFDTO dto) {
        DetalleFEntity entity = new DetalleFEntity();
        entity.setDescripcionDetalle(dto.getDescripcionDetalle());
        entity.setCompletado(dto.getCompletado());
        entity.setFase(buscarFase(dto.getFase()));
        return entity;
    }

    private DetalleFDTO convertirADTO(DetalleFEntity entity) {
        DetalleFDTO dto = new DetalleFDTO();
        dto.setIdDetalleFase(entity.getIdDetalleFase());
        dto.setDescripcionDetalle(entity.getDescripcionDetalle());
        dto.setCompletado(entity.getCompletado());
        dto.setFase(entity.getFase().getIdFase());
        dto.setNombreFase(entity.getFase().getNombreFase());
        return dto;
    }

    private FaseEntity buscarFase(Long id) {
        return faseRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ninguna fase con id: " + id));
    }

    @Transactional
    public DetalleFDTO nuevoDetalleF(@Valid DetalleFDTO dto) {
        DetalleFEntity entity = convertirAEntity(dto);
        DetalleFEntity entitySave = repo.save(entity);
        log.info("Nuevo detalle de fase registrado: " + entitySave.getIdDetalleFase());
        return convertirADTO(entitySave);
    }

    public List<DetalleFDTO> obtenerDetallesF() {
        List<DetalleFEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public DetalleFDTO actualizarDetalleF(Long id, @Valid DetalleFDTO dto) {
        DetalleFEntity entity = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un detalle de fase con id " + id));

        entity.setDescripcionDetalle(dto.getDescripcionDetalle());
        entity.setCompletado(dto.getCompletado());
        entity.setFase(buscarFase(dto.getFase()));

        DetalleFEntity datosGuardados = repo.save(entity);
        log.info("Detalle de fase con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarDetalleF(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DetalleFDTO> obtenerPorIdFase(Long fase) {
        List<DetalleFEntity> registro = repo.findByFase_idFase(fase);
        return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}