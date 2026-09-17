package iTicket.Douglas.Fases.DTO;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FaseDTO {

    private Long idFase;

    @NotBlank @Size (max = 100, message = "El nombre de la fase no puede exceder los 100 carácteres.")
    private String nombreFase;

    @NotBlank @Size (max = 300, message = "La descripción de la fase no puede exceder los 300 carácteres.")
    private String faseDescripcion;

    @NotNull @FutureOrPresent (message = "La fecha de inicio estimada no puede ser pasada.")
    private LocalDate fechaInicioEstimada;

    // Las fechas reales describen lo que ya ocurrió, por eso pueden ser pasadas.
    private LocalDate fechaInicioReal;

    @NotNull
    @Future (message = "La fecha final estimada debe ser futura.")
    private LocalDate fechaFinalEstimada;

    private LocalDate fechaFinalReal;

    @NotBlank @Size (max = 100, message = "El nombre del proveedor no puede exceder los 100 carácteres.")
    private String nombreProveedor;

    @NotNull (message = "Debe existir un presupuesto estimado.")
    @PositiveOrZero (message = "El presupuesto estimado no puede tener valores negativos.")
    private Double presupuestoEstimado;

    @PositiveOrZero (message = "El gasto total no puede ser negativo")
    private Double gastoTotal;

    @NotNull
    private Boolean finalizado;

    @NotBlank @Size (max = 20, message = "El departamento encargado no puede exceder los 20 carácteres.")
    private String departamentoEncargado;

    @NotNull @Positive (message = "El id del proyecto debe ser positivo.")
    private Long proyecto;
}
