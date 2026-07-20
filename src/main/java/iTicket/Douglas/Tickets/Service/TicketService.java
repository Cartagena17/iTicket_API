package iTicket.Douglas.Tickets.Service;

import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.Prioridades.Entity.PrioridadEntity;
import iTicket.Douglas.Prioridades.Repository.PrioridadRepository;
import iTicket.Douglas.Tickets.DTO.TicketDTO;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketService { //Captura el DTO y lo convierte a Entity  y vicevera

    //Inyectar dependencias
    private final TicketRepository repo;
    private final PrioridadRepository prioridadesRepo;
    private final DepartamentoRepository departamentosRepo;
    private final UsuarioRepository usuariosRepo;
    //private final BitacoraRepository bitacoraRepo;

    //Constructor
    public TicketService(TicketRepository repo, PrioridadRepository prioridadesRepo, DepartamentoRepository departamentosRepo, UsuarioRepository usuariosRepo/*,BitacoraRepository bitacoraRepo*/) {
        this.repo = repo;
        this.prioridadesRepo = prioridadesRepo;
        this.departamentosRepo = departamentosRepo;
        this.usuariosRepo = usuariosRepo;
        //this.bitacoraRepo = bitacoraRepo;
    }

    //Método para crear tickets
    @Transactional
    public TicketDTO nuevoTicket(@Valid TicketDTO dto){
        try {
            //Convertir DTO a Entity
            TicketEntity entity = convertirAEntity(dto);
            //entity.setCodigo(generarCodigo());
            //Guardar en la base
            TicketEntity entitySave = repo.save(entity);
            //Devolver respuesta de entitySave como DTO
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al registrar el ticket " + e.getMessage()); //Enviar el error a la consola del servidor
            throw new RuntimeException("Error al registrar el ticket");
        }
    }

    //Método para traer todos los tickets de la base
    public List<TicketDTO> obtenerTodo() {
        //Retorna una lista de TicketDTO, pero recive un entity ya que viene de la base
        //Convertir entity a dto
        List<TicketEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
        //.map le da los atributos que vienen del entity a los atributos del DTO
    }

    //Método para obtener tickets por id
    public TicketDTO buscarTicket(Long id) {
        //La entidad puede existir o puede no hacerlo
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    //Método para eliminar tickets
    public boolean eliminarData(Long id){
        //Verficiar si el registro existe
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false; //Retornamos false si el valor no se encontró
    }

    //Método para actualizar tickets
    public TicketDTO actualizarTicket(Long id, @Valid TicketDTO dto){
        try {
            //Verificar si el ticket existe
            Optional<TicketEntity> entidadOpcional = repo.findById(id);
            //Verificar si contiene valores
            if (entidadOpcional.isPresent()){
                //Crear nuevo entity
                TicketEntity entidad = entidadOpcional.get();

                //Convertir y asignar los nuevos valores a la entidad
                entidad.setCodigo(dto.getCodigo());
                entidad.setAsunto(dto.getAsunto());
                entidad.setDescripcion(dto.getDescripcion());
                //Busca el objeto Departamento antes de asignarlo
                entidad.setDepartamento(buscarDepartamento(dto.getDepartamento()));
                entidad.setDescripcionFalla(dto.getDescripcionFalla());
                entidad.setDescripcionSolucion(dto.getDescripcionSolucion());
                entidad.setFechaVencimiento(dto.getFechaVencimiento());
                //Busca el objeto Usuario antes de asignarlo
                entidad.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
                //Busca el objeto Prioridad completo antes de asignarlo
                entidad.setPrioridad(buscarPrioridad(dto.getPrioridad()));

                //Actualizar datos
                TicketEntity datosGuardados = repo.save(entidad);
                //Retornar
                return convertirADTO(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops, ocurrió un error al procesar la información");
            return null;
        }
    }

    //Método reutilizable para obtener un objeto Prioridad
    private PrioridadEntity buscarPrioridad(Long id) {
        Optional<PrioridadEntity> prioridadOp = prioridadesRepo.findById(id);
        if (prioridadOp.isPresent()){
            return prioridadOp.get();
        }
        log.warn("No existe ninguna prioridad con ID: " + id);
        throw new RuntimeException("No existe ninguna prioridad con ID: " + id);
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
        if (dto.getTecnicoAsignado() != null) {
            objEntity.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
        }
        if (dto.getPrioridad() != null) {
            objEntity.setPrioridad(buscarPrioridad(dto.getPrioridad()));
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
        //No se mostrará el id del departamento, sino, el nombre
        objDTO.setNombreDepartamento(entity.getDepartamento().getNombreDepartamento());
        objDTO.setDescripcionFalla(entity.getDescripcionFalla());
        objDTO.setDescripcionSolucion(entity.getDescripcionSolucion());
        objDTO.setFechaVencimiento(entity.getFechaVencimiento());
        //Verificar si el id del tecnico y de la prioridad vienen en el dto(según la lógica, no deberían venir al crearse un ticket)
        if (entity.getTecnicoAsignado() != null) {
            objDTO.setTecnicoAsignado(entity.getTecnicoAsignado().getIdUsuario());
            //No se mostrará el id del usuario, sino, el nombre
            objDTO.setNombreTecnico(entity.getTecnicoAsignado().getNombreUsuario());
        }
        if (entity.getPrioridad() != null) {
            objDTO.setPrioridad(entity.getPrioridad().getIdPrioridad());
            //En lugar de obtener el id de la prioridad, se muestra el nombre
            objDTO.setNombrePrioridad(entity.getPrioridad().getNombrePrioridad());
        }

        return objDTO;
    }

//    //Método para generar automaticamente el código del ticket
//    public String generarCodigo() {
//        LocalDate hoy = LocalDate.now();
//        String fecha = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//
//        //Para contar cuántos registros de bitácora con estado "Nuevo" se crearon hoy
//        long ticketsHoy = bitacoraRepo.countByEstadoAndFechaCambioBetween("Nuevo", hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
//
//        //"%03d" establece que el número de digitos minimo es de 3, si el numero es menor, lo rellena con 0
//        String siguiente = String.format("%03d", ticketsHoy + 1);
//        return "#" + fecha + "-" + siguiente;
//    }

//    //Método para actualizar parcialmente un ticket
//    public TicketDTO actualizarParcial(Long id, TicketDTO dto){
//        try {
//            Optional<TicketEntity> entidadOpcional = repo.findById(id);
//            if (entidadOpcional.isPresent()){
//                TicketEntity entidad = entidadOpcional.get();
//
//                //Solo se actualiza el campo si viene en el DTO
//                if (dto.getCodigo() != null) entidad.setCodigo(dto.getCodigo());
//                if (dto.getAsunto() != null) entidad.setAsunto(dto.getAsunto());
//                if (dto.getDescripcion() != null) entidad.setDescripcion(dto.getDescripcion());
//                if (dto.getDepartamento() != null) entidad.setDepartamento(buscarDepartamento(dto.getDepartamento()));
//                if (dto.getDescripcionFalla() != null) entidad.setDescripcionFalla(dto.getDescripcionFalla());
//                if (dto.getDescripcionSolucion() != null) entidad.setDescripcionSolucion(dto.getDescripcionSolucion());
//                if (dto.getFechaVencimiento() != null) entidad.setFechaVencimiento(dto.getFechaVencimiento());
//                if (dto.getTecnicoAsignado() != null) entidad.setTecnicoAsignado(buscarTecnico(dto.getTecnicoAsignado()));
//                if (dto.getPrioridad() != null) entidad.setPrioridad(buscarPrioridad(dto.getPrioridad()));
//                TicketEntity datosGuardados = repo.save(entidad);
//                return convertirADTO(datosGuardados);
//            }
//            return null;
//        }catch (Exception e){
//            log.error("Ocurrió un error al actualizar parcialmente el ticket con ID: " + id);
//            return null;
//        }
//    }
}
