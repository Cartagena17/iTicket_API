package iTicket.Douglas.Fases.DTO;

// Grupo de validación de Bean Validation usado únicamente al crear una fase nueva (ver FaseDTO
// y FaseController.nuevaFase). Las fechas estimadas solo tienen sentido como "hoy o futuras" en
// el momento de crear la fase; al editar una fase ya existente (PUT /api/fases/{id}) esas fechas
// pueden legítimamente haber quedado en el pasado (la fase ya inició, o su fin estimado ya llegó),
// así que actualizarFase valida solo el grupo Default (los @NotBlank, @Size, @NotNull, etc. siguen
// aplicando siempre) y omite estas dos restricciones de fecha.
public interface ValidacionCrear {
}
