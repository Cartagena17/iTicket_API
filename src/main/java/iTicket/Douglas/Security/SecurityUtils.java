package iTicket.Douglas.Security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 *el usuario autenticado sale del token que ya validó el JwtCookieAuthFilter
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser usuarioActual() {
        return (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public static Long idUsuarioActual() {
        return usuarioActual().idUsuario();
    }
}
