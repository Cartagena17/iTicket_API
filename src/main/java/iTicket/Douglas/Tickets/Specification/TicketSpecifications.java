package iTicket.Douglas.Tickets.Specification;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketSpecifications {

    public static Specification<TicketEntity> conDepartamento(String nombreDepartamento){
        return (root, query, cb) -> cb.equal(cb.upper(root.get("departamento").get("nombreDepartamento")), nombreDepartamento.toUpperCase());
    }

    public static Specification<TicketEntity> conTecnico(Long idUsuario){
        return (root, query, cb) -> cb.equal(root.get("tecnicoAsignado").get("idUsuario"), idUsuario);
    }

    public static Specification<TicketEntity> conUsuario(Long idUsuario){
        return (root, query, cb) -> cb.equal(root.get("creador").get("idUsuario"), idUsuario);
    }

    public static Specification<TicketEntity> conBusqueda(String texto){
        return (root, query, cb) -> {String comodin = "%" + texto.toUpperCase() + "%";
            return cb.or(
                    cb.like(cb.upper(root.get("codigo")), comodin),
                    cb.like(cb.upper(root.get("asunto")), comodin)
            );
        };
    }

    public static Specification<TicketEntity> conPrioridad(String prioridad) {
        return (root, query, cb) -> cb.equal(root.get("prioridad"), prioridad);
    }

    public static Specification<TicketEntity> conEstado(String estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<TicketEntity> conFechaCreacion(LocalDate fecha) {
        return (root, query, cb) -> {
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("fechaCreacion"), inicioDia),
                    cb.lessThan(root.get("fechaCreacion"), finDia)
            );
        };
    }
}