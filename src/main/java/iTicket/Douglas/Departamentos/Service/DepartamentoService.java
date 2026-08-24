package iTicket.Douglas.Departamentos.Service;

import iTicket.Douglas.Areas.Entity.AreaEntity;
import iTicket.Douglas.Areas.Repository.AreaRepository;
import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository repo;
    private final AreaRepository arearepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    public DepartamentoDTO nuevoDepartamento(@Valid DepartamentoDTO dto) {
        AreaEntity area = arearepo.findById(dto.getIdArea())
                .orElseThrow(() -> new RecursoNoEncontradoException("El área con id " + dto.getIdArea() + " no existe"));

        DepartamentoEntity entity = convertirAEntity(dto, area);
        DepartamentoEntity entitySave = repo.save(entity);
        log.info("Nuevo departamento registrado: " + entitySave.getIdDepartamento());
        return convertirADTO(entitySave);
    }

    public List<DepartamentoDTO> obetenerTodo() {
        List<DepartamentoEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DepartamentoDTO obtenerPorId(Long id) {
        DepartamentoEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un departamento con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public DepartamentoDTO actualizar(Long id, @Valid DepartamentoDTO dto) {
        DepartamentoEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un departamento con id " + id));

        if (dto.getIdArea() != null) {
            AreaEntity area = arearepo.findById(dto.getIdArea())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El área con id " + dto.getIdArea() + " no existe"));
            entidad.setArea(area);
        }
        entidad.setNombreDepartamento(dto.getNombreDepartamento());
        DepartamentoEntity datosGuardados = repo.save(entidad);
        log.info("Departamento con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    //La lista es la misma para todos, ya no depende del area del usuario
    public List<DepartamentoDTO> obtenerDepartamentosAsignables() {
        List<DepartamentoEntity> data = repo.findAsignables();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private DepartamentoEntity convertirAEntity(@Valid DepartamentoDTO dto, AreaEntity area) {
        DepartamentoEntity objEntity = new DepartamentoEntity();
        objEntity.setNombreDepartamento(dto.getNombreDepartamento());
        objEntity.setTipoDepartamento(dto.getTipoDepartamento());
        objEntity.setArea(area);
        return objEntity;
    }

    private DepartamentoDTO convertirADTO(@Valid DepartamentoEntity entity) {
        DepartamentoDTO objDTO = new DepartamentoDTO();
        objDTO.setIdDepartamento(entity.getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getNombreDepartamento());
        objDTO.setTipoDepartamento(entity.getTipoDepartamento());
        objDTO.setIdArea(entity.getArea().getIdArea());
        objDTO.setNombreArea(entity.getArea().getNombreArea());
        return objDTO;
    }
}