package iTicket.Douglas.Auth.Controller;

import iTicket.Douglas.Auth.DTO.LoginRequestDTO;
import iTicket.Douglas.Auth.DTO.LoginResponseDTO;
import iTicket.Douglas.Auth.Service.AuthService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Security.AuthenticatedUser;
import iTicket.Douglas.Security.CookieFactory;
import iTicket.Douglas.Security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import iTicket.Douglas.Auth.DTO.RecuperacionRequestDTO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final CookieFactory cookieFactory;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto,
                                                               HttpServletResponse response) {
        LoginResponseDTO respuesta = authService.login(dto);

        String token = jwtUtils.create(respuesta.getIdUsuario(), respuesta.getCorreo(), respuesta.getNombreRol());
        ResponseCookie cookie = cookieFactory.build(token, jwtUtils.getExpirationSeconds());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Inicio de sesion exitoso", respuesta));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        AuthenticatedUser usuario = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, Object> datos = Map.of(
                "idUsuario", usuario.idUsuario(),
                "correo", usuario.correo(),
                "rol", usuario.rol()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Sesion activa", datos));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.clear().toString());
        return ResponseEntity.ok(new ApiResponse<>(true, "Sesion cerrada"));
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String correo = body.get("correo");
        if (correo == null || correo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "El correo es requerido"));
        }
        
        String tokenValidacion = authService.solicitarRecuperacion(correo);
        ResponseCookie cookie = cookieFactory.build(tokenValidacion, 30 * 60);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Correo enviado exitosamente"));
    }
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<ApiResponse<Void>> reenviarCodigo(@CookieValue(value = "authToken", required = false) String token, HttpServletResponse response) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token requerido para reenviar"));
        }
        
        String nuevoToken = authService.reenviarCodigo(token);
        ResponseCookie cookie = cookieFactory.build(nuevoToken, 30 * 60);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Nuevo codigo enviado exitosamente"));
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<ApiResponse<Void>> validarCodigo(@RequestBody Map<String, String> body, 
                                                                         @CookieValue(value = "authToken", required = false) String token,
                                                                         HttpServletResponse response) {
        String codigo = body.get("codigo");
        if (token == null || token.trim().isEmpty() || codigo == null || codigo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token y codigo son requeridos"));
        }
        
        String tokenRecuperacion = authService.validarCodigo(token, codigo);
        ResponseCookie cookie = cookieFactory.build(tokenRecuperacion, 30 * 60);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Codigo valido"));
    }

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<ApiResponse<Void>> restablecerContrasena(@RequestBody RecuperacionRequestDTO dto,
                                                                   @CookieValue(value = "authToken", required = false) String token,
                                                                   HttpServletResponse response) {
        if (token == null || dto.getNuevaContrasena() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token y nueva contrasena son requeridos"));
        }
        
        authService.restablecerContrasena(token, dto.getNuevaContrasena());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.clear().toString());
        return ResponseEntity.ok(new ApiResponse<>(true, "Contrasena actualizada exitosamente"));
    }
}
