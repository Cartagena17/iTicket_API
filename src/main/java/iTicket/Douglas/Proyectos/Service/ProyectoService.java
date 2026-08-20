package iTicket.Douglas.Proyectos.Service;

import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Proyectos.DTO.ProyectoDTO;
import iTicket.Douglas.Proyectos.DTO.ProyectoPaginaDTO;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @Transactional
    public ProyectoDTO crearProyecto(@Valid ProyectoDTO dto) {
        ProyectoEntity entity = convertirAEntity(dto);
        ProyectoEntity entitySave = repo.save(entity);
        log.info("Nuevo proyecto registrado: " + entitySave.getIdProyecto());
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
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un proyecto con id " + id));
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
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un proyecto con id " + id));

        entidad.setNombreProyecto(dto.getNombreProyecto());
        entidad.setTipoProyecto(dto.getTipoProyecto());
        entidad.setUbicacion(dto.getUbicacion());
        entidad.setDescripcionProyecto(dto.getDescripcionProyecto());
        entidad.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        entidad.setCoordinador(buscarUsuario(dto.getCoordinador()));
        entidad.setSupervisor(buscarUsuario(dto.getSupervisor()));
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
        return objDTO;
    }

    private ProyectoEntity convertirAEntity(@Valid ProyectoDTO dto) {
        ProyectoEntity objEntity = new ProyectoEntity();
        objEntity.setNombreProyecto(dto.getNombreProyecto());
        objEntity.setTipoProyecto(dto.getTipoProyecto());
        objEntity.setUbicacion(dto.getUbicacion());
        objEntity.setDescripcionProyecto(dto.getDescripcionProyecto());
        objEntity.setPresupuestoEstimado(dto.getPresupuestoEstimado());
        objEntity.setCoordinador(buscarUsuario(dto.getCoordinador()));
        objEntity.setSupervisor(buscarUsuario(dto.getSupervisor()));
        objEntity.setFinalizado(dto.getFinalizado());
        return objEntity;
    }

    private UsuarioEntity buscarUsuario(Long id) {
        return repoUsuario.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún usuario con ID: " + id));
    }
}