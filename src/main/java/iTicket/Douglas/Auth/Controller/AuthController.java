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

    /**
     * No consulta la base de datos: todo sale del token que ya validó el
     * JwtCookieAuthFilter (Modulo 2/4). Si no hay sesion valida, la peticion
     * ni siquiera llega aqui — la corta el authenticationEntryPoint del
     * SecurityConfig (Modulo 3) con el 401.
     */
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
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "El correo es requerido"));
        }
        
        authService.solicitarRecuperacion(correo);
        return ResponseEntity.ok(new ApiResponse<>(true, "Correo enviado exitosamente"));
    }

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<ApiResponse<Void>> restablecerContrasena(@RequestBody RecuperacionRequestDTO dto) {
        if (dto.getToken() == null || dto.getNuevaContrasena() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Token y nueva contraseña son requeridos"));
        }
        
        authService.restablecerContrasena(dto.getToken(), dto.getNuevaContrasena());
        return ResponseEntity.ok(new ApiResponse<>(true, "Contraseña actualizada exitosamente"));
    }
}