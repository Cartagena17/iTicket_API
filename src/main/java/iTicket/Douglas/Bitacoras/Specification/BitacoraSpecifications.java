package iTicket.Douglas.Bitacoras.Specification;

import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import org.springframework.data.jpa.domain.Specification;

public class BitacoraSpecifications {

    public static Specification<BitacoraEntity> conTipoDepartamento(String tipoDepartamento) {
        return (root, query, cb) -> cb.equal(root.get("tipoDepartamentoTicket"), tipoDepartamento);
    }

    public static Specification<BitacoraEntity> conEstado(String estado) {
        return (root, query, cb) -> cb.equal(root.get("nuevoEstado"), estado);
    }

    public static Specification<BitacoraEntity> conBusqueda(String texto) {
        return (root, query, cb) -> {
            String comodin = "%" + texto.toUpperCase() + "%";
            return cb.or(
                    cb.like(cb.upper(root.get("codigoTicket")), comodin),
                    cb.like(cb.upper(root.get("asuntoTicket")), comodin),
                    cb.like(cb.upper(root.get("usuario").get("nombreUsuario")), comodin)
            );
        };
    }
}
