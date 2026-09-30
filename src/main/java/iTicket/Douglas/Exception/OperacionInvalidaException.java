package iTicket.Douglas.Exception;

import iTicket.Douglas.util.ErrorCode;
import lombok.Getter;

@Getter
public class OperacionInvalidaException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public OperacionInvalidaException(ErrorCode errorCode, String mensaje) {
        super(mensaje);
        this.errorCode = errorCode;
    }

    // Constructor original por retrocompatibilidad temporal
    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
        this.errorCode = null;
    }
}
