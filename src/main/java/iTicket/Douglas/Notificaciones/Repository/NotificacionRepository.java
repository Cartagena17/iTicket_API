package iTicket.Douglas.Notificaciones.Repository;

import iTicket.Douglas.Notificaciones.Entity.NotificacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    List<NotificacionEntity> findByUsuarioDestino_IdUsuarioOrderByFechaHoraDesc(Long idUsuario);

    Page<NotificacionEntity> findByUsuarioDestino_IdUsuarioAndFechaHoraAfterOrderByFechaHoraDesc(Long idUsuario, LocalDateTime desde, Pageable pageable);

    long countByUsuarioDestino_IdUsuarioAndLeida(Long idUsuario, Boolean leida);

    long countByUsuarioDestino_IdUsuarioAndLeidaAndFechaHoraAfter(Long idUsuario, Boolean leida, LocalDateTime desde);
}