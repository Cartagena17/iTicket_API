package iTicket.Douglas.Tickets.Service;

import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.Tickets.DTO.TicketAsignacionDTO;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import iTicket.Douglas.Tickets.DTO.TicketResolucionDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketService {

    private final TicketRepository repo;
    private final DepartamentoRepository departamentosRepo;
    private final UsuarioRepository usuariosRepo;

    public TicketService(TicketRepository repo, DepartamentoRepository departamentosRepo, UsuarioRepository usuariosRepo) {
        this.repo = repo;
        this.departamentosRepo = departamentosRepo;
        this.usuariosRepo = usuariosRepo;
    }

    public TicketDTO nuevoTicket(@Valid TicketDTO dto){
        try {
            TicketEntity entity = convertirAEntity(dto);
            TicketEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al registrar el ticket " + e.getMessage());
            throw new RuntimeException("Error al registrar el ticket");
        }
    }

    public List<TicketDTO> obtenerTodo() {
        List<TicketEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TicketDTO buscarTicket(Long id) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public boolean eliminarData(Long id){
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public TicketDTO actualizarTicket(Long id, @Valid TicketDTO dto){
        try {
            Optional<TicketEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                TicketEntity entidad = entidadOpcional.get();

                entidad.setAsunto(dto.getAsunto());
                entidad.setDescripcion(dto.getDescripcion());
                //Busca el objeto Departamento antes de asignarlo
                entidad.setDepartamento(buscarDepartamento(dto.getDepartamento()));
                entidad.setDescripcionFalla(dto.getDescripcionFalla());
                entidad.setDescripcionSolucion(dto.getDescripcionSolucion());
                entidad.setFechaVencimiento(dto.getFechaVencimiento());
                //Busca el objeto Usuario antes de asignarlo
                entidad.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
                entidad.setPrioridad(dto.getPrioridad());
                entidad.setTipoTicket(dto.getTipoTicket());

                TicketEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops, ocurrió un error al procesar la información");
            return null;
        }
    }

    public TicketDTO buscarPorCodigo(String codigo) {
        try {
            Optional<TicketEntity> registro = repo.findByCodigo(codigo);
            if (registro.isPresent()){
                return convertirADTO(registro.get());
            }
            log.warn("No existe ningún ticket con código: " + codigo);
            return null;
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return null;
        }
    }

    public List<TicketDTO> buscarPorAsunto(String asunto) {
        try {
            List<TicketEntity> registros = repo.findByAsuntoContainingIgnoreCase(asunto);
            if (!registros.isEmpty()){
                return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún ticket con asunto: " + asunto);
            return Collections.emptyList();
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return Collections.emptyList();
        }
    }

    public List<TicketDTO> buscarPorPrioridad(String prioridad) {
        try {
            List<TicketEntity> registros = repo.findByPrioridad(prioridad);
            if (!registros.isEmpty()){
                return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún ticket con prioridad: " + prioridad);
            return null;
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return null;
        }
    }

    //Método reutilizable para obtener un objeto Departamento
    private DepartamentoEntity buscarDepartamento(Long id) {
        Optional<DepartamentoEntity> departamentoOp = departamentosRepo.findById(id);
        if (departamentoOp.isPresent()){
            return departamentoOp.get();
        }
        log.warn("No existe ningún departamento con ID: " + id);
        throw new RuntimeException("No existe ningún departamento con ID: " + id);
    }

    //Método reutilizable para obtener un objeto Usuario
    private UsuarioEntity buscarTecnico(Long id) {
        Optional<UsuarioEntity> tecnicoOp = usuariosRepo.findById(id);
        if (tecnicoOp.isPresent()){
            return tecnicoOp.get();
        }
        log.warn("No existe ningún usuario con ID: " + id);
        throw new RuntimeException("No existe ningún usuario con ID: " + id);
    }

    //Método para convertir DTO a Entity
    private TicketEntity convertirAEntity(@Valid TicketDTO dto) {
        TicketEntity objEntity = new TicketEntity();
        objEntity.setCodigo(dto.getCodigo());
        objEntity.setAsunto(dto.getAsunto());
        objEntity.setDescripcion(dto.getDescripcion());
        objEntity.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        objEntity.setDescripcionFalla(dto.getDescripcionFalla());
        objEntity.setDescripcionSolucion(dto.getDescripcionSolucion());
        objEntity.setFechaVencimiento(dto.getFechaVencimiento());
        objEntity.setPrioridad(dto.getPrioridad());
        objEntity.setTipoTicket(dto.getTipoTicket());
        //Verificar si el id del t'ecnico viene en el dto(según la lógica, no debería venir al crearse un ticket)
        if (dto.getTecnicoAsignado() != null) {
            objEntity.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
        }
        return objEntity;
    }

    //Método para convertir Entity a DTO
    private TicketDTO convertirADTO(@Valid TicketEntity entity) {
        TicketDTO objDTO = new TicketDTO();

        objDTO.setIdTicket(entity.getIdTicket());
        objDTO.setCodigo(entity.getCodigo());
        objDTO.setAsunto(entity.getAsunto());
        objDTO.setDescripcion(entity.getDescripcion());
        objDTO.setDepartamento(entity.getDepartamento().getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getDepartamento().getNombreDepartamento());
        objDTO.setDescripcionFalla(entity.getDescripcionFalla());
        objDTO.setDescripcionSolucion(entity.getDescripcionSolucion());
        objDTO.setFechaVencimiento(entity.getFechaVencimiento());
        objDTO.setPrioridad(entity.getPrioridad());
        objDTO.setTipoTicket(entity.getTipoTicket());
        if (entity.getTecnicoAsignado() != null) {
            objDTO.setTecnicoAsignado(entity.getTecnicoAsignado().getIdUsuario());
            objDTO.setNombreTecnico(entity.getTecnicoAsignado().getNombreUsuario());
        }
        return objDTO;
    }


    public boolean asignarTicket(Long id, @Valid TicketAsignacionDTO dto) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);

        if (entidadOpcional.isPresent()){
            TicketEntity ticket = entidadOpcional.get();
            ticket.setFechaVencimiento(dto.getFechaVencimiento());
            ticket.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
            ticket.setPrioridad(dto.getPrioridad());

            repo.save(ticket);
            return true;
        }
        return false;
    }

    public boolean reporteTicket(Long id, @Valid TicketResolucionDTO dto) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);

        if (entidadOpcional.isPresent()){
            TicketEntity ticket = entidadOpcional.get();
            ticket.setDescripcionFalla(dto.getDescripcionFalla());
            ticket.setDescripcionSolucion(dto.getDescripcionSolucion());
            repo.save(ticket);
            return true;
        }
        return false;
    }
}
