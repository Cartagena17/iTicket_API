package iTicket.Douglas.Exception;

import iTicket.Douglas.Response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("Recurso no encontrado: " + e.getMessage());
        ApiResponse<Object> respuesta = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ApiResponse<Object>> manejarDuplicado(RecursoDuplicadoException e) {
        log.warn("Recurso duplicado: " + e.getMessage());
        ApiResponse<Object> respuesta = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    // Errores de validación de @Valid en los DTO (@NotBlank, @NotNull, @Size, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> manejarValidacion(MethodArgumentNotValidException e) {
        String mensajes = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> "[" + fe.getField() + "] " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validación fallida: " + mensajes);
        ApiResponse<Object> respuesta = new ApiResponse<>(false, "Datos inválidos: " + mensajes, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // Red de seguridad: restricciones de la base de datos que no se validaron a mano antes
    // (llaves foráneas inexistentes, UNIQUE, NOT NULL, CHECK), decodificando el código Oracle real
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> manejarIntegridad(DataIntegrityViolationException e) {
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

        ApiResponse<Object> respuesta = new ApiResponse<>(false, mensaje, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> manejarGeneral(Exception e) {
        log.error("Error inesperado: ", e);
        ApiResponse<Object> respuesta = new ApiResponse<>(false, "Ocurrió un error inesperado. Contacte al administrador", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
