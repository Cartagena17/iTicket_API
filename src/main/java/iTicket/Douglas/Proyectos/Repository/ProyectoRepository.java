package iTicket.Douglas.Proyectos.Repository;

import iTicket.Douglas.Proyectos.Entity.ProyectoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoEntity, Long> {

    //Metodo personalizado para buscar proyectos por medio del nombre
    List<ProyectoEntity> findByNombreProyectoContainingIgnoreCase(String nombre);

    //Metodo personalizado para buscar proyectos por medio del tipo
    List<ProyectoEntity> findByTipoProyecto(String nombre);
}
