package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ErrorResponseDTO {
    private int status;
    private String errorCode;
    private String message;

    public ErrorResponseDTO(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
