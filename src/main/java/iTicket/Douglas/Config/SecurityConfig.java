package iTicket.Douglas.Config;

import iTicket.Douglas.Security.JwtCookieAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize en los Controllers
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter;

    @Autowired
    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) ->
                                escribirError(response, 401, "No hay sesion activa o el token es invalido"))
                        .accessDeniedHandler((request, response, ex) ->
                                escribirError(response, 403, "No tienes permisos para realizar esta accion"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/recuperar-contrasena", "/api/auth/restablecer-contrasena").permitAll()

                        // --- Administracion pura: solo Administrador ---
                        .requestMatchers("/api/roles/**").hasRole("Administrador")
                        .requestMatchers("/api/areas/**").hasRole("Administrador")
                        // Proyectos: el tecnico solo consulta (GET); crear/editar/eliminar es del Administrador
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/**", "/api/fases/**", "/api/detalleFase/**")
                        .hasAnyRole("Administrador", "Tecnico")
                        .requestMatchers("/api/proyectos/**", "/api/fases/**", "/api/detalleFase/**")
                        .hasRole("Administrador")

                        // --- Departamentos: leer (GET) lo necesita cualquiera al crear un ticket ---
                        .requestMatchers(HttpMethod.GET, "/api/departamentos/**").hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/departamentos/**").hasRole("Administrador")

                        // --- Catalogos de inventario: leer abierto, escribir Administrador+Tecnico ---
                        .requestMatchers(HttpMethod.GET, "/api/tipoubicacion/**", "/api/ubicaciones/**",
                                "/api/categorias/**", "/api/marcas/**", "/api/modelos/**", "/api/articulos/**")
                        .hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/tipoubicacion/**", "/api/ubicaciones/**",
                                "/api/categorias/**", "/api/marcas/**", "/api/modelos/**", "/api/articulos/**")
                        .hasAnyRole("Administrador", "Tecnico")

                        // --- Usuarios: autoservicio (propia clave/imagen, ver tecnico o un usuario puntual) ---
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/tecnicos", "/api/usuarios/*")
                        .hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers(HttpMethod.PATCH, "/api/usuarios/*/clave", "/api/usuarios/*/imagen")
                        .hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/usuarios/**").hasRole("Administrador")

                        // --- Estadisticas: puro dashboard, sin ruta de autoservicio ---
                        .requestMatchers("/api/estadisticas/**").hasAnyRole("Administrador", "Tecnico")

                        // --- Operacion diaria: los 3 roles (los sub-endpoints de panel se cierran con @PreAuthorize) ---
                        .requestMatchers("/api/tickets/**").hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/evaluaciones/**").hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/bitacoras/**").hasAnyRole("Administrador", "Tecnico", "Usuario")
                        .requestMatchers("/api/comentarios/**", "/api/multimediaComentarios/**",
                                "/api/evidencias/**", "/api/detalleG/**", "/api/detalleta/**",
                                "/api/detalleTS/**", "/api/notificaciones/**", "/api/chatbot/**")
                        .hasAnyRole("Administrador", "Tecnico", "Usuario")

                        // Cualquier otra ruta (incluye /api/auth/me y /api/auth/logout): con sesion basta
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Mismo formato ApiResponse<T> del proyecto ({success, message, data}), armado a mano
    // para no depender del paquete de Jackson de cada version de Spring Boot.
    private void escribirError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + mensaje + "\",\"data\":null}");
    }
}