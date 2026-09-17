package iTicket.Douglas.Exception;

import iTicket.Douglas.Response.ApiResponse;
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

        ApiResponse<Object> respuesta = new ApiResponse<>(false, mensaje, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
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
    public ResponseEntity<ApiResponse<Object>> manejarOperacionInvalida(OperacionInvalidaException e) {
        log.warn("Operación inválida: " + e.getMessage());
        ApiResponse<Object> respuesta = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // Una URL que no existe es un 404, no un fallo del servidor: sin esto caía en el catch general
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> manejarRutaInexistente(NoResourceFoundException e) {
        log.warn("Ruta inexistente: " + e.getResourcePath());
        ApiResponse<Object> respuesta = new ApiResponse<>(false, "La ruta solicitada no existe", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> manejarGeneral(Exception e) {
        log.error("Error inesperado: ", e);
        ApiResponse<Object> respuesta = new ApiResponse<>(false, "Ocurrió un error inesperado. Contacte al administrador", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
