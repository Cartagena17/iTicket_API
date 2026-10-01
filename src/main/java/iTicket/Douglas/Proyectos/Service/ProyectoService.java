package iTicket.Douglas.Proyectos.Service;

import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import iTicket.Douglas.Notificaciones.Event.ProyectoCreadoEvent;
import iTicket.Douglas.Proyectos.DTO.ProyectoDTO;
import iTicket.Douglas.Proyectos.DTO.ProyectoPaginaDTO;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
import iTicket.Douglas.Security.AutorizacionUtils;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import iTicket.Douglas.util.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoService {

    private final ProyectoRepository repo;
    private final UsuarioRepository repoUsuario;
    private final FaseRepository faseRepo;
    private final ApplicationEventPublisher eventos;

    @Transactional
    public ProyectoDTO crearProyecto(@Valid ProyectoDTO dto) {
        ProyectoEntity entity = convertirAEntity(dto);
        ProyectoEntity entitySave = repo.save(entity);
        log.info("Nuevo proyecto registrado: " + entitySave.getIdProyecto());
        eventos.publishEvent(new ProyectoCreadoEvent(entitySave.getIdProyecto(), entitySave.getCoordinador().getIdUsuario(), entitySave.getSupervisor().getIdUsuario()));
        return convertirADTO(entitySave);
    }

    public List<ProyectoDTO> obtenerTodo() {
        List<ProyectoEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ProyectoPaginaDTO obtenerPaginado(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idProyecto").descending());
        Page<ProyectoEntity> resultado = repo.findAll(pageable);

        List<ProyectoDTO> proyectos = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());

        return new ProyectoPaginaDTO(proyectos, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    public ProyectoDTO obtenerPorId(Long id) {
        ProyectoEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY001, "No existe un proyecto con id " + id));
        return convertirADTO(entidad);
    }

    public List<ProyectoDTO> buscarPorNombre(String nombre) {
        List<ProyectoEntity> registros = repo.findByNombreProyectoContainingIgnoreCase(nombre);
        return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ProyectoDTO> buscarPorTipo(String tipo) {
        List<ProyectoEntity> registros = repo.findByTipoProyecto(tipo);
        return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean eliminarProyecto(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ProyectoDTO actualizarProyecto(Long id, @Valid ProyectoDTO dto) {
        ProyectoEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY001, "No existe un proyecto con id " + id));

        // Un proyecto solo puede marcarse como finalizado si TODAS sus fases ya están finalizadas.
        if (Boolean.TRUE.equals(dto.getFinalizado())) {
            validarFasesCompletadas(id);
        }

        entidad.setNombreProyecto(dto.getNombreProyecto());
        entidad.setTipoProyecto(dto.getTipoProyecto());
        entidad.setUbicacion(dto.getUbicacion());
        entidad.setDescripcionProyecto(dto.getDescripcionProyecto());
        entidad.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        entidad.setContratista(dto.getContratista());
        entidad.setCoordinador(buscarResponsable(dto.getCoordinador(), "coordinador"));
        entidad.setSupervisor(buscarResponsable(dto.getSupervisor(), "supervisor"));
        entidad.setFinalizado(dto.getFinalizado());
        entidad.setGastoTotal(dto.getGastoTotal());

        ProyectoEntity datosGuardados = repo.save(entidad);
        log.info("Proyecto con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    private ProyectoDTO convertirADTO(@Valid ProyectoEntity entity) {
        ProyectoDTO objDTO = new ProyectoDTO();
        objDTO.setIdProyecto(entity.getIdProyecto());
        objDTO.setNombreProyecto(entity.getNombreProyecto());
        objDTO.setTipoProyecto(entity.getTipoProyecto());
        objDTO.setUbicacion(entity.getUbicacion());
        objDTO.setDescripcionProyecto(entity.getDescripcionProyecto());
        objDTO.setPresupuestoEstimado(entity.getPresupuestoEstimado());
        objDTO.setGastoTotal(entity.getGastoTotal());
        objDTO.setCoordinador(entity.getCoordinador().getIdUsuario());
        objDTO.setNombreCoordinador(entity.getCoordinador().getNombreUsuario());
        objDTO.setSupervisor(entity.getSupervisor().getIdUsuario());
        objDTO.setNombreSupervisor(entity.getSupervisor().getNombreUsuario());
        objDTO.setFinalizado(entity.getFinalizado());
        objDTO.setContratista(entity.getContratista());
        return objDTO;
    }

    private ProyectoEntity convertirAEntity(@Valid ProyectoDTO dto) {
        ProyectoEntity objEntity = new ProyectoEntity();
        objEntity.setNombreProyecto(dto.getNombreProyecto());
        objEntity.setTipoProyecto(dto.getTipoProyecto());
        objEntity.setUbicacion(dto.getUbicacion());
        objEntity.setDescripcionProyecto(dto.getDescripcionProyecto());
        objEntity.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        objEntity.setContratista(dto.getContratista());
        objEntity.setCoordinador(buscarResponsable(dto.getCoordinador(), "coordinador"));
        objEntity.setSupervisor(buscarResponsable(dto.getSupervisor(), "supervisor"));
        objEntity.setFinalizado(dto.getFinalizado());
        return objEntity;
    }

    private UsuarioEntity buscarUsuario(Long id) {
        return repoUsuario.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WUSR002, "No existe ningún usuario con ID: " + id));
    }

    // Revisa todas las fases del proyecto; si al menos una existe y no está marcada como
    // finalizada, no se permite finalizar el proyecto. Sin fases, se permite (nada que validar).
    private void validarFasesCompletadas(Long idProyecto) {
        List<FaseEntity> fases = faseRepo.findByProyecto_IdProyecto(idProyecto);
        boolean hayIncompletas = fases.stream().anyMatch(f -> !Boolean.TRUE.equals(f.getFinalizado()));
        if (hayIncompletas) {
            throw new OperacionInvalidaException(
                    "No se puede finalizar el proyecto: todavía tiene fases que no están finalizadas.");
        }
    }

    // Solo el personal operativo (rol Administrador o Tecnico) puede ser coordinador o supervisor
    // de un proyecto; un usuario con rol "Usuario" (quien reporta tickets) no califica.
    private static final List<String> ROLES_VALIDOS_RESPONSABLE = List.of("Administrador", "Tecnico");

    private UsuarioEntity buscarResponsable(Long id, String cargo) {
        UsuarioEntity usuario = buscarUsuario(id);
        String rol = usuario.getRol() != null ? usuario.getRol().getNombreRol() : null;
        if (rol == null || !ROLES_VALIDOS_RESPONSABLE.contains(rol)) {
            throw new OperacionInvalidaException(
                    "El usuario asignado como " + cargo + " debe tener rol Administrador o Tecnico.");
        }
        return usuario;
    }

    // Válvula de escape para deshacer una finalización por error: reabre un proyecto ya
    // finalizado (lo regresa a "en progreso"), devolviendo el acceso normal a sus fases y
    // detalles. De un solo sentido y restringida a Administrador, igual que reabrirFase.
    @Transactional
    public ProyectoDTO reabrirProyecto(Long id) {
        ProyectoEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY001, "No existe un proyecto con id " + id));

        if (!AutorizacionUtils.esAdministrador()) {
            throw new AccessDeniedException("Solo un Administrador puede reabrir un proyecto ya finalizado.");
        }

        if (!Boolean.TRUE.equals(entidad.getFinalizado())) {
            throw new OperacionInvalidaException("El proyecto no está finalizado; no hay nada que reabrir.");
        }

        entidad.setFinalizado(false);
        ProyectoEntity datosGuardados = repo.save(entidad);
        log.info("Proyecto con id " + id + " reabierto por un administrador");
        return convertirADTO(datosGuardados);
    }
}
