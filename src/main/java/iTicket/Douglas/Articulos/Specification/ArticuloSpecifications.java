package iTicket.Douglas.Articulos.Specification;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import org.springframework.data.jpa.domain.Specification;

public class ArticuloSpecifications {

    // Búsqueda por código de artículo (coincidencia parcial, sin importar mayúsculas/minúsculas)
    public static Specification<ArticuloEntity> conBusqueda(String texto) {
        return (root, query, cb) -> {
            String comodin = "%" + texto.toUpperCase() + "%";
            return cb.like(cb.upper(root.get("codigoArticulo")), comodin);
        };
    }

    public static Specification<ArticuloEntity> conCategoria(Long idCategoria) {
        return (root, query, cb) -> cb.equal(root.get("categoria").get("idCategoria"), idCategoria);
    }

    public static Specification<ArticuloEntity> conUbicacion(Long idUbicacion) {
        return (root, query, cb) -> cb.equal(root.get("ubicacion").get("id"), idUbicacion);
    }

    public static Specification<ArticuloEntity> conMarca(Long idMarca) {
        return (root, query, cb) -> cb.equal(root.get("modelo").get("marca").get("idMarca"), idMarca);
    }
}
