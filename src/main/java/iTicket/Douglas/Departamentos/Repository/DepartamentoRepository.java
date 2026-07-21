package iTicket.Douglas.Departamentos.Repository;

import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<DepartamentoEntity, Long>{
    boolean existsByNombreDepartamento(@NotBlank String nombreDepartamento);
}
