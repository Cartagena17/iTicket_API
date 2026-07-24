package iTicket.Douglas.Comentarios.Repository;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Ubicaciones.Entity.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
    Optional<ComentarioEntity> findByComentario(String comentario);
}
