package iTicket.Douglas.Roles.Repository;

import iTicket.Douglas.Roles.Entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {
    boolean existsByNombreRolIgnoreCase(String nombreRol);
    boolean existsByNombreRolIgnoreCaseAndIdRolNot(String nombreRol, Long idRol);
}
