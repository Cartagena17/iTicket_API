package iTicket.Douglas.Proyectos.Service;

import iTicket.Douglas.Proyectos.DTO.ProyectoDTO;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
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
public class ProyectoService {

    private final ProyectoRepository repo;
    private final UsuarioRepository repoUsuario;

    public ProyectoService(ProyectoRepository repo, UsuarioRepository repoUsuario) {
        this.repo = repo;
        this.repoUsuario = repoUsuario;
    }

    public ProyectoDTO crearProyecto(@Valid ProyectoDTO dto){
        try {
            ProyectoEntity entity = convertirAEntity(dto);
            ProyectoEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al registrar el proyecto " + e.getMessage());
            throw new RuntimeException("No se pudo crear el proyecto");
        }
    }

    public List<ProyectoDTO> obtenerTodo(){
        List<ProyectoEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ProyectoDTO obtenerPorId(Long id){
        Optional<ProyectoEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public List<ProyectoDTO> buscarPorNombre(String nombre) {
        try {
            List<ProyectoEntity> registros = repo.findByNombreProyectoContainingIgnoreCase(nombre);
            if (!registros.isEmpty()){
                return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún proyecto con nombre: " + nombre);
            return Collections.emptyList();
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return Collections.emptyList();
        }
    }

    public List<ProyectoDTO> buscarPorTipo(String tipo) {
        try {
            List<ProyectoEntity> registros = repo.findByTipoProyecto(tipo);
            if (!registros.isEmpty()){
                return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún proyecto del tipo: " + tipo);
            return Collections.emptyList();
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return Collections.emptyList();
        }
    }

    public boolean eliminarProyecto(Long id){
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public ProyectoDTO actualizarProyecto(Long id, @Valid ProyectoDTO dto){
        Optional<ProyectoEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isPresent()){
            ProyectoEntity entidad = entidadOpcional.get();
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
            return convertirADTO(datosGuardados);
        }
        return null;
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
        ProyectoEntity objEntity =  new ProyectoEntity();
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
        Optional<UsuarioEntity> usuarioOpcional = repoUsuario.findById(id);
        if (usuarioOpcional.isPresent()){
            return usuarioOpcional.get();
        }
        log.warn("No existe ningún usuario con ID: " + id);
        throw new RuntimeException("No existe ningún usuario con ID: " + id);
    }

}
