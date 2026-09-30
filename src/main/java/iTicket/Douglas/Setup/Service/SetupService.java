package iTicket.Douglas.Setup.Service;

import iTicket.Douglas.Areas.DTO.AreaDTO;
import iTicket.Douglas.Areas.Service.AreaService;
import iTicket.Douglas.Departamentos.DTO.DepartamentoDTO;
import iTicket.Douglas.Departamentos.Service.DepartamentoService;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoDuplicadoException;
import iTicket.Douglas.Roles.Entity.RolEntity;
import iTicket.Douglas.Roles.Repository.RolRepository;
import iTicket.Douglas.Setup.DTO.SetupAdministradorDTO;
import iTicket.Douglas.Setup.DTO.SetupEstadoDTO;
import iTicket.Douglas.Usuarios.DTO.UsuarioDTO;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import iTicket.Douglas.Usuarios.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SetupService {

    private static final String ROL_ADMINISTRADOR = "Administrador";

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final AreaService areaService;
    private final DepartamentoService departamentoService;
    private final UsuarioService usuarioService;

    public SetupEstadoDTO obtenerEstado() {
        if (usuarioRepo.count() > 0) {
            return new SetupEstadoDTO(true, List.of());
        }
        return new SetupEstadoDTO(false, departamentoService.obtenerDepartamentosAsignables());
    }

    @Transactional
    public UsuarioDTO crearAdministradorInicial(@Valid SetupAdministradorDTO dto) {
        if (usuarioRepo.count() > 0) {
            throw new RecursoDuplicadoException(
                    "El sistema ya tiene usuarios registrados. Inicia sesion con tu cuenta.");
        }

        RolEntity rolAdministrador = rolRepo.findByNombreRolIgnoreCase(ROL_ADMINISTRADOR)
                .orElseThrow(() -> new OperacionInvalidaException(
                        "No existe el rol Administrador. Revisa que se haya ejecutado el script de la base de datos."));

        Long idDepartamento = dto.getIdDepartamento() != null
                ? dto.getIdDepartamento()
                : crearAreaYDepartamento(dto);

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setClave(dto.getClave());
        usuario.setIdRol(rolAdministrador.getIdRol());
        usuario.setIdDepartamento(idDepartamento);
        usuario.setEstado(true);

        UsuarioDTO creado = usuarioService.nuevoUsuario(usuario);
        log.info("Administrador inicial creado: " + creado.getIdUsuario());
        return creado;
    }

    /* Solo corre cuando la base esta recien creada y no hay ni un departamento.
       Va dentro de la misma transaccion que el usuario: si el usuario falla
       (por ejemplo, correo repetido) el area y el departamento tampoco quedan. */
    private Long crearAreaYDepartamento(SetupAdministradorDTO dto) {
        if (esVacio(dto.getNombreArea()) || esVacio(dto.getNombreDepartamento())) {
            throw new OperacionInvalidaException("Indica el area y el departamento del administrador.");
        }

        AreaDTO area = new AreaDTO();
        area.setNombreArea(dto.getNombreArea().trim());
        Long idArea = areaService.nuevaArea(area).getIdArea();

        DepartamentoDTO departamento = new DepartamentoDTO();
        departamento.setNombreDepartamento(dto.getNombreDepartamento().trim());
        // Un administrador atiende tickets, asi que su departamento no puede ser de tipo 'Otro'
        departamento.setTipoDepartamento(esVacio(dto.getTipoDepartamento()) ? "IT" : dto.getTipoDepartamento().trim());
        departamento.setIdArea(idArea);

        return departamentoService.nuevoDepartamento(departamento).getIdDepartamento();
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
