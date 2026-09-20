package iTicket.Douglas.Notificaciones.Service;

import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Notificaciones.DTO.NotificacionDTO;
import iTicket.Douglas.Notificaciones.DTO.NotificacionPaginaDTO;
import iTicket.Douglas.Notificaciones.Entity.NotificacionEntity;
import iTicket.Douglas.Notificaciones.Repository.NotificacionRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository repo;
    private final UsuarioRepository usuarioRepo;

    //Método interno, lo llama el NotificacionListener, no un controller público
    @Transactional(propagation = Propagation.REQUIRES_NEW) //Se ejecuta de forma independiente, sin importar si hay una transaccion activa, y si falla no afecta a la otra transaccion
    public void crearNotificacion(Long idUsuarioDestino, String tipo, String titulo, String mensaje, String tipoEntidad, Long idEntidad) {
        UsuarioEntity usuario = usuarioRepo.findById(idUsuarioDestino).orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + idUsuarioDestino));

        NotificacionEntity entity = new NotificacionEntity();
        entity.setUsuarioDestino(usuario);
        entity.setTipo(tipo);
        entity.setTitulo(titulo);
        entity.setMensaje(mensaje);
        entity.setTipoEntidad(tipoEntidad);
        entity.setIdEntidad(idEntidad);
        entity.setLeida(false);

        repo.save(entity);
        log.info("Notificación '" + tipo + "' creada para usuario " + idUsuarioDestino);
    }

    public List<NotificacionDTO> obtenerPorUsuario(Long idUsuario) {
        return repo.findByUsuarioDestino_IdUsuarioOrderByFechaHoraDesc(idUsuario).stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private static final int DIAS_HISTORIAL_NOTIFICACIONES = 30;

    //solo trae lo del ultimo mes
    public NotificacionPaginaDTO obtenerPorUsuarioPaginado(Long idUsuario, int pagina, int tamano) {
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_HISTORIAL_NOTIFICACIONES);
        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("fechaHora").descending());

        Page<NotificacionEntity> resultado = repo.findByUsuarioDestino_IdUsuarioAndFechaHoraAfterOrderByFechaHoraDesc(idUsuario, desde, pageable);

        List<NotificacionDTO> notificaciones = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
        return new NotificacionPaginaDTO(notificaciones, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    //Se limita a la misma ventana de tiempo que ve el panel, para que el numero no cuente cosas que el usuario no puede abrir
    public long contarNoLeidas(Long idUsuario) {
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_HISTORIAL_NOTIFICACIONES);
        return repo.countByUsuarioDestino_IdUsuarioAndLeidaAndFechaHoraAfter(idUsuario, false, desde);
    }

    @Transactional
    public NotificacionDTO marcarComoLeida(Long idNotificacion, Long idUsuarioSolicitante) {
        NotificacionEntity entity = repo.findById(idNotificacion).orElseThrow(() -> new RecursoNoEncontradoException("No existe una notificación con id " + idNotificacion));

        //Para que otros usuario no puedan marcar las notificaciones de otras como leidas
        if (!entity.getUsuarioDestino().getIdUsuario().equals(idUsuarioSolicitante)) {
            throw new OperacionInvalidaException("Esta notificación no pertenece al usuario solicitante.");
        }

        entity.setLeida(true);
        return convertirADTO(repo.save(entity));
    }

    @Transactional
    public void marcarTodasComoLeidas(Long idUsuario) {
        List<NotificacionEntity> pendientes = repo.findByUsuarioDestino_IdUsuarioOrderByFechaHoraDesc(idUsuario).stream().filter(n -> !Boolean.TRUE.equals(n.getLeida())).collect(Collectors.toList());
        pendientes.forEach(n -> n.setLeida(true));
        repo.saveAll(pendientes);
    }

    private NotificacionDTO convertirADTO(NotificacionEntity entity) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(entity.getIdNotificacion());
        dto.setIdUsuarioDestino(entity.getUsuarioDestino().getIdUsuario());
        dto.setTipo(entity.getTipo());
        dto.setTitulo(entity.getTitulo());
        dto.setMensaje(entity.getMensaje());
        dto.setTipoEntidad(entity.getTipoEntidad());
        dto.setIdEntidad(entity.getIdEntidad());
        dto.setLeida(entity.getLeida());
        dto.setFechaHora(entity.getFechaHora());
        return dto;
    }
}