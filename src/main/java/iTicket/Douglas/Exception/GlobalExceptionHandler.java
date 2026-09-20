package iTicket.Douglas.Exception;

import iTicket.Douglas.Response.ErrorResponseDTO;
import iTicket.Douglas.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> manejarIntegridad(DataIntegrityViolationException e) {
        log.error("Conflicto de integridad de datos: ", e);
        String mensaje = "El dato enviado no existe o hay un conflicto con la información ya guardada.";

        if (e.getCause() != null && e.getCause().getCause() != null) {
            String causa = e.getCause().getCause().getMessage();
            if (causa.contains("ORA-02291")) {
                mensaje = "El registro relacionado (llave foránea) no existe. Verifica el id enviado.";
            } else if (causa.contains("ORA-00001")) {
                mensaje = "Ese dato ya existe (restricción única violada).";
            } else if (causa.contains("ORA-02290")) {
                mensaje = "El dato no cumple con el formato requerido (restricción CHECK).";
            } else if (causa.contains("ORA-01400")) {
                mensaje = "Falta un campo obligatorio (no puede quedar vacío).";
            } else if (causa.contains("ORA-02292")) {
                mensaje = "No se puede eliminar: este registro está siendo usado por otro (llave foránea dependiente).";
            }
        }

        return buildErrorResponse(HttpStatus.CONFLICT, ErrorCode.WGLB002.name(), mensaje);
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
}
