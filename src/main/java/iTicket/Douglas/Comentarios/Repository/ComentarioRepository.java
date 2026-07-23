package iTicket.Douglas.Comentarios.Repository;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
}
