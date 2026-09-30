package iTicket.Douglas.Exception;

import iTicket.Douglas.util.ErrorCode;
import lombok.Getter;

@Getter
public class RecursoDuplicadoException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public RecursoDuplicadoException(ErrorCode errorCode, String mensaje) {
        super(mensaje);
        this.errorCode = errorCode;
    }

    // Constructor original
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
        this.errorCode = null;
    }
}
