package iTicket.Douglas.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Punto único para leer el usuario autenticado (id + rol) desde el SecurityContext.
 * Usado por los servicios que necesitan validar permisos "a nivel de proyecto"
 * (p. ej. solo el coordinador puede modificar las fases de SU proyecto), algo que
 * SecurityConfig no puede expresar con simples requestMatchers por ruta/rol.
 */
public final class AutorizacionUtils {

    private AutorizacionUtils() {
    }

    public static AuthenticatedUser usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser usuario)) {
            return null;
        }
        return usuario;
    }

    public static boolean esAdministrador() {
        AuthenticatedUser usuario = usuarioActual();
        return usuario != null && "Administrador".equals(usuario.rol());
    }
}
