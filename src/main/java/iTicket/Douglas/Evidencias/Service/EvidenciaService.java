package iTicket.Douglas.Evidencias.Service;

import iTicket.Douglas.Evidencias.DTO.EvidenciaDTO;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Evidencias.Repository.EvidenciaRepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EvidenciaService {

    //Inyectar dependencias
    private final EvidenciaRepository repo;
    private final TicketRepository ticketsRepo; //Para buscar si el ticket existe

    public EvidenciaService(EvidenciaRepository repo, TicketRepository ticketsrepo) {
        this.repo = repo;
        this.ticketsRepo = ticketsrepo;
    }

    //Método para crear evidencias
    public EvidenciaDTO nuevaEvidencia(@Valid EvidenciaDTO dto){
        try {
            EvidenciaEntity entity = convertirAEntity(dto);
            //Guardar en la base
            EvidenciaEntity entitySave = repo.save(entity);
            //Devolver e de entitySave como DTO
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al registrar la evidencia " + e.getMessage());
            throw new RuntimeException("Error al registrar la evidencia");
        }
    }

    //Método para obtener evidencias por Ticket
    public List<EvidenciaDTO> obtenerEvidenciasPorTicket(Long idTicket) {
        //Buscar el ticket relacionado en la base
        Optional<TicketEntity> entidadOpcional = ticketsRepo.findById(idTicket);

        if (entidadOpcional.isEmpty()) {
            throw new RuntimeException("El ticket con ID: " + idTicket + " no existe");
        }
        TicketEntity ticket = entidadOpcional.get();
        //Buscar evidencias asociadas al objeto ticket
        List<EvidenciaEntity> data = repo.findByTicket(ticket);

        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
        //.map le da los atributos que vienen del entity a los atributos del DTO
    }

    //Método para eliminar
    public boolean eliminarData(Long id) {
        if (repo.existsById(id)){ //Validar si existe
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    //Método para obtener todas las evidencias
    public List<EvidenciaDTO> obtenerTodo() {
        List<EvidenciaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    //Método para obtener evidencias por su id
    public EvidenciaDTO buscarEvidencia(Long id) {
        Optional<EvidenciaEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    //Método para convertir los DTO a Entity
    private EvidenciaEntity convertirAEntity(@Valid EvidenciaDTO dto) {
        EvidenciaEntity objEntity = new EvidenciaEntity();

        objEntity.setEvidenciaUrl(dto.getEvidenciaUrl());

        //getReferenceById sirve para evitar hacer un SELECT completo y retornar un proxy solo con el id
        TicketEntity ticket = ticketsRepo.getReferenceById(dto.getTicket());

        objEntity.setTicket(ticket);
        return objEntity;
    }

    //Método para convertir Entity a DTO
    private EvidenciaDTO convertirADTO(@Valid EvidenciaEntity entity) {
        EvidenciaDTO objDTO = new EvidenciaDTO();
        objDTO.setIdEvidencia(entity.getIdEvidencia());
        objDTO.setEvidenciaUrl(entity.getEvidenciaUrl());

        //Para traer el objeto ticket guardado en el entity y solo tomar el id del ticket
        if (entity.getTicket() != null) {
            objDTO.setTicket(entity.getTicket().getIdTicket());
        }
        return objDTO;
    }

}
