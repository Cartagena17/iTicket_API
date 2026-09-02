package iTicket.Douglas.Fases.Service;

import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Fases.DTO.FaseDTO;
import iTicket.Douglas.Fases.DTO.PatchFaseDTO;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import iTicket.Douglas.Notificaciones.Event.FaseCreadaEvent;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún proyecto con id: " + id));
    }

    @Transactional
    public FaseDTO nuevaFase(@Valid FaseDTO dto) {
        FaseEntity entity = convertirAEntity(dto);
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
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una fase con id " + id));

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
        entidad.setProyecto(buscarProyecto(dto.getProyecto()));

        FaseEntity datosGuardados = repo.save(entidad);
        log.info("Fase con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    public FaseDTO buscarPorNombreFase(String nombreFase) {
        FaseEntity entidad = repo.findByNombreFase(nombreFase)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ninguna fase con nombre: " + nombreFase));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarFase(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public FaseDTO actualizarCampoFase(Long id, @Valid PatchFaseDTO dto) {
        FaseEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una fase con id " + id));

        if (dto.getGastoTotal() != null) {
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