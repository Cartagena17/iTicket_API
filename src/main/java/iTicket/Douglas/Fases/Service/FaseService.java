package iTicket.Douglas.Fases.Service;


import iTicket.Douglas.Fases.DTO.FaseDTO;
import iTicket.Douglas.Fases.DTO.PatchFaseDTO;
import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Fases.Repository.FaseRepository;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import iTicket.Douglas.Proyectos.Repository.ProyectoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FaseService {

    private final FaseRepository repo;
    //Quitar comentario al unir las demas partes
    private final ProyectoRepository proyectoRepo;

    public FaseService(FaseRepository repo, ProyectoRepository proyectoRepo) {
        this.repo = repo;
        this.proyectoRepo = proyectoRepo;
    }

    private FaseEntity convertirAEntity(@Valid FaseDTO dto){
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
        //Quitar comentario al unir las demas partes
        entity.setProyecto(buscarProyecto(dto.getProyecto()));
        return entity;
    }

    private FaseDTO convertirADTO (FaseEntity entity){
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
        //quitar comentario al unir las demas partes
        dto.setProyecto(entity.getProyecto().getIdProyecto());
        return dto;
    }

    //quitar comentario al unir las demas partes
    private ProyectoEntity buscarProyecto (Long id){
        Optional<ProyectoEntity> proyecto = proyectoRepo.findById(id);
        if (proyecto.isPresent()){
            return proyecto.get();
        }
        log.warn("No existe ningun proyecto con id: " + id);
        throw new RuntimeException("No existe ningun proyecto con id:" + id);
    }


    public FaseDTO nuevaFase(@Valid FaseDTO dto){
        try {
            FaseEntity entity = convertirAEntity(dto);
            FaseEntity entitySave = repo.save(entity);
            return convertirADTO(entitySave);
        }
        catch (Exception e){
            log.error("Error al registrar la fase "  +e.getMessage());
            throw new RuntimeException("Error al registrar la fase.");
        }
    }

    public List<FaseDTO> obtenerFases() {
        List<FaseEntity> dto = repo.findAll();
        return dto.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public FaseDTO actualizarFase(Long id, @Valid FaseDTO dto) {
        try {
            Optional<FaseEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                FaseEntity entidad = entidadOpcional.get();
                entidad.setNombreFase(dto.getNombreFase());
                entidad.setFaseDescripcion(dto.getFaseDescripcion());
                entidad.setFechaInicioEstimada(dto.getFechaInicioEstimada());
                entidad.setFechaInicioReal(dto.getFechaInicioReal());
                entidad.setFechaFinalEstimada(dto.getFechaFinalEstimada());
                entidad.setNombreProveedor(dto.getNombreProveedor());
                entidad.setPresupuestoEstimado(dto.getPresupuestoEstimado());
                entidad.setGastoTotal(dto.getGastoTotal());
                entidad.setFinalizado(dto.getFinalizado());
                //Quitar comentario al unir las demas partes
                entidad.setProyecto(buscarProyecto(dto.getProyecto()));

                FaseEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }
        catch (Exception e){
            log.error("Ocurrió un error al procesar la información");
            return null;
        }
    }

    public FaseDTO buscarPorNombreFase(String nombreFase) {
        try {
            Optional<FaseEntity> registro = repo.findByNombreFase(nombreFase);
            if (registro.isPresent()){
                return convertirADTO(registro.get());
            }
            log.warn("No existe ninguna fase con nombre: " + nombreFase);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrió un error durante el proceso de obtención");
            return null;
        }
    }

    public boolean eliminarFase(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public FaseDTO actualizarCampoFase(Long id, @Valid PatchFaseDTO dto) {
        try{
            Optional<FaseEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                FaseEntity entidad = entidadOpcional.get();

                if (dto.getGastoTotal() != null){
                    entidad.setGastoTotal(dto.getGastoTotal());
                }

                if (dto.getFechaInicioEstimada() != null){
                    entidad.setFechaInicioEstimada(dto.getFechaInicioEstimada());
                }

                if (dto.getFechaFinalEstimada() != null){
                    entidad.setFechaFinalEstimada(dto.getFechaFinalEstimada());
                }

                FaseEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            log.warn("No se encontro la fase con id: " + id);
            return null;
        }
        catch (Exception e){
            log.error("Error al actualizar parcialmente la fase con id: " + id);
            return null;
        }
    }

    public List<FaseDTO> buscarPorIdProyecto(Long proyecto){
        try{
            List<FaseEntity> registro = repo.findByProyecto_IdProyecto(proyecto);
            if (registro != null && !registro.isEmpty()){
                return registro.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existen fases asociadas al proyecto con id: " + proyecto);
            return null;
        }
        catch (Exception e){
            log.error("Ocurrió un error durante el proceso de obtención");
            e.printStackTrace();
            return null;
        }
    }
}
