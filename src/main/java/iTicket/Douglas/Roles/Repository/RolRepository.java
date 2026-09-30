package iTicket.Douglas.Roles.Repository;

import iTicket.Douglas.Roles.Entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {
    boolean existsByNombreRolIgnoreCase(String nombreRol);
    boolean existsByNombreRolIgnoreCaseAndIdRolNot(String nombreRol, Long idRol);

    // La usa el SetupService para ubicar el rol Administrador sin depender de que su id sea 1
    Optional<RolEntity> findByNombreRolIgnoreCase(String nombreRol);
}
