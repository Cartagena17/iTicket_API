package iTicket.Douglas.Tickets.Specification;

import iTicket.Douglas.Tickets.Entity.TicketEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketSpecifications {

    //Filtra por tipo y no por nombre, para que el admin vea sus tickets de todas las areas. 'Otro' no trae nada
    public static Specification<TicketEntity> conTipoDepartamento(String tipoDepartamento){
        if (tipoDepartamento == null || "Otro".equalsIgnoreCase(tipoDepartamento)) {
            return (root, query, cb) -> cb.disjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("departamento").get("tipoDepartamento"), tipoDepartamento);
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

    //Para el panel "Mi resumen" del dashboard admin, tickets activos (no finalizados, no vencidos aun)
    public static Specification<TicketEntity> conEstadosActivos(java.util.List<String> estados) {
        return (root, query, cb) -> root.get("estado").in(estados);
    }

    //Tickets cuya fecha de vencimiento cae dentro del dia de hoy
    public static Specification<TicketEntity> conVenceHoy() {
        return (root, query, cb) -> {
            LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
            LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();
            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("fechaVencimiento"), inicioHoy),
                    cb.lessThan(root.get("fechaVencimiento"), finHoy)
            );
        };
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