package iTicket.Douglas.Categoria.Service;


import iTicket.Douglas.Categoria.DTO.CategoriaDTO;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.Categoria.Repository.CategoriaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoriaSevice {

    private final CategoriaRepository repo;

    public CategoriaSevice(CategoriaRepository repo) {
        this.repo = repo;
    }

    public CategoriaEntity convertirAEntity(@Valid CategoriaDTO dto){
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombreCategoria(dto.getNombreCategoria());
        return entity;
    }

    public CategoriaDTO convertirADTO(@Valid CategoriaEntity entity){
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(entity.getIdCategoria());
        dto.setNombreCategoria(entity.getNombreCategoria());
        return dto;
    }

    public CategoriaDTO nuevaCategoria(@Valid CategoriaDTO dto) {
        try {
            CategoriaEntity entity = convertirAEntity(dto);
            CategoriaEntity entitySave = repo.save(entity);
            return  convertirADTO(entitySave);
        }
        catch (Exception e){
            log.error("Error al ingresar la categoria" + e.getMessage());
            return null;
        }
    }

    public List<CategoriaDTO> obtenerCategorias() {
        List<CategoriaEntity> datos = repo.findAll();
        return datos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public CategoriaDTO obtenerNombreCategoria(String nombreCategoria) {
        Optional<CategoriaEntity> entidadOpcional = repo.findByNombreCategoria(nombreCategoria);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public CategoriaDTO actualizarCategoria(Long id, @Valid CategoriaDTO dto) {
        try{
            Optional<CategoriaEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                CategoriaEntity entidad = entidadOpcional.get();
                entidad.setNombreCategoria(dto.getNombreCategoria());
                CategoriaEntity datosGuardados = repo.save(entidad);
                return convertirADTO(datosGuardados);
            }
            return null;
        }
        catch (Exception e){
            log.error("Error al procesar la informacion de la categoria");
            return null;
        }
    }

    public boolean eliminarCategoria(Long id) {
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
