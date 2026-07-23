package iTicket.Douglas.MultimediaComentario.Repository;

import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultimediaComentarioRepository extends JpaRepository<MultimediaComentarioEntity, Long> {
}
