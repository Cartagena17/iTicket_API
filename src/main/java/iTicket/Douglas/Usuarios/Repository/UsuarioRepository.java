package iTicket.Douglas.Usuarios.Repository;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    //Metodo para buscar usuarios por rol y estado, según el departamento
    List<UsuarioEntity> findByRol_NombreRolInAndEstadoAndDepartamento_NombreDepartamentoIgnoreCase(List<String> roles, Character estado, String nombreDepartamento);
}