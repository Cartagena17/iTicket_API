package iTicket.Douglas.DetalleFases.Service;

import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.DetalleFases.Repository.DetalleFRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Security.AuthenticatedUser;
import iTicket.Douglas.Security.AutorizacionUtils;
import iTicket.Douglas.util.ErrorCode;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY002, "No existe ninguna fase con id: " + id));
    }

    // Si una fase ya está finalizada, no debe poder recibir detalles nuevos ni que uno de los
    // suyos vuelva a quedar incompleto: eso dejaría la fase marcada como "Finalizada" con
    // detalles pendientes, justo la inconsistencia que el bloqueo de finalización de fases evita.
    private void validarFaseNoFinalizada(FaseEntity fase) {
        if (Boolean.TRUE.equals(fase.getFinalizado())) {
            throw new OperacionInvalidaException(
                    "No se pueden agregar ni modificar detalles de una fase ya finalizada.");
        }
    }

    // Solo el Administrador, el coordinador o el supervisor del proyecto pueden crear/editar/eliminar
    // sus detalles de fase (cualquier otro tecnico solo consulta). SecurityConfig ya deja pasar a
    // Administrador y Tecnico hasta aqui; este es el filtro fino "a nivel de proyecto".
    private void validarPermisoEscrituraDetalle(ProyectoEntity proyecto) {
        if (AutorizacionUtils.esAdministrador()) return;

        AuthenticatedUser usuario = AutorizacionUtils.usuarioActual();
        Long idUsuario = usuario != null ? usuario.idUsuario() : null;

        boolean esCoordinador = idUsuario != null && proyecto != null && proyecto.getCoordinador() != null
                && idUsuario.equals(proyecto.getCoordinador().getIdUsuario());
        boolean esSupervisor = idUsuario != null && proyecto != null && proyecto.getSupervisor() != null
                && idUsuario.equals(proyecto.getSupervisor().getIdUsuario());

        if (!esCoordinador && !esSupervisor) {
            throw new AccessDeniedException("Solo el coordinador o el supervisor del proyecto pueden modificar sus detalles.");
        }
    }

    // Un proyecto ya finalizado no puede recibir detalles nuevos, ni que los existentes
    // se editen o eliminen.
    private void validarProyectoNoFinalizado(ProyectoEntity proyecto) {
        if (proyecto != null && Boolean.TRUE.equals(proyecto.getFinalizado())) {
            throw new OperacionInvalidaException(
                    "No se pueden modificar los detalles de un proyecto ya finalizado.");
        }
    }

    // La descripcion de un detalle debe ser unica DENTRO de su fase (no global): dos fases
    // distintas si pueden tener, cada una, un detalle con la misma descripcion.
    private void validarDescripcionDetalleUnica(Long idFase, String descripcion, Long idDetalleExcluir) {
        boolean existe = idDetalleExcluir == null
                ? repo.existsByFase_IdFaseAndDescripcionDetalleIgnoreCase(idFase, descripcion)
                : repo.existsByFase_IdFaseAndDescripcionDetalleIgnoreCaseAndIdDetalleFaseNot(idFase, descripcion, idDetalleExcluir);

        if (existe) {
            throw new RecursoDuplicadoException(
                    "Ya existe un detalle con la descripcion \"" + descripcion + "\" en esta fase.");
        }
    }

    @Transactional
    public DetalleFDTO nuevoDetalleF(@Valid DetalleFDTO dto) {
        FaseEntity fase = buscarFase(dto.getFase());
        validarPermisoEscrituraDetalle(fase.getProyecto());
        validarProyectoNoFinalizado(fase.getProyecto());
        validarDescripcionDetalleUnica(dto.getFase(), dto.getDescripcionDetalle(), null);
        validarFaseNoFinalizada(fase);

        DetalleFEntity entity = new DetalleFEntity();
        entity.setDescripcionDetalle(dto.getDescripcionDetalle());
        entity.setCompletado(dto.getCompletado());
        entity.setFase(fase);

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
                .orElseThrow(() -> new RecursoNoEncontradoException(ErrorCode.WPRY002, "No existe un detalle de fase con id " + id));

        FaseEntity fase = buscarFase(dto.getFase());
        validarPermisoEscrituraDetalle(fase.getProyecto());
        validarProyectoNoFinalizado(fase.getProyecto());
        validarDescripcionDetalleUnica(dto.getFase(), dto.getDescripcionDetalle(), id);
        // Una fase ya finalizada queda "cerrada": ninguno de sus detalles puede modificarse,
        // sin importar qué campo se esté cambiando.
        validarFaseNoFinalizada(fase);

        entity.setDescripcionDetalle(dto.getDescripcionDetalle());
        entity.setCompletado(dto.getCompletado());
        entity.setFase(fase);

        DetalleFEntity datosGuardados = repo.save(entity);
        log.info("Detalle de fase con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarDetalleF(Long id) {
        DetalleFEntity entity = repo.findById(id).orElse(null);
        if (entity == null) return false;

        validarPermisoEscrituraDetalle(entity.getFase().getProyecto());
        validarProyectoNoFinalizado(entity.getFase().getProyecto());
        // Una fase ya finalizada no puede perder ninguno de sus detalles tampoco.
        validarFaseNoFinalizada(entity.getFase());
        repo.deleteById(id);
        return true;
    }

    public List<DetalleFDTO> obtenerPorIdFase(Long fase) {
        List<DetalleFEntity> registro = repo.findByFase_idFase(fase);
        return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
