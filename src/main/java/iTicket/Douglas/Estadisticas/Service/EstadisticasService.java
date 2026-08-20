package iTicket.Douglas.Estadisticas.Service;

import iTicket.Douglas.Bitacoras.Repository.BitacoraRepository;
import iTicket.Douglas.Evaluaciones.Entity.EvaluacionesEntity;
import iTicket.Douglas.Evaluaciones.Repository.EvaluacionesRepository;
import iTicket.Douglas.Response.*;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class EstadisticasService {

    private final TicketRepository ticketRepository;
    private final EvaluacionesRepository evaluacionesRepository;
    private final BitacoraRepository bitacoraRepository;

    @Autowired
    public EstadisticasService(TicketRepository ticketRepository,
                               EvaluacionesRepository evaluacionesRepository,
                               BitacoraRepository bitacoraRepository) {
        this.ticketRepository = ticketRepository;
        this.evaluacionesRepository = evaluacionesRepository;
        this.bitacoraRepository = bitacoraRepository;
    }

    public MetricasResponseDTO obtenerMetricas(LocalDate fechaInicio, LocalDate fechaFin,
                                               Pageable pageableAlertas, Pageable pageableEquipos) {
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        MetricasResponseDTO metricas = new MetricasResponseDTO();

        // 1. Estadísticas de Tickets
        EstadisticasTicketDTO statsTickets = new EstadisticasTicketDTO();
        long totalTickets = ticketRepository.contarTicketsTotalesRangoFechas(inicio, fin);
        statsTickets.setTotalTickets(totalTickets);

        List<Object[]> conteoPorEstado = ticketRepository.contarTicketsPorEstadoRangoFechas(inicio, fin);
        long nuevos = 0, enProceso = 0, resueltos = 0, cancelados = 0;

        if (conteoPorEstado != null) {
            for (Object[] fila : conteoPorEstado) {
                if (fila != null && fila.length >= 2) {
                    String estado = (String) fila[0];
                    Number conteoNum = (Number) fila[1];
                    long conteo = conteoNum != null ? conteoNum.longValue() : 0L;

                    if (estado != null) {
                        switch (estado.toLowerCase()) {
                            case "nuevo":
                            case "nuevos":
                                nuevos += conteo;
                                break;
                            case "en proceso":
                            case "proceso":
                                enProceso += conteo;
                                break;
                            case "resuelto":
                            case "resueltos":

                            case "cerrado":
                            case "cerrados":
                                resueltos += conteo;
                                break;
                            case "cancelado":
                            case "cancelados":
                                cancelados += conteo;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        statsTickets.setTicketsNuevos(nuevos);
        statsTickets.setTicketsEnProceso(enProceso);
        statsTickets.setTicketsResueltos(resueltos);
        statsTickets.setTicketsCancelados(cancelados);

        // Tiempo Medio de Resolución — calculado desde Bitácoras filtrando solo tickets Resueltos/Cerrados
        // Se usa MIN(fechaHora) para tomar la primera vez que el ticket fue marcado como resuelto,
        // evitando que reaperturas o ediciones posteriores inflen el resultado.
        List<Object[]> tiemposResolucion = bitacoraRepository.obtenerTiemposResolucionReales(inicio, fin);
        if (tiemposResolucion != null && !tiemposResolucion.isEmpty()) {
            long totalHoras = 0;
            long count = 0;
            for (Object[] row : tiemposResolucion) {
                if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                    LocalDateTime fechaCreacion = (LocalDateTime) row[0];
                    LocalDateTime fechaResolucion = (LocalDateTime) row[1];
                    // Guardia de seguridad: solo calcular si la resolución es posterior a la creación
                    if (fechaResolucion.isAfter(fechaCreacion)) {
                        long horas = Duration.between(fechaCreacion, fechaResolucion).toHours();
                        totalHoras += horas;
                        count++;
                    }
                }
            }
            if (count > 0) {
                // Redondear a 1 decimal para mayor legibilidad en el Frontend
                double promedio = (double) totalHoras / count;
                statsTickets.setTiempoMedioResolucionHoras(Math.round(promedio * 10.0) / 10.0);
            } else {
                statsTickets.setTiempoMedioResolucionHoras(0.0);
            }
        } else {
            statsTickets.setTiempoMedioResolucionHoras(0.0);
        }
        metricas.setTickets(statsTickets);

        // 2. Estadísticas de Evaluaciones
        EstadisticasEvaluacionesDTO statsEvaluaciones = new EstadisticasEvaluacionesDTO();
        Object[] metricasEv = evaluacionesRepository.obtenerMetricasDashboard(inicio, fin);

        if (metricasEv != null && metricasEv.length >= 6) {
            statsEvaluaciones.setTotalEvaluaciones(metricasEv[0] != null ? ((Number) metricasEv[0]).longValue() : 0L);
            statsEvaluaciones.setPromedioCsat(metricasEv[1] != null ? ((Number) metricasEv[1]).doubleValue() : 0.0);
            statsEvaluaciones.setEstrellas5(metricasEv[2] != null ? ((Number) metricasEv[2]).longValue() : 0L);
            statsEvaluaciones.setEstrellas4(metricasEv[3] != null ? ((Number) metricasEv[3]).longValue() : 0L);
            statsEvaluaciones.setEstrellas3(metricasEv[4] != null ? ((Number) metricasEv[4]).longValue() : 0L);
            statsEvaluaciones.setEstrellas1y2(metricasEv[5] != null ? ((Number) metricasEv[5]).longValue() : 0L);
        } else if (metricasEv != null && metricasEv.length == 1 && metricasEv[0] instanceof Object[]) {
            Object[] row = (Object[]) metricasEv[0];
            if (row.length >= 6) {
                statsEvaluaciones.setTotalEvaluaciones(row[0] != null ? ((Number) row[0]).longValue() : 0L);
                statsEvaluaciones.setPromedioCsat(row[1] != null ? ((Number) row[1]).doubleValue() : 0.0);
                statsEvaluaciones.setEstrellas5(row[2] != null ? ((Number) row[2]).longValue() : 0L);
                statsEvaluaciones.setEstrellas4(row[3] != null ? ((Number) row[3]).longValue() : 0L);
                statsEvaluaciones.setEstrellas3(row[4] != null ? ((Number) row[4]).longValue() : 0L);
                statsEvaluaciones.setEstrellas1y2(row[5] != null ? ((Number) row[5]).longValue() : 0L);
            }
        }
        metricas.setEvaluaciones(statsEvaluaciones);

        // 3. Equipos Más Reportados (paginado)
        org.springframework.data.domain.Page<Object[]> articulosPage =
                ticketRepository.obtenerArticulosMasReportadosRangoFechas(inicio, fin, pageableEquipos);
        List<EquipoReportadoDTO> equiposList = new ArrayList<>();

        for (Object[] row : articulosPage.getContent()) {
            if (row != null && row.length >= 3) {
                EquipoReportadoDTO eq = new EquipoReportadoDTO();
                eq.setCodigoArticulo((String) row[0]);
                eq.setModelo((String) row[1]);
                eq.setCantidadReportes(row[2] != null ? ((Number) row[2]).longValue() : 0L);
                equiposList.add(eq);
            }
        }
        metricas.setEquiposMasReportados(new PaginatedResponseDTO<>(
                equiposList,
                articulosPage.getNumber(),
                articulosPage.getTotalPages(),
                articulosPage.getTotalElements()
        ));

        // 4. Alertas de Insatisfacción (paginado)
        org.springframework.data.domain.Page<EvaluacionesEntity> alertasPage =
                evaluacionesRepository.obtenerAlertasInsatisfaccion(inicio, fin, pageableAlertas);
        List<AlertaInsatisfaccionDTO> alertasList = new ArrayList<>();

        for (EvaluacionesEntity ev : alertasPage.getContent()) {
            AlertaInsatisfaccionDTO alerta = new AlertaInsatisfaccionDTO();
            alerta.setCalificacion(ev.getCalificacion());
            alerta.setComentario(ev.getComentario());

            if (ev.getTicket() != null) {
                alerta.setCodigoTicket(ev.getTicket().getCodigo());
                if (ev.getTicket().getCreador() != null) {
                    alerta.setUsuario(ev.getTicket().getCreador().getNombreUsuario());
                }
                if (ev.getTicket().getTecnicoAsignado() != null) {
                    alerta.setTecnico(ev.getTicket().getTecnicoAsignado().getNombreUsuario());
                }
            }
            alertasList.add(alerta);
        }
        metricas.setAlertas(new PaginatedResponseDTO<>(
                alertasList,
                alertasPage.getNumber(),
                alertasPage.getTotalPages(),
                alertasPage.getTotalElements()
        ));

        // 5. Tickets por Prioridad
        List<Object[]> ticketsPrioridadData = ticketRepository.contarTicketsPorPrioridadRangoFechas(inicio, fin);
        List<TicketPrioridadDTO> prioridadesList = new ArrayList<>();

        if (ticketsPrioridadData != null) {
            for (Object[] row : ticketsPrioridadData) {
                if (row != null && row.length >= 2) {
                    TicketPrioridadDTO tp = new TicketPrioridadDTO();
                    tp.setPrioridad(row[0] != null ? (String) row[0] : "Sin Prioridad");
                    tp.setCantidad(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                    prioridadesList.add(tp);
                }
            }
        }
        metricas.setTicketsPorPrioridad(prioridadesList);

        // 6. Tickets por Mes (Generado mes a mes para seguridad)
        List<TicketMesDTO> ticketsMesList = new ArrayList<>();

        LocalDate fechaIter = fechaInicio != null ? fechaInicio.withDayOfMonth(1) : LocalDate.now().withMonth(1).withDayOfMonth(1);
        LocalDate fechaFinIter = fechaFin != null ? fechaFin : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        while (!fechaIter.isAfter(fechaFinIter)) {
            LocalDateTime inicioMes = fechaIter.atStartOfDay();
            LocalDateTime finMes = fechaIter.withDayOfMonth(fechaIter.lengthOfMonth()).atTime(LocalTime.MAX);

            TicketMesDTO mesDTO = new TicketMesDTO();
            mesDTO.setMes(fechaIter.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
            mesDTO.setYear(fechaIter.getYear());

            long creadosMes = ticketRepository.contarTicketsTotalesRangoFechas(inicioMes, finMes);
            mesDTO.setCreados(creadosMes);

            long vencidosMes = ticketRepository.contarTicketsVencidosRangoFechas(inicioMes, finMes);
            mesDTO.setVencidos(vencidosMes);

            List<Object[]> conteoPorEstadoMes = ticketRepository.contarTicketsPorEstadoRangoFechas(inicioMes, finMes);
            long resueltosMes = 0;
            if (conteoPorEstadoMes != null) {
                for (Object[] fila : conteoPorEstadoMes) {
                    if (fila != null && fila.length >= 2 && fila[0] != null) {
                        String estado = (String) fila[0];
                        if (estado.equalsIgnoreCase("resuelto") || estado.equalsIgnoreCase("cerrado") || estado.equalsIgnoreCase("resueltos")) {
                            resueltosMes += ((Number) fila[1]).longValue();
                        }
                    }
                }
            }
            mesDTO.setResueltos(resueltosMes);

            ticketsMesList.add(mesDTO);

            fechaIter = fechaIter.plusMonths(1);
        }
        metricas.setTicketsPorMes(ticketsMesList);

        // 7. Satisfacción por Técnico
        List<Object[]> satisfaccionData = evaluacionesRepository.obtenerPromedioSatisfaccionPorTecnico(inicio, fin);
        List<SatisfaccionTecnicoDTO> satisfaccionList = new ArrayList<>();

        if (satisfaccionData != null) {
            for (Object[] row : satisfaccionData) {
                if (row != null && row.length >= 2) {
                    SatisfaccionTecnicoDTO st = new SatisfaccionTecnicoDTO();
                    st.setTecnico(row[0] != null ? (String) row[0] : "Desconocido");

                    double promedioRaw = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    // Redondear a 1 decimal
                    st.setPromedio(Math.round(promedioRaw * 10.0) / 10.0);

                    satisfaccionList.add(st);
                }
            }
        }
        metricas.setSatisfaccionPorTecnico(satisfaccionList);

        return metricas;
    }
}
