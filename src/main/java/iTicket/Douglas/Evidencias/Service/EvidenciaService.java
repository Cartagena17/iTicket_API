package iTicket.Douglas.Evidencias.Service;

import iTicket.Douglas.Evidencias.DTO.EvidenciaDTO;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Evidencias.Repository.EvidenciaRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Utils.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenciaService {

    private final EvidenciaRepository repo;
    private final TicketRepository ticketsRepo;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public EvidenciaDTO subirEvidencia(MultipartFile archivo, Long idTicket) {
        if (!ticketsRepo.existsById(idTicket)) {
            throw new RecursoNoEncontradoException("El ticket con ID: " + idTicket + " no existe");
        }

        String urlPublica = cloudinaryService.subirImagen(archivo, "iticket/evidencias");

        EvidenciaDTO dto = new EvidenciaDTO();
        dto.setEvidenciaUrl(urlPublica);
        dto.setTicket(idTicket);

        return nuevaEvidencia(dto);
    }

    @Transactional
    public EvidenciaDTO nuevaEvidencia(@Valid EvidenciaDTO dto) {
        EvidenciaEntity entity = convertirAEntity(dto);
        EvidenciaEntity entitySave = repo.save(entity);
        log.info("Nueva evidencia registrada: " + entitySave.getIdEvidencia());
        return convertirADTO(entitySave);
    }

    public List<EvidenciaDTO> obtenerEvidenciasPorTicket(Long idTicket) {
        TicketEntity ticket = ticketsRepo.findById(idTicket)
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID: " + idTicket + " no existe"));

        List<EvidenciaEntity> data = repo.findByTicket(ticket);
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean eliminarData(Long id) {
        Optional<EvidenciaEntity> entidad = repo.findById(id);
        if (entidad.isPresent()) {
            String publicId = cloudinaryService.extraerPublicId(entidad.get().getEvidenciaUrl(), "iticket/evidencias");
            cloudinaryService.eliminarImagen(publicId);
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<EvidenciaDTO> obtenerTodo() {
        List<EvidenciaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public EvidenciaDTO buscarEvidencia(Long id) {
        EvidenciaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una evidencia con id " + id));
        return convertirADTO(entidad);
    }

    private EvidenciaEntity convertirAEntity(@Valid EvidenciaDTO dto) {
        EvidenciaEntity objEntity = new EvidenciaEntity();
        objEntity.setEvidenciaUrl(dto.getEvidenciaUrl());
        TicketEntity ticket = ticketsRepo.getReferenceById(dto.getTicket());
        objEntity.setTicket(ticket);
        return objEntity;
    }

    private EvidenciaDTO convertirADTO(@Valid EvidenciaEntity entity) {
        EvidenciaDTO objDTO = new EvidenciaDTO();
        objDTO.setIdEvidencia(entity.getIdEvidencia());
        objDTO.setEvidenciaUrl(entity.getEvidenciaUrl());
        if (entity.getTicket() != null) {
            objDTO.setTicket(entity.getTicket().getIdTicket());
        }
        return objDTO;
    }
}
