package iTicket.Douglas.util;

import lombok.Getter;

/**
 * Catálogo Definitivo de Códigos de Error Internos
 * Formato: W + [PREFIJO_MODULO_3_LETRAS] + [CORRELATIVO_3_DIGITOS]
 */
@Getter
public enum ErrorCode {
    // Autenticación (WAUTxxx)
    WAUT001("Credenciales inválidas"),
    
    // Tickets (WTKxxx)
    WTK001("Falta la ubicación del problema en el ticket General."),
    WTK002("Operación de ticket inválida para el estado actual."),
    WTK003("Permisos insuficientes sobre el ticket."),
    WTK004("Error de validación en artículos o software del ticket."),
    WTK005("Eliminación no permitida (Ticket asignado o resuelto)."),
    WTK006("Tipo de ticket no reconocido por el sistema."),
    WTK007("Transición de estado no permitida manualmente."),
    WTK008("Ticket o Detalle asociado no encontrado."),

    // Proyectos (WPRYxxx)
    WPRY001("Proyecto no encontrado."),
    WPRY002("Fase o Detalle de Fase no encontrado."),

    // Usuarios y Roles (WUSRxxx)
    WUSR001("Rol de sistema incompatible con el departamento asignado."),
    WUSR002("Usuario o Rol no encontrado en los registros."),

    // Departamentos (WDEPxxx)
    WDEP001("Departamento o Área no encontrado."),

    // Evaluaciones (WEVAxxx)
    WEVA001("La evaluación no es válida porque el ticket ya fue evaluado."),
    WEVA002("Solo los tickets en estado 'Resuelto' pueden ser evaluados."),
    WEVA003("Evaluación no encontrada."),

    // Evidencias / Multimedia (WEVIxxx)
    WEVI001("Error de conexión con Cloudinary al subir imagen."),
    WEVI002("Evidencia o Comentario no encontrado."),

    // Marcas y Catálogos (WMARxxx)
    WMAR001("Ya existe una marca registrada con ese nombre."),
    WMAR002("Artículo, Marca, Modelo, Categoría o Ubicación no encontrada."),

    // Bitácoras (WBITxxx)
    WBIT001("Registro de Bitácora no encontrado."),

    // Globales y Base de Datos (WGLBxxx)
    WGLB001("Error de validación en los datos del formulario."),
    WGLB002("Error de restricción en base de datos (Conflicto de integridad)."),
    WGLB404("El recurso solicitado no existe."),
    WGLB409("El recurso ya existe o está duplicado."),
    WGLB500("Error interno inesperado del servidor.");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }
}
