package iTicket.Douglas.Bitacoras.Repository;

import iTicket.Douglas.Bitacoras.Entity.BitacoraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BitacoraRepository extends JpaRepository<BitacoraEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener bitacora por id de ticket
    List<BitacoraEntity> findByIdTicketOrderByFechaHoraAsc(Long ticket);

    @org.springframework.data.jpa.repository.Query("SELECT t.fechaCreacion, MIN(b.fechaHora) " +
           "FROM iTicket.Douglas.Bitacoras.Entity.BitacoraEntity b " +
           "JOIN iTicket.Douglas.Tickets.Entity.TicketEntity t ON t.idTicket = b.idTicket " +
           "WHERE LOWER(b.nuevoEstado) IN ('resuelto', 'cerrado') " +
           "AND LOWER(t.estado) IN ('resuelto', 'cerrado') " +
           "AND b.fechaHora > t.fechaCreacion " +
           "AND (:inicio IS NULL OR t.fechaCreacion >= :inicio) " +
           "AND (:fin IS NULL OR t.fechaCreacion <= :fin) " +
           "GROUP BY t.idTicket, t.fechaCreacion")
    List<Object[]> obtenerTiemposResolucionReales(
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );
}
