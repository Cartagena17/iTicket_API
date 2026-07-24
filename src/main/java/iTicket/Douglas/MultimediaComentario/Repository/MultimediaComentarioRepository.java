package iTicket.Douglas.MultimediaComentario.Repository;

import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MultimediaComentarioRepository extends JpaRepository<MultimediaComentarioEntity, Long> {
    Optional<MultimediaComentarioEntity> findByMultimediaUrl(String multimediaUrl);
}
