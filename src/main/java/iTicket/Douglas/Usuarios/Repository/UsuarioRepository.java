package iTicket.Douglas.Usuarios.Repository;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);

    List<UsuarioEntity> findByRol_IdRol(Long idRol);
    List<UsuarioEntity> findByDepartamento_IdDepartamento(Long idDepartamento);
    List<UsuarioEntity> findByDepartamento_Area_IdArea(Long idArea);
}
