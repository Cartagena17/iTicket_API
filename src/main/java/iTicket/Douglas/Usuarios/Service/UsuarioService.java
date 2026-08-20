package iTicket.Douglas.Usuarios.Service;

import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final RolRepository rolRepo;
    private final DepartamentoRepository departamentoRepo;
    private final PasswordUtil passwordUtil;

    public UsuarioDTO nuevoUsuario(@Valid UsuarioDTO dto) {
        try {
            Optional<RolEntity> rolOpcional = rolRepo.findById(dto.getIdRol());
            if (rolOpcional.isEmpty()) {
                log.warn("El rol con id " + dto.getIdRol() + " no existe");
                return null;
            }

            Optional<DepartamentoEntity> depaOpcional = departamentoRepo.findById(dto.getIdDepartamento());
            if (depaOpcional.isEmpty()) {
                log.warn("El departamento con id " + dto.getIdDepartamento() + " no existe");
                return null;
            }


            UsuarioEntity entity = convertirAEntity(dto, rolOpcional.get(), depaOpcional.get());
            UsuarioEntity entitySave = repo.save(entity);
            log.info("Nuevo usuario registrado: " + entitySave.getIdUsuario());
            return convertirADTO(entitySave);
        }catch (Exception e) {
            log.error("Error al ingresar la información del usuario: " + e.getMessage());
            return null;
        }
    }

    public List<UsuarioDTO> obtenerTodo() {
        List<UsuarioEntity> lista = repo.findAll();
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Optional<UsuarioEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    public UsuarioDTO actualizarData(Long id,@Valid UsuarioUpdateDTO dto) {
        try {
            Optional<UsuarioEntity> registroExistente = repo.findById(id);
            if (registroExistente.isEmpty()) {
                return null;
            }
            UsuarioEntity entidad = registroExistente.get();

            Optional<RolEntity> rolOpcional = rolRepo.findById(dto.getIdRol());
            if (rolOpcional.isEmpty()) {
                log.warn("El rol con id " + dto.getIdRol() + " no existe");
                return null;
            }

            Optional<DepartamentoEntity> depaOpcional = departamentoRepo.findById(dto.getIdDepartamento());
            if (depaOpcional.isEmpty()) {
                log.warn("El departamento con id " + dto.getIdDepartamento() + " no existe");
                return null;
            }

            entidad.setNombreUsuario(dto.getNombreUsuario());
            entidad.setCorreo(dto.getCorreo());
            entidad.setImagenUrl(dto.getImagenUrl());
            entidad.setRol(rolOpcional.get());
            entidad.setDepartamento(depaOpcional.get());

            if (dto.getClave() != null && !dto.getClave().isBlank()) {
                entidad.setClave(passwordUtil.encriptar(dto.getClave()));
            }

            UsuarioEntity datosGuardados = repo.save(entidad);
            log.info("Usuario con id " + id + " actualizado");
            return convertirADTO(datosGuardados);
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la info " + e.getMessage());
            return null;
        }
    }

    public UsuarioDTO actualizarParcial (Long id, UsuarioPatchDTO dto) {
        try {
            Optional<UsuarioEntity> registroExistente = repo.findById(id);
            if (registroExistente != null){
                return null;
            }
            UsuarioEntity entidad = registroExistente.get();

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
                Optional<RolEntity> rolOpcional = rolRepo.findById(dto.getIdRol());
                if (rolOpcional.isEmpty()) {
                    log.warn("El rol con id " + dto.getIdRol() + " no existe");
                    return null;
                }
                entidad.setRol(rolOpcional.get());
            }
            if (dto.getIdDepartamento() != null) {
                Optional<DepartamentoEntity> depaOpcional = departamentoRepo.findById(dto.getIdDepartamento());
                if (depaOpcional.isEmpty()) {
                    log.warn("El departamento con id " + dto.getIdDepartamento() + " no existe");
                    return null;
                }
                entidad.setDepartamento(depaOpcional.get());
            }

            UsuarioEntity datosGuardados = repo.save(entidad);
            log.info("Usuario con id " + id + " actualizado parcialmente");
            return convertirADTO(datosGuardados);
        } catch (Exception e) {
            log.error("Ocurrió un error al procesar la info: " + e.getMessage());
            return null;
        }
        }

    public boolean eliminarUsuario(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }


    private UsuarioEntity convertirAEntity(UsuarioDTO dto, RolEntity rol, DepartamentoEntity departamento) {
        UsuarioEntity objEntity = new UsuarioEntity();
        objEntity.setNombreUsuario(dto.getNombreUsuario());
        objEntity.setCorreo(dto.getCorreo());
        objEntity.setClave(passwordUtil.encriptar(dto.getClave())); // ← se encripta aquí, nunca antes
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

    public List<UsuarioDTO> obtenerTecnicosPorDepartamento(Long idDepartamento) {
        Optional<DepartamentoEntity> departamento = departamentoRepo.findById(idDepartamento);
        if (departamento.isEmpty()){
            log.warn("No existe ningún departamento con id: " + idDepartamento);
            throw new RuntimeException("No existe ningún departamento con id: " + idDepartamento);
        }

        List<UsuarioEntity> lista = repo.findByRol_NombreRolInAndEstadoAndDepartamento_NombreDepartamentoIgnoreCase(List.of("Tecnico", "Administrador"), true, departamento.get().getNombreDepartamento());
        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
