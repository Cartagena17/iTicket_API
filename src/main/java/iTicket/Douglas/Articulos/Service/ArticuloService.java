package iTicket.Douglas.Articulos.Service;

import iTicket.Douglas.Articulos.DTO.ArticuloDTO;
import iTicket.Douglas.Articulos.DTO.ArticuloPaginaDTO;
import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.Articulos.Specification.ArticuloSpecifications;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.Categoria.Repository.CategoriaRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Modelos.Entity.ModeloEntity;
import iTicket.Douglas.Modelos.Repository.ModeloRepository;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import iTicket.Douglas.Ubicaciones.Repository.UbicacionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticuloService {

    private final ArticuloRepository repo;
    private final ModeloRepository modelorepo;
    private final CategoriaRepository categoriaRepo;
    private final UbicacionRepository ubicacionRepo;

    @Transactional
    public ArticuloDTO nuevoArticulo(@Valid ArticuloDTO dto) {
        CategoriaEntity categoria = categoriaRepo.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + dto.getIdCategoria()));

        UbicacionEntity ubicacion = ubicacionRepo.findById(dto.getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la ubicación con id " + dto.getIdUbicacion()));

        ModeloEntity modelo = null;
        if (dto.getIdModelo() != null) {
            modelo = modelorepo.findById(dto.getIdModelo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el modelo con id " + dto.getIdModelo()));
        }

        ArticuloEntity entity = convertirAEntity(dto, modelo, categoria, ubicacion);
        ArticuloEntity entitySave = repo.save(entity);
        log.info("Nuevo artículo registrado: " + entitySave.getIdArticulo());
        return convertirADTO(entitySave);
    }

    public List<ArticuloDTO> obtenerTodo() {
        List<ArticuloEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ArticuloDTO obtenerPorId(Long id) {
        ArticuloEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un artículo con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public ArticuloDTO actualizarData(Long id, @Valid ArticuloDTO dto) {
        ArticuloEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un artículo con id " + id));

        if (dto.getIdCategoria() != null) {
            CategoriaEntity categoria = categoriaRepo.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + dto.getIdCategoria()));
            entidad.setCategoria(categoria);
        }

        if (dto.getIdUbicacion() != null) {
            UbicacionEntity ubicacion = ubicacionRepo.findById(dto.getIdUbicacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la ubicación con id " + dto.getIdUbicacion()));
            entidad.setUbicacion(ubicacion);
        }

        if (dto.getIdModelo() != null) {
            ModeloEntity modelo = modelorepo.findById(dto.getIdModelo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el modelo con id " + dto.getIdModelo()));
            entidad.setModelo(modelo);
        }

        entidad.setCodigoArticulo(dto.getCodigoArticulo());
        ArticuloEntity datosGuardados = repo.save(entidad);
        log.info("Artículo con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    public List<ArticuloDTO> buscarPorCodigoParcial(String fragmento) {
        List<ArticuloEntity> lista = repo.findByCodigoArticuloContainingIgnoreCase(fragmento);
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ArticuloPaginaDTO obtenerPaginado(int pagina, int tamano, String busqueda, Long idCategoria, Long idUbicacion, Long idMarca) {
        Specification<ArticuloEntity> spec = (root, query, cb) -> cb.conjunction();
        if (busqueda != null && !busqueda.isBlank()) {
            spec = spec.and(ArticuloSpecifications.conBusqueda(busqueda));
        }
        if (idCategoria != null) {
            spec = spec.and(ArticuloSpecifications.conCategoria(idCategoria));
        }
        if (idUbicacion != null) {
            spec = spec.and(ArticuloSpecifications.conUbicacion(idUbicacion));
        }
        if (idMarca != null) {
            spec = spec.and(ArticuloSpecifications.conMarca(idMarca));
        }

        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idArticulo").descending());
        Page<ArticuloEntity> resultado = repo.findAll(spec, pageable);

        List<ArticuloDTO> articulos = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());

        return new ArticuloPaginaDTO(articulos, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    @Transactional
    public boolean eliminarArticulo(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private ArticuloEntity convertirAEntity(@Valid ArticuloDTO dto, ModeloEntity modelo, CategoriaEntity categoria, UbicacionEntity ubicacion) {
        ArticuloEntity objEntity = new ArticuloEntity();
        objEntity.setCodigoArticulo(dto.getCodigoArticulo());
        objEntity.setModelo(modelo);
        objEntity.setCategoria(categoria);
        objEntity.setUbicacion(ubicacion);
        return objEntity;
    }

    private ArticuloDTO convertirADTO(@Valid ArticuloEntity entity) {
        ArticuloDTO objDTO = new ArticuloDTO();
        objDTO.setIdArticulo(entity.getIdArticulo());
        objDTO.setCodigoArticulo(entity.getCodigoArticulo());

        if (entity.getModelo() != null) {
            objDTO.setIdModelo(entity.getModelo().getIdModelo());
            objDTO.setNombreModelo(entity.getModelo().getNombreModelo());
            objDTO.setNombreMarca(entity.getModelo().getMarca().getNombreMarca());
        }

        objDTO.setIdCategoria(entity.getCategoria().getIdCategoria());
        objDTO.setNombreCategoria(entity.getCategoria().getNombreCategoria());

        objDTO.setIdUbicacion(entity.getUbicacion().getId());
        objDTO.setNombreUbicacion(entity.getUbicacion().getNombreUbicacion());

        return objDTO;
    }
}