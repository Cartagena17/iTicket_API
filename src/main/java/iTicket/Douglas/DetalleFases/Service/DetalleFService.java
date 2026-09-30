package iTicket.Douglas.DetalleFases.Service;

import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import iTicket.Douglas.DetalleFases.Repository.DetalleFRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
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

    @Transactional
    public DetalleFDTO nuevoDetalleF(@Valid DetalleFDTO dto) {
        FaseEntity fase = buscarFase(dto.getFase());
        validarPermisoEscrituraDetalle(fase.getProyecto());
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
        // Marcar un detalle como no completado en una fase ya finalizada reabriría esa
        // inconsistencia, así que se bloquea (sí se permite editar la descripción o
        // dejarlo completado, ya que eso no contradice el estado de la fase).
        if (Boolean.FALSE.equals(dto.getCompletado())) {
            validarFaseNoFinalizada(fase);
        }

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
        repo.deleteById(id);
        return true;
    }

    public List<DetalleFDTO> obtenerPorIdFase(Long fase) {
        List<DetalleFEntity> registro = repo.findByFase_idFase(fase);
        return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
