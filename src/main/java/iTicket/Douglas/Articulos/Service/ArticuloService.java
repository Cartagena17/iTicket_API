package iTicket.Douglas.Articulos.Service;

import iTicket.Douglas.Articulos.DTO.ArticuloDTO;
import iTicket.Douglas.Articulos.DTO.ArticuloPaginaDTO;
import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.Articulos.Specification.ArticuloSpecifications;
import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import iTicket.Douglas.Categoria.Repository.CategoriaRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final ArticuloRepository repo;
    private final ModeloRepository modelorepo;
    private final CategoriaRepository categoriaRepo;
    private final UbicacionRepository ubicacionRepo;

    public ArticuloDTO nuevoArticulo (@Valid ArticuloDTO dto){
        try {
            Optional<CategoriaEntity> categoriaOpcional = categoriaRepo.findById(dto.getIdCategoria());
            if (categoriaOpcional.isEmpty()){
                log.warn("no existe la categoria con id"+ dto.getIdCategoria());
                return null;
            }
            Optional<UbicacionEntity> ubicacionOpcional = ubicacionRepo.findById(dto.getIdUbicacion());
            if (ubicacionOpcional.isEmpty()){
                log.warn("no existe la ubicacion con id"+ dto.getIdUbicacion());
                return null;
            }

            ModeloEntity modelo = null;
            if (dto.getIdModelo() != null){
                Optional<ModeloEntity> modeloOpcional = modelorepo.findById(dto.getIdModelo());
                if (modeloOpcional.isEmpty()){
                    log.warn("no existe el modelo con id"+ dto.getIdModelo());
                    return null;
                }
                modelo = modeloOpcional.get();
            }

            ArticuloEntity entity = convertirAEntity(dto, modelo, categoriaOpcional.get(), ubicacionOpcional.get());
            ArticuloEntity entitySave = repo.save(entity);
            log.info("Nuevo articulo registrado");
            return convertirADTO(entitySave);
        } catch (Exception e) {
            log.error("Error al ingresar la informacion del articulo " + e.getMessage());
            return null;
        }
    }

    public List<ArticuloDTO> obtenerTodo(){
        List<ArticuloEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ArticuloDTO obtenerPorId (Long id){
        Optional<ArticuloEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public ArticuloDTO actualizarData(Long id,@Valid ArticuloDTO dto){
        try {
            Optional<ArticuloEntity> registroExistente = repo.findById(id);
            if (registroExistente.isEmpty()){
                return null;
            }
            ArticuloEntity entidad = registroExistente.get();

            if (dto.getIdCategoria() != null){
                Optional<CategoriaEntity> categoriaOpcional = categoriaRepo.findById(dto.getIdCategoria());
                if (categoriaOpcional.isEmpty()){
                    log.warn("no existe la categoria con id"+ dto.getIdCategoria());
                    return null;
                }
                entidad.setCategoria(categoriaOpcional.get());
            }

            if (dto.getIdUbicacion() != null){
                Optional<UbicacionEntity> ubicacionOpcional = ubicacionRepo.findById(dto.getIdUbicacion());
                if (ubicacionOpcional.isEmpty()){
                    log.warn("no existe la ubicacion con id"+ dto.getIdUbicacion());
                    return null;
                }
                entidad.setUbicacion(ubicacionOpcional.get());
            }

            if (dto.getIdModelo() != null){
                Optional<ModeloEntity> modeloOpcional = modelorepo.findById(dto.getIdModelo());
                if (modeloOpcional.isEmpty()){
                    log.warn("no existe el modelo con id"+ dto.getIdModelo());
                    return null;
                }
                entidad.setModelo(modeloOpcional.get());
            }

            entidad.setCodigoArticulo(dto.getCodigoArticulo());
            ArticuloEntity datosGuardados = repo.save(entidad);
            log.info("Articulo actualizado");
            return convertirADTO(datosGuardados);
        } catch (Exception e) {
            log.error("Error al actualizar la informacion del articulo " + e.getMessage());
            return null;
        }
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

        // pagina - 1 porque Spring Data cuenta las páginas desde 0, pero en la interfaz la primera es la 1
        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idArticulo").descending());
        Page<ArticuloEntity> resultado = repo.findAll(spec, pageable);

        List<ArticuloDTO> articulos = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());

        return new ArticuloPaginaDTO(articulos, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

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