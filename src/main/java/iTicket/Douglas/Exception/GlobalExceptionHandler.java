package iTicket.Douglas.Exception;

import iTicket.Douglas.Response.ErrorResponseDTO;
import iTicket.Douglas.util.ErrorCode;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;



import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("Recurso no encontrado: " + e.getMessage());
        String errorCodeStr = e.getErrorCode() != null ? e.getErrorCode().name() : ErrorCode.WGLB404.name();
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorCodeStr, e.getMessage());
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarDuplicado(RecursoDuplicadoException e) {
        log.warn("Recurso duplicado: " + e.getMessage());
        String errorCodeStr = e.getErrorCode() != null ? e.getErrorCode().name() : ErrorCode.WGLB409.name();
        return buildErrorResponse(HttpStatus.CONFLICT, errorCodeStr, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException e) {
        String mensajes = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> "[" + fe.getField() + "] " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validación fallida: " + mensajes);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.WGLB001.name(), "Datos inválidos: " + mensajes);
    }

<<<<<<< HEAD
=======
    // Errores de la paginación, para que no devuelvan un 500 y devuelva un 400
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> manejarValidacionDeParametros(ConstraintViolationException e) {
        String mensajes = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        log.warn("Parámetros inválidos: " + mensajes);
        ApiResponse<Object> respuesta = new ApiResponse<>(false, mensajes, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // Red de seguridad: restricciones de la base de datos que no se validaron a mano antes
    // (llaves foráneas inexistentes, UNIQUE, NOT NULL, CHECK), decodificando el código Oracle real
>>>>>>> origin/master
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> manejarIntegridad(DataIntegrityViolationException e) {
        log.error("Conflicto de integridad de datos: ", e);
        String mensaje = "El dato enviado no existe o hay un conflicto con la información ya guardada.";

        String causa = obtenerCadenaDeCausas(e).toUpperCase();
        if (causa.contains("ORA-02291")) {
            mensaje = "No existe uno de los registros seleccionados. Actualiza la página y vuelve a elegirlo.";
        } else if (causa.contains("ORA-00001")) {
            mensaje = mensajeDuplicadoPorRestriccion(causa);
        } else if (causa.contains("ORA-02290")) {
            mensaje = "Uno de los datos no tiene un valor permitido. Revisa la información seleccionada.";
        } else if (causa.contains("ORA-01400")) {
            mensaje = "Falta completar un campo obligatorio.";
        } else if (causa.contains("ORA-02292")) {
            mensaje = "No se puede eliminar porque este registro está siendo utilizado en otra parte del sistema.";
        }

        return buildErrorResponse(HttpStatus.CONFLICT, ErrorCode.WGLB002.name(), mensaje);
    }

    private String mensajeDuplicadoPorRestriccion(String causa) {
        if (causa.contains("U_NOMBRE_ROL")) return "Ese rol ya está registrado.";
        if (causa.contains("U_NOMBRE_AREA")) return "Esa área ya está registrada.";
        if (causa.contains("U_NOMBRE_CATEGORIA")) return "Esa categoría ya está registrada.";
        if (causa.contains("U_NOMBRE_TIPO_UBICACION")) return "Ese tipo de ubicación ya está registrado.";
        if (causa.contains("U_NOMBRE_MARCA")) return "Esa marca ya está registrada.";
        if (causa.contains("U_DEPARTAMENTO_AREA")) return "Ese departamento ya está registrado en el área seleccionada.";
        if (causa.contains("U_CORREO")) return "Ese correo ya está registrado.";
        if (causa.contains("U_NOMBRE_UBICACION")) return "Esa ubicación ya está registrada.";
        if (causa.contains("U_CODIGO_TICKETS")) return "Ese código de ticket ya está registrado.";
        if (causa.contains("U_CODIGO")) return "Ese código de artículo ya está registrado.";
        if (causa.contains("U_ID_TICKET")) return "Ese ticket ya tiene la información asociada registrada.";
        return "No se pudo guardar porque uno de los datos ya está registrado.";
    }

    private String obtenerCadenaDeCausas(Throwable error) {
        StringBuilder mensajes = new StringBuilder();
        Throwable actual = error;
        while (actual != null) {
            if (actual.getMessage() != null) mensajes.append(' ').append(actual.getMessage());
            actual = actual.getCause();
        }
        return mensajes.toString();
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarOperacionInvalida(OperacionInvalidaException e) {
        log.warn("Operación inválida: " + e.getMessage());
        String errorCodeStr = e.getErrorCode() != null ? e.getErrorCode().name() : "WGLB001";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorCodeStr, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRutaInexistente(NoResourceFoundException e) {
        log.warn("Ruta inexistente: " + e.getResourcePath());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.WGLB404.name(), "La ruta solicitada no existe");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarGeneral(Exception e) {
        log.error("Error inesperado: ", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.WGLB500.name(), "Ocurrió un error inesperado. Contacte al administrador");
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String errorCode, String message) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status.value(), errorCode, message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> manejarAccesoDenegado(AccessDeniedException e) {
        log.warn("Acceso denegado: " + e.getMessage());
        ApiResponse<Object> respuesta = new ApiResponse<>(false, "No tienes permisos para realizar esta accion", null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
    }
}
