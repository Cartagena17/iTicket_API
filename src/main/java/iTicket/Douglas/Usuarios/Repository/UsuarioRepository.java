package iTicket.Douglas.Usuarios.Repository;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    //Busca usuarios por rol y estado, según la FUNCION de su departamento.
    //Por tipo y no por nombre, el personal de IT es el mismo para todas las areas
    List<UsuarioEntity> findByRol_NombreRolInAndEstadoAndDepartamento_TipoDepartamento(List<String> roles, Boolean estado, String tipoDepartamento);

    //Usado para notificar al admin del departamento dueño de un ticket
    List<UsuarioEntity> findByRol_NombreRolAndDepartamento_IdDepartamento(String rol, Long idDepartamento);

    List<UsuarioEntity> findByDepartamento_TipoDepartamentoIn(List<String> tipos);
}