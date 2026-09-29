package iTicket.Douglas.Auth.Service;

import iTicket.Douglas.Auth.DTO.LoginRequestDTO;
import iTicket.Douglas.Auth .DTO.LoginResponseDTO;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import iTicket.Douglas.Utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordUtil passwordUtil;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                // Mismo mensaje genérico si el correo no existe o la clave no coincide:
                // no revelamos cuál de las dos falló.
                .orElseThrow(() -> new OperacionInvalidaException("Correo o contraseña incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new OperacionInvalidaException("El usuario se encuentra inactivo");
        }

        if (!passwordUtil.coincidence(dto.getClave(), usuario.getClave())) {
            throw new OperacionInvalidaException("Correo o contraseña incorrectos");
        }

        log.info("Login exitoso: " + dto.getCorreo());
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol()
        );
    }
}
