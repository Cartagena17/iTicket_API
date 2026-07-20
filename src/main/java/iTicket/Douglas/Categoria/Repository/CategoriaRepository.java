package iTicket.Douglas.Categoria.Repository;

import iTicket.Douglas.Categoria.Entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {

    //Metodos personalizados
    Optional<CategoriaEntity> findByNombreCategoria(String nombreCategoria);
}
