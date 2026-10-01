package iTicket.Douglas.Fases.Repository;

import iTicket.Douglas.Fases.Entity.FaseEntity;
import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaseRepository extends JpaRepository<FaseEntity, Long> {

    //Metodos personalizados

    //Metodo para obtener fases por nombre fase
    Optional<FaseEntity> findByNombreFase(String nombreFase);

    List<FaseEntity> findByProyecto_IdProyecto(Long proyectoIdProyecto);

    // Para la validacion de nombre de fase unico DENTRO de un mismo proyecto (no global).
    boolean existsByProyecto_IdProyectoAndNombreFaseIgnoreCase(Long idProyecto, String nombreFase);

    boolean existsByProyecto_IdProyectoAndNombreFaseIgnoreCaseAndIdFaseNot(Long idProyecto, String nombreFase, Long idFase);
}
