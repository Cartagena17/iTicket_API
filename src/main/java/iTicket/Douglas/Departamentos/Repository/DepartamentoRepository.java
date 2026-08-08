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

    //Trae solo los departamentos asignables a tickets (IT y Mantenimiento) de una área específica
    //Se usa UPPER() para no depender de mayúsculas/minúsculas exactas guardadas en BD
    @Query("SELECT d FROM DepartamentoEntity d " +
            "WHERE UPPER(d.nombreDepartamento) IN ('IT', 'MANTENIMIENTO') " +
            "AND d.area.idArea = :idArea")
    List<DepartamentoEntity> findAsignablesPorArea(@Param("idArea") Long idArea);
}
