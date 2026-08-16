package iTicket.Douglas.DetalleFases.Repository;

import iTicket.Douglas.DetalleFases.Entity.DetalleFEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFRepository extends JpaRepository<DetalleFEntity, Long> {

    //metodos personalizados

    //metodo para obtener la lista de detalles de fase
    List<DetalleFEntity> findByFase_idFase(Long faseIdFase);
}
