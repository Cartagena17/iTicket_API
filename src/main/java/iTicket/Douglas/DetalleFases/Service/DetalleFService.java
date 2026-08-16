package iTicket.Douglas.DetalleFases.Service;


import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.DetalleFases.Repository.DetalleFRepository;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DetalleFService {

    private final DetalleFRepository repo;
    private final FaseRepository faseRepo;

    public DetalleFService(DetalleFRepository repo, FaseRepository faseRepo) {
        this.repo = repo;
        this.faseRepo = faseRepo;
    }

    private DetalleFEntity convertirAEntity (DetalleFDTO dto){
        DetalleFEntity entity = new DetalleFEntity();
        entity.setDescripcionDetalle(dto.getDescripcionDetalle());
        entity.setCompletado(dto.getCompletado());
        entity.setFase(buscarFase(dto.getFase()));
        return entity;
    }

    private DetalleFDTO convertirADTO(DetalleFEntity entity){
        DetalleFDTO dto = new DetalleFDTO();
        dto.setIdDetalleFase(entity.getIdDetalleFase());
        dto.setDescripcionDetalle(entity.getDescripcionDetalle());
        dto.setCompletado(entity.getCompletado());
        dto.setFase(entity.getFase().getIdFase());
        dto.setNombreFase(entity.getFase().getNombreFase());
        return dto;
    }

    private FaseEntity buscarFase(Long id){
        Optional<FaseEntity> fase = faseRepo.findById(id);
        if (fase.isPresent()){
            return fase.get();
        }
        log.warn("No existe ninguna fase con id: " + id);
        throw new RuntimeException("No existe ninguna fase con id: " + id);
    }

    public DetalleFDTO nuevoDetalleF(@Valid DetalleFDTO dto) {
        try {
            DetalleFEntity entity = convertirAEntity(dto);
            DetalleFEntity entitySave = repo.save(entity);
            return convertirADTO(entity);
        }
        catch (Exception e){
            log.error("Error al registrar el detalle de la fase " + e.getMessage());
            throw new RuntimeException("Error al registrar el detalle de la fase");
        }
    }

    public List<DetalleFDTO> obtenerDetallesF() {
        List<DetalleFEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleFDTO actualizarDetalleF(Long id, @Valid DetalleFDTO dto) {
        try{
            Optional<DetalleFEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                DetalleFEntity entity = entidadOpcional.get();
                entity.setDescripcionDetalle(dto.getDescripcionDetalle());
                entity.setCompletado(dto.getCompletado());
                entity.setFase(buscarFase(dto.getFase()));

                DetalleFEntity datosGuardados = repo.save(entity);
                return convertirADTO(datosGuardados);
            }
            return null;
        }
        catch (Exception e){
            log.error("Ocurrio un error al procesar la información del detalle de fase");
            return null;
        }
    }

    public boolean eliminarDetalleF(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DetalleFDTO> obtenerPorIdFase(Long fase) {
        try {
            List<DetalleFEntity> registro = repo.findByFase_idFase(fase);
            if (registro != null && !registro.isEmpty()){
                return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún detalle de fase con id: " + fase);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrió un error durante el proceso de obtención");
            e.printStackTrace();
            return null;
        }
    }
}
