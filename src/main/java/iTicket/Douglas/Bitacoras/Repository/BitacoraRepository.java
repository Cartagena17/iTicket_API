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

    //Cuenta los tickets cerrados dentro del rango. Sale de aqui porque TICKETS no guarda la fecha de cierre
    //El DISTINCT evita contar dos veces un ticket que paso por Resuelto y luego por Cerrado en el mismo periodo
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT b.idTicket) " +
            "FROM BitacoraEntity b " +
            "WHERE LOWER(b.nuevoEstado) IN ('resuelto', 'cerrado') " +
            "AND (:inicio IS NULL OR b.fechaHora >= :inicio) " +
            "AND (:fin IS NULL OR b.fechaHora <= :fin)")
    Long contarTicketsCerradosEnRango(
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );

    @org.springframework.data.jpa.repository.Query("SELECT b FROM BitacoraEntity b " +
            "JOIN iTicket.Douglas.Tickets.Entity.TicketEntity t ON t.idTicket = b.idTicket " +
            "WHERE t.departamento.tipoDepartamento = :tipoDepartamento " +
            "ORDER BY b.fechaHora DESC")
    List<BitacoraEntity> obtenerBitacorasPorDepartamento(
            @org.springframework.data.repository.query.Param("tipoDepartamento") String tipoDepartamento
    );

    //b.idTicket no es una relacion JPA, por eso el join manual contra TicketEntity.
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT b.idTicket) " +
            "FROM BitacoraEntity b " +
            "JOIN iTicket.Douglas.Tickets.Entity.TicketEntity t ON t.idTicket = b.idTicket " +
            "WHERE LOWER(b.nuevoEstado) IN ('resuelto', 'cerrado') " +
            "AND t.departamento.tipoDepartamento = :tipoDepartamento " +
            "AND (:inicio IS NULL OR b.fechaHora >= :inicio) " +
            "AND (:fin IS NULL OR b.fechaHora <= :fin)")
    Long contarTicketsCerradosEnRangoPorDepartamento(
            @org.springframework.data.repository.query.Param("tipoDepartamento") String tipoDepartamento,
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );

    @org.springframework.data.jpa.repository.Query("SELECT t.fechaCreacion, MIN(b.fechaHora) " +
            "FROM iTicket.Douglas.Bitacoras.Entity.BitacoraEntity b " +
            "JOIN iTicket.Douglas.Tickets.Entity.TicketEntity t ON t.idTicket = b.idTicket " +
            "WHERE LOWER(b.nuevoEstado) IN ('resuelto', 'cerrado') " +
            "AND LOWER(t.estado) IN ('resuelto', 'cerrado') " +
            "AND b.fechaHora > t.fechaCreacion " +
            "AND t.departamento.tipoDepartamento = :tipoDepartamento " +
            "AND (:inicio IS NULL OR t.fechaCreacion >= :inicio) " +
            "AND (:fin IS NULL OR t.fechaCreacion <= :fin) " +
            "GROUP BY t.idTicket, t.fechaCreacion")
    List<Object[]> obtenerTiemposResolucionReales(
            @org.springframework.data.repository.query.Param("tipoDepartamento") String tipoDepartamento,
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );

    // Promedio de horas de resolución agrupado por día de la semana (1=Domingo...7=Sábado en Oracle)
    // Devuelve: [díaSemanaNumero (Number), promedioHoras (Number)]
    @org.springframework.data.jpa.repository.Query(
            value = "SELECT TO_CHAR(b.FECHA_HORA, 'D') AS dia_semana, " +
                    "AVG((CAST(b.FECHA_HORA AS DATE) - CAST(t.FECHA_CREACION AS DATE)) * 24) AS promedio_horas " +
                    "FROM BITACORAS b " +
                    "JOIN TICKETS t ON t.ID_TICKET = b.ID_TICKET " +
                    "JOIN DEPARTAMENTOS d ON d.ID_DEPARTAMENTO = t.ID_DEPARTAMENTO " +
                    "WHERE LOWER(b.NUEVO_ESTADO) IN ('resuelto', 'cerrado') " +
                    "AND b.FECHA_HORA > t.FECHA_CREACION " +
                    "AND d.TIPO_DEPARTAMENTO = :tipoDepartamento " +
                    "AND (:inicio IS NULL OR t.FECHA_CREACION >= :inicio) " +
                    "AND (:fin IS NULL OR t.FECHA_CREACION <= :fin) " +
                    "GROUP BY TO_CHAR(b.FECHA_HORA, 'D') " +
                    "ORDER BY TO_CHAR(b.FECHA_HORA, 'D')",
            nativeQuery = true)
    java.util.List<Object[]> obtenerTiemposResolucionPorDiaSemana(
            @org.springframework.data.repository.query.Param("tipoDepartamento") String tipoDepartamento,
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );

    @org.springframework.data.jpa.repository.Query(
            value = "SELECT TO_CHAR(b.FECHA_HORA, 'D') AS dia_semana, " +
                    "AVG((CAST(b.FECHA_HORA AS DATE) - CAST(t.FECHA_CREACION AS DATE)) * 24) AS promedio_horas " +
                    "FROM BITACORAS b " +
                    "JOIN TICKETS t ON t.ID_TICKET = b.ID_TICKET " +
                    "WHERE LOWER(b.NUEVO_ESTADO) IN ('resuelto', 'cerrado') " +
                    "AND b.FECHA_HORA > t.FECHA_CREACION " +
                    "AND t.ID_TECNICO_ASIGNADO = :idUsuario " +
                    "AND (:inicio IS NULL OR t.FECHA_CREACION >= :inicio) " +
                    "AND (:fin IS NULL OR t.FECHA_CREACION <= :fin) " +
                    "GROUP BY TO_CHAR(b.FECHA_HORA, 'D') " +
                    "ORDER BY TO_CHAR(b.FECHA_HORA, 'D')",
            nativeQuery = true)
    java.util.List<Object[]> obtenerTiemposResolucionPorDiaSemanaTecnico(
            @org.springframework.data.repository.query.Param("idUsuario") Long idUsuario,
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin
    );

    @org.springframework.data.jpa.repository.Query(
            value = "SELECT AVG((CAST(b.FECHA_HORA AS DATE) - CAST(t.FECHA_CREACION AS DATE)) * 24) " +
                    "FROM BITACORAS b " +
                    "JOIN TICKETS t ON t.ID_TICKET = b.ID_TICKET " +
                    "WHERE LOWER(b.NUEVO_ESTADO) IN ('resuelto', 'cerrado') " +
                    "AND b.FECHA_HORA > t.FECHA_CREACION " +
                    "AND t.ID_CREADOR = :idUsuario",
            nativeQuery = true)
    Double obtenerTiempoPromedioResolucionPorUsuario(
            @org.springframework.data.repository.query.Param("idUsuario") Long idUsuario
    );
}