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

import java.util.Random;

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
                .orElseThrow(() -> new OperacionInvalidaException("Correo o contrasena incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new OperacionInvalidaException("El usuario se encuentra inactivo");
        }

        if (!passwordUtil.coincidence(dto.getClave(), usuario.getClave())) {
            throw new OperacionInvalidaException("Correo o contrasena incorrectos");
        }

        log.info("Login exitoso: " + dto.getCorreo());
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol()
        );
    }

    public String solicitarRecuperacion(String correo) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El correo proporcionado no esta registrado"));

        String codigo = String.format("%06d", new Random().nextInt(999999));
        
        emailService.enviarCorreoRecuperacion(correo, codigo, usuario.getNombreUsuario());

        return jwtUtils.createCodeVerificationToken(correo, codigo);
    }
    public String reenviarCodigo(String token) {
        try {
            Claims claims = jwtUtils.parseTokenAndClaims(token);
            if (!"CODE_VERIFICATION".equals(claims.get("purpose", String.class))) {
                throw new OperacionInvalidaException("Token invalido");
            }
            String correo = claims.getSubject();
            
            UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
                    
            String nuevoCodigo = String.format("%06d", new Random().nextInt(999999));
            emailService.enviarCorreoRecuperacion(correo, nuevoCodigo, usuario.getNombreUsuario());
            
            return jwtUtils.createCodeVerificationToken(correo, nuevoCodigo);
        } catch (JwtException | IllegalArgumentException e) {
            throw new OperacionInvalidaException("Tu sesion de recuperacion ha expirado.");
        }
    }

    public String validarCodigo(String token, String codigoIngresado) {
        try {
            Claims claims = jwtUtils.parseTokenAndClaims(token);
            
            if (!"CODE_VERIFICATION".equals(claims.get("purpose", String.class))) {
                throw new OperacionInvalidaException("Token invalido");
            }
            
            String codigoCorrecto = claims.get("codigo", String.class);
            String correo = claims.getSubject();

            if (!codigoCorrecto.equals(codigoIngresado)) {
                throw new OperacionInvalidaException("El codigo es incorrecto.");
            }

            // Return the final recovery token
            return jwtUtils.createRecoveryToken(correo);

        } catch (JwtException | IllegalArgumentException e) {
            throw new OperacionInvalidaException("El codigo ha expirado o es inválido.");
        }
    }

    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        try {
            Claims claims = jwtUtils.parseTokenAndClaims(token);
            
            if (!"PASSWORD_RECOVERY".equals(claims.get("purpose", String.class))) {
                throw new OperacionInvalidaException("Token invalido para esta operacion");
            }
            
            String correo = claims.getSubject();

            UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

            usuario.setClave(passwordUtil.encriptar(nuevaContrasena));
            usuarioRepository.save(usuario);
            
            log.info("contrasena restablecida exitosamente para: " + correo);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token invalido o expirado en recuperacion: " + e.getMessage());
            throw new OperacionInvalidaException("Tu sesion para restablecer ha expirado");
        }
    }
}

