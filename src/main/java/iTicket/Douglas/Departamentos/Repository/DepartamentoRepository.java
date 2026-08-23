package iTicket.Douglas.Departamentos.Repository;

import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<DepartamentoEntity, Long>{

    //Departamentos que reciben tickets, sin filtrar por area: IT y Mantenimiento estan en Ricaldone y atienden tambien a CFP
    @Query("SELECT d FROM DepartamentoEntity d WHERE d.tipoDepartamento <> 'Otro'")
    List<DepartamentoEntity> findAsignables();
}
