package iTicket.Douglas.Exception;

import iTicket.Douglas.util.ErrorCode;
import lombok.Getter;

@Getter
public class RecursoNoEncontradoException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public RecursoNoEncontradoException(ErrorCode errorCode, String mensaje) {
        super(mensaje);
        this.errorCode = errorCode;
    }

    // Constructor original
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
        this.errorCode = null;
    }
}
