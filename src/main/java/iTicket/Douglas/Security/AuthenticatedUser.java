package iTicket.Douglas.Security;

/** Lo que queda disponible del usuario autenticado, sin volver a tocar la BD. */
public record AuthenticatedUser(Long idUsuario, String correo, String rol) {

}
