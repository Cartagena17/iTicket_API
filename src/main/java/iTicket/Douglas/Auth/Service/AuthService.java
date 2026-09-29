package iTicket.Douglas.Auth.Service;

import iTicket.Douglas.Auth.DTO.LoginRequestDTO;
import iTicket.Douglas.Auth.DTO.LoginResponseDTO;
import iTicket.Douglas.Exception.OperacionInvalidaException;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import iTicket.Douglas.Utils.PasswordUtil;
import iTicket.Douglas.Security.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(dto.getCorreo())
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

    public void solicitarRecuperacion(String correo) {
        // 1. Buscamos al usuario (A peticion, lanzaremos error si no existe para atraparlo en el Frontend)
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El correo proporcionado no está registrado"));

        // 2. Fabricar el token de 30 minutos
        String token = jwtUtils.createRecoveryToken(correo);

        // 3. Enviar el correo usando la plantilla HTML y pasandole el nombre real del usuario
        emailService.enviarCorreoRecuperacion(correo, token, usuario.getNombreUsuario());
    }

    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        try {
            // 1. Desempaquetar y validar firma del token
            Claims claims = jwtUtils.parseTokenAndClaims(token);
            
            // 2. Verificar escudo de seguridad (proposito)
            if (!"PASSWORD_RECOVERY".equals(claims.get("purpose", String.class))) {
                throw new OperacionInvalidaException("Token invalido para esta operacion");
            }
            
            String correo = claims.getSubject();

            // 3. Buscar al dueño del correo
            UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

            // 4. Encriptar la nueva contrasena y guardarla
            // IMPORTANTE: usamos passwordUtil.encriptar() como se define en tu clase PasswordUtil
            usuario.setClave(passwordUtil.encriptar(nuevaContrasena));
            usuarioRepository.save(usuario);
            
            log.info("Contrasena restablecida exitosamente para: " + correo);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token invalido o expirado en recuperacion: " + e.getMessage());
            throw new OperacionInvalidaException("El enlace de recuperacion es invalido o ha expirado");
        }
    }
}