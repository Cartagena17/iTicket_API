package iTicket.Douglas.Fases.Service;

import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.DetalleFases.Repository.DetalleFRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Fases.DTO.FaseDTO;
import iTicket.Douglas.Fases.DTO.PatchFaseDTO;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import iTicket.Douglas.Notificaciones.Event.FaseCreadaEvent;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
import iTicket.Douglas.Security.AuthenticatedUser;
import iTicket.Douglas.Security.AutorizacionUtils;
import iTicket.Douglas.util.ErrorCode;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaseService {

    private final FaseRepository repo;
    private final ProyectoRepository proyectoRepo;
    private final ApplicationEventPublisher eventos;
    private final UsuarioRepository usuarioRepo;
    private final DetalleFRepository detalleFRepo;

    private FaseEntity convertirAEntity(@Valid FaseDTO dto) {
        FaseEntity entity = new FaseEntity();
        entity.setNombreFase(dto.getNombreFase());
        entity.setFaseDescripcion(dto.getFaseDescripcion());
        entity.setFechaInicioEstimada(dto.getFechaInicioEstimada());
        entity.setFechaInicioReal(dto.getFechaInicioReal());
        entity.setFechaFinalEstimada(dto.getFechaFinalEstimada());
        entity.setFechaFinalReal(dto.getFechaFinalReal());
        entity.setNombreProveedor(dto.getNombreProveedor());
        entity.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        entity.setGastoTotal(dto.getGastoTotal());
        entity.setFinalizado(dto.getFinalizado());
        entity.setDepartamentoEncargado(dto.getDepartamentoEncargado());
        entity.setProyecto(buscarProyecto(dto.getProyecto()));
        return entity;
    }

    private FaseDTO convertirADTO(FaseEntity entity) {
        FaseDTO dto = new FaseDTO();
        dto.setIdFase(entity.getIdFase());
        dto.setNombreFase(entity.getNombreFase());
        dto.setFaseDescripcion(entity.getFaseDescripcion());
        dto.setFechaInicioEstimada(entity.getFechaInicioEstimada());
        dto.setFechaInicioReal(entity.getFechaInicioReal());
        dto.setFechaFinalEstimada(entity.getFechaFinalEstimada());
        dto.setFechaFinalReal(entity.getFechaFinalReal());
        dto.setNombreProveedor(entity.getNombreProveedor());
        dto.setPresupuestoEstimado(entity.getPresupuestoEstimado());
        dto.setGastoTotal(entity.getGastoTotal());
        dto.setFinalizado(entity.getFinalizado());
        dto.setDepartamentoEncargado(entity.getDepartamentoEncargado());
        dto.setProyecto(entity.getProyecto().getIdProyecto());
        return dto;
    }

    private ProyectoEntity buscarProyecto(Long id) {
        return proyectoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY001, "No existe ningún proyecto con id: " + id));
    }

    @Transactional
    public FaseDTO nuevaFase(@Valid FaseDTO dto) {
        FaseEntity entity = convertirAEntity(dto);
        validarPermisoEscrituraFase(entity.getProyecto());
        validarPresupuestoNoExcedido(entity.getProyecto(), null, entity.getGastoTotal());
        FaseEntity entitySave = repo.save(entity);
        log.info("Nueva fase registrada: " + entitySave.getIdFase());

        //Para generar notificación
        List<Long> idsDestino = resolverUsuariosDepartamento(entitySave.getDepartamentoEncargado());
        eventos.publishEvent(new FaseCreadaEvent(entitySave.getIdFase(), entitySave.getProyecto().getIdProyecto(), idsDestino));

        return convertirADTO(entitySave);
    }

    private List<Long> resolverUsuariosDepartamento(String departamentoEncargado) {
        List<String> tipos = switch (departamentoEncargado) {
            case "IT" -> List.of("IT");
            case "Mantenimiento" -> List.of("Mantenimiento");
            case "Ambos" -> List.of("IT", "Mantenimiento");
            default -> List.of(); // "Externo" u otro valor: no se notifica a nadie
        };
        if (tipos.isEmpty()) return List.of();

        return usuarioRepo.findByDepartamento_TipoDepartamentoIn(tipos).stream().map(UsuarioEntity::getIdUsuario).collect(Collectors.toList());
    }

    public List<FaseDTO> obtenerFases() {
        List<FaseEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Transactional
    public FaseDTO actualizarFase(Long id, @Valid FaseDTO dto) {
        FaseEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY002, "No existe una fase con id " + id));

        // Bloqueo duro: no se puede finalizar una fase mientras tenga detalles declarados
        // que aún no están marcados como completados.
        if (Boolean.TRUE.equals(dto.getFinalizado())) {
            validarDetallesCompletados(id);
        }

        ProyectoEntity proyecto = buscarProyecto(dto.getProyecto());
        validarPermisoEscrituraFase(proyecto);
        validarPresupuestoNoExcedido(proyecto, id, dto.getGastoTotal());

        entidad.setNombreFase(dto.getNombreFase());
        entidad.setFaseDescripcion(dto.getFaseDescripcion());
        entidad.setFechaInicioEstimada(dto.getFechaInicioEstimada());
        entidad.setFechaInicioReal(dto.getFechaInicioReal());
        entidad.setFechaFinalEstimada(dto.getFechaFinalEstimada());
        entidad.setFechaFinalReal(dto.getFechaFinalReal());
        entidad.setNombreProveedor(dto.getNombreProveedor());
        entidad.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        entidad.setGastoTotal(dto.getGastoTotal());
        entidad.setFinalizado(dto.getFinalizado());
        entidad.setDepartamentoEncargado(dto.getDepartamentoEncargado());
        entidad.setProyecto(proyecto);

        FaseEntity datosGuardados = repo.save(entidad);
        log.info("Fase con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    // Solo el Administrador o el coordinador del proyecto pueden crear/editar/eliminar sus fases
    // (el supervisor y cualquier otro tecnico solo consultan). SecurityConfig ya deja pasar a
    // Administrador y Tecnico hasta aqui; este es el filtro fino "a nivel de proyecto".
    private void validarPermisoEscrituraFase(ProyectoEntity proyecto) {
        if (AutorizacionUtils.esAdministrador()) return;

        AuthenticatedUser usuario = AutorizacionUtils.usuarioActual();
        boolean esCoordinador = usuario != null && usuario.idUsuario() != null
                && proyecto != null && proyecto.getCoordinador() != null
                && usuario.idUsuario().equals(proyecto.getCoordinador().getIdUsuario());

        if (!esCoordinador) {
            throw new AccessDeniedException("Solo el coordinador del proyecto puede modificar sus fases.");
        }
    }

    // Revisa todos los detalles declarados de la fase; si al menos uno existe y no está
    // marcado como completado, no se permite finalizar la fase.
    private void validarDetallesCompletados(Long idFase) {
        List<DetalleFEntity> detalles = detalleFRepo.findByFase_idFase(idFase);
        boolean hayIncompletos = detalles.stream().anyMatch(d -> !Boolean.TRUE.equals(d.getCompletado()));
        if (hayIncompletos) {
            throw new OperacionInvalidaException(
                    "No se puede finalizar la fase: todavía tiene detalles declarados que no están completados.");
        }
    }

    // Suma el gasto de todas las fases del proyecto (excluyendo la fase que se está editando,
    // si aplica) más el nuevo gasto propuesto para esta fase, y valida que ese total no supere
    // el presupuesto estimado del proyecto. El gasto_total del proyecto es en realidad un valor
    // calculado en la base de datos (trigger TRG_Actualizar_Gasto_Proyecto = SUM de las fases),
    // por eso la validación se hace aquí, en el punto donde se controla el gasto de cada fase.
    private void validarPresupuestoNoExcedido(ProyectoEntity proyecto, Long idFaseExcluir, Double nuevoGastoFase) {
        List<FaseEntity> fasesDelProyecto = repo.findByProyecto_IdProyecto(proyecto.getIdProyecto());

        double sumaOtrasFases = fasesDelProyecto.stream()
                .filter(f -> idFaseExcluir == null || !f.getIdFase().equals(idFaseExcluir))
                .mapToDouble(f -> f.getGastoTotal() != null ? f.getGastoTotal() : 0.0)
                .sum();

        double gastoNuevo = nuevoGastoFase != null ? nuevoGastoFase : 0.0;
        BigDecimal gastoTotalProyectado = BigDecimal.valueOf(sumaOtrasFases + gastoNuevo);

        BigDecimal presupuesto = proyecto.getPresupuestoEstimado();
        if (presupuesto != null && gastoTotalProyectado.compareTo(presupuesto) > 0) {
            throw new OperacionInvalidaException(
                    "El gasto total del proyecto (" + gastoTotalProyectado + ") superaría su presupuesto estimado ("
                            + presupuesto + ").");
        }
    }

    public FaseDTO buscarPorNombreFase(String nombreFase) {
        FaseEntity entidad = repo.findByNombreFase(nombreFase)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY002, "No existe ninguna fase con nombre: " + nombreFase));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarFase(Long id) {
        FaseEntity fase = repo.findById(id).orElse(null);
        if (fase == null) return false;

        validarPermisoEscrituraFase(fase.getProyecto());
        repo.deleteById(id);
        return true;
    }

    @Transactional
    public FaseDTO actualizarCampoFase(Long id, @Valid PatchFaseDTO dto) {
        FaseEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY002, "No existe una fase con id " + id));

        validarPermisoEscrituraFase(entidad.getProyecto());

        if (dto.getGastoTotal() != null) {
            validarPresupuestoNoExcedido(entidad.getProyecto(), id, dto.getGastoTotal());
            entidad.setGastoTotal(dto.getGastoTotal());
        }
        if (dto.getFechaInicioEstimada() != null) {
            entidad.setFechaInicioEstimada(dto.getFechaInicioEstimada());
        }
        if (dto.getFechaFinalEstimada() != null) {
            entidad.setFechaFinalEstimada(dto.getFechaFinalEstimada());
        }

        FaseEntity datosGuardados = repo.save(entidad);
        log.info("Fase con id " + id + " actualizada parcialmente");
        return convertirADTO(datosGuardados);
    }

    public List<FaseDTO> buscarPorIdProyecto(Long proyecto) {
        List<FaseEntity> registro = repo.findByProyecto_IdProyecto(proyecto);
        return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
