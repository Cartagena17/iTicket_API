package iTicket.Douglas.Usuarios.Service;

import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Roles.Entity.RolEntity;
import iTicket.Douglas.Roles.Repository.RolRepository;
import iTicket.Douglas.Usuarios.DTO.UsuarioDTO;
import iTicket.Douglas.Usuarios.DTO.UsuarioPatchDTO;
import iTicket.Douglas.Usuarios.DTO.UsuarioUpdateDTO;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import iTicket.Douglas.Utils.PasswordUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final RolRepository rolRepo;
    private final DepartamentoRepository departamentoRepo;
    private final PasswordUtil passwordUtil;

    @Transactional
    public UsuarioDTO nuevoUsuario(@Valid UsuarioDTO dto) {
        RolEntity rol = rolRepo.findById(dto.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol con id " + dto.getIdRol() + " no existe"));

        DepartamentoEntity departamento = departamentoRepo.findById(dto.getIdDepartamento())
                .orElseThrow(() -> new RecursoNoEncontradoException("El departamento con id " + dto.getIdDepartamento() + " no existe"));

        validarDepartamentoSegunRol(rol, departamento);

        UsuarioEntity entity = convertirAEntity(dto, rol, departamento);
        UsuarioEntity entitySave = repo.save(entity);
        log.info("Nuevo usuario registrado: " + entitySave.getIdUsuario());
        return convertirADTO(entitySave);
    }

    public List<UsuarioDTO> obtenerTodo() {
        List<UsuarioEntity> lista = repo.findAll();
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        UsuarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public UsuarioDTO actualizarData(Long id, @Valid UsuarioUpdateDTO dto) {
        UsuarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + id));

        RolEntity rol = rolRepo.findById(dto.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol con id " + dto.getIdRol() + " no existe"));

        DepartamentoEntity departamento = departamentoRepo.findById(dto.getIdDepartamento())
                .orElseThrow(() -> new RecursoNoEncontradoException("El departamento con id " + dto.getIdDepartamento() + " no existe"));

        validarDepartamentoSegunRol(rol, departamento);

        entidad.setNombreUsuario(dto.getNombreUsuario());
        entidad.setCorreo(dto.getCorreo());
        entidad.setImagenUrl(dto.getImagenUrl());
        entidad.setRol(rol);
        entidad.setDepartamento(departamento);

        if (dto.getClave() != null && !dto.getClave().isBlank()) {
            entidad.setClave(passwordUtil.encriptar(dto.getClave()));
        }

        UsuarioEntity datosGuardados = repo.save(entidad);
        log.info("Usuario con id " + id + " actualizado");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public UsuarioDTO actualizarParcial(Long id, UsuarioPatchDTO dto) {
        // Bug corregido: antes decía "if (registroExistente != null)", que siempre es true
        // (un Optional nunca es null), así que el .get() de abajo podía explotar sin control.
        UsuarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + id));

        if (dto.getNombreUsuario() != null && !dto.getNombreUsuario().isBlank()) {
            entidad.setNombreUsuario(dto.getNombreUsuario());
        }
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            entidad.setCorreo(dto.getCorreo());
        }
        if (dto.getClave() != null && !dto.getClave().isBlank()) {
            entidad.setClave(passwordUtil.encriptar(dto.getClave()));
        }
        if (dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank()) {
            entidad.setImagenUrl(dto.getImagenUrl());
        }
        if (dto.getIdRol() != null) {
            RolEntity rol = rolRepo.findById(dto.getIdRol())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El rol con id " + dto.getIdRol() + " no existe"));
            entidad.setRol(rol);
        }
        if (dto.getIdDepartamento() != null) {
            DepartamentoEntity departamento = departamentoRepo.findById(dto.getIdDepartamento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El departamento con id " + dto.getIdDepartamento() + " no existe"));
            entidad.setDepartamento(departamento);
        }

        //Se valida el resultado final: el PATCH puede cambiar rol y departamento por separado
        validarDepartamentoSegunRol(entidad.getRol(), entidad.getDepartamento());

        UsuarioEntity datosGuardados = repo.save(entidad);
        log.info("Usuario con id " + id + " actualizado parcialmente");
        return convertirADTO(datosGuardados);
    }

    @Transactional
    public boolean eliminarUsuario(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UsuarioDTO> obtenerTecnicosPorDepartamento(Long idDepartamento) {
        DepartamentoEntity departamento = departamentoRepo.findById(idDepartamento)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún departamento con id: " + idDepartamento));

        // Un departamento 'Otro' no atiende tickets, no tiene tecnicos que asignar
        String tipo = departamento.getTipoDepartamento();
        if (tipo == null || "Otro".equalsIgnoreCase(tipo)) {
            return List.of();
        }

        List<UsuarioEntity> lista = repo.findByRol_NombreRolInAndEstadoAndDepartamento_TipoDepartamento(
                List.of("Tecnico", "Administrador"), 'T', departamento.getTipoDepartamento());
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    //Un tecnico o administrador atiende tickets, asi que no puede estar en un departamento 'Otro'
    private void validarDepartamentoSegunRol(RolEntity rol, DepartamentoEntity departamento) {
        boolean atiendeTickets = "Tecnico".equalsIgnoreCase(rol.getNombreRol())
                || "Administrador".equalsIgnoreCase(rol.getNombreRol());

        if (atiendeTickets && "Otro".equalsIgnoreCase(departamento.getTipoDepartamento())) {
            throw new OperacionInvalidaException("Un " + rol.getNombreRol() + " no puede pertenecer a "
                    + departamento.getNombreDepartamento() + ", porque ese departamento no atiende tickets.");
        }
    }

    private UsuarioEntity convertirAEntity(UsuarioDTO dto, RolEntity rol, DepartamentoEntity departamento) {
        UsuarioEntity objEntity = new UsuarioEntity();
        objEntity.setNombreUsuario(dto.getNombreUsuario());
        objEntity.setCorreo(dto.getCorreo());
        objEntity.setClave(passwordUtil.encriptar(dto.getClave()));
        objEntity.setImagenUrl(dto.getImagenUrl());
        objEntity.setRol(rol);
        objEntity.setDepartamento(departamento);
        objEntity.setEstado(dto.getEstado());
        return objEntity;
    }

    private UsuarioDTO convertirADTO(UsuarioEntity entity) {
        UsuarioDTO objDTO = new UsuarioDTO();
        objDTO.setIdUsuario(entity.getIdUsuario());
        objDTO.setNombreUsuario(entity.getNombreUsuario());
        objDTO.setCorreo(entity.getCorreo());
        objDTO.setImagenUrl(entity.getImagenUrl());
        objDTO.setIdRol(entity.getRol().getIdRol());
        objDTO.setNombreRol(entity.getRol().getNombreRol());
        objDTO.setIdDepartamento(entity.getDepartamento().getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getDepartamento().getNombreDepartamento());
        objDTO.setEstado(entity.getEstado());
        return objDTO;
    }
}