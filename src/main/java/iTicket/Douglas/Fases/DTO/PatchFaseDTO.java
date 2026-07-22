package iTicket.Douglas.Fases.DTO;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchFaseDTO {

    @PositiveOrZero (message = "El gasto total no puede ser negativo.")
    private Double gastoTotal;

    @FutureOrPresent (message = "La fecha debe ser futura")
    private LocalDate fechaInicioEstimada;

    @Future (message = "La fecha debe ser futura")
    private LocalDate fechaFinalEstimada;
}
