package iTicket.Douglas.Usuarios.Repository;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    //Busca usuarios por rol y estado, según la FUNCION de su departamento.
    //Por tipo y no por nombre: el personal de IT es el mismo para todas las areas,
    //así que un técnico de IT de Ricaldone también atiende tickets de IT de CFP.
    List<UsuarioEntity> findByRol_NombreRolInAndEstadoAndDepartamento_TipoDepartamento(List<String> roles, Character estado, String tipoDepartamento);
}