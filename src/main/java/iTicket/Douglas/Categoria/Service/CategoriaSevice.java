package iTicket.Douglas.Categoria.Service;

import iTicket.Douglas.Categoria.DTO.CategoriaDTO;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.Categoria.Repository.CategoriaRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
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
public class CategoriaSevice {

    private final CategoriaRepository repo;

    public CategoriaEntity convertirAEntity(@Valid CategoriaDTO dto) {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombreCategoria(dto.getNombreCategoria());
        return entity;
    }

    public CategoriaDTO convertirADTO(@Valid CategoriaEntity entity) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(entity.getIdCategoria());
        dto.setNombreCategoria(entity.getNombreCategoria());
        return dto;
    }

    @Transactional
    public CategoriaDTO nuevaCategoria(@Valid CategoriaDTO dto) {
        CategoriaEntity entity = convertirAEntity(dto);
        CategoriaEntity entitySave = repo.save(entity);
        log.info("Nueva categoría registrada: " + entitySave.getIdCategoria());
        return convertirADTO(entitySave);
    }

    public List<CategoriaDTO> obtenerCategorias() {
        List<CategoriaEntity> datos = repo.findAll();
        return datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public CategoriaDTO obtenerNombreCategoria(String nombreCategoria) {
        CategoriaEntity entidad = repo.findByNombreCategoria(nombreCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una categoría con nombre: " + nombreCategoria));
        return convertirADTO(entidad);
    }

    @Transactional
    public CategoriaDTO actualizarCategoria(Long id, @Valid CategoriaDTO dto) {
        CategoriaEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una categoría con id " + id));

        entidad.setNombreCategoria(dto.getNombreCategoria());
        CategoriaEntity datosGuardados = repo.save(entidad);
        log.info("Categoría con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarCategoria(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}