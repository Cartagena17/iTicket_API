package iTicket.Douglas.Proyectos.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProyectoDTO {

    private Long idProyecto;

    @NotBlank(message = "El proyecto debe tener un nombre")
    @Size(max = 100, message = "Longitud inválida en el nombre del proyecto [100 caracteres]")
    private String nombreProyecto;

    @NotBlank(message = "El tipo de proyecto es obligatorio")
    @Size(max = 30, message = "Longitud inválida en el tipo de proyecto [30 caracteres]")
    private String tipoProyecto;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 100, message = "Longitud inválida en la ubicación [100 caracteres]")
    private String ubicacion;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "Longitud inválida en la descripción del proyecto [300 caracteres]")
    private String descripcionProyecto;

    @NotNull(message = "El presupuesto estimado es obligatorio")
    @PositiveOrZero(message = "El presupuesto estimado no puede ser un número negativo")
    private BigDecimal presupuestoEstimado;

    @PositiveOrZero(message = "El gasto total no puede ser un número negativo")
    private BigDecimal gastoTotal;

    @NotNull
    @Positive
    private Long coordinador;
    private String nombreCoordinador;

    @NotNull
    @Positive
    private Long supervisor;
    private String nombreSupervisor;

    @NotNull(message = "El estado de finalización del proyecto es obligatorio")
    private Boolean finalizado;
}
