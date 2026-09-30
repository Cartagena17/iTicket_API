package iTicket.Douglas.Utils;

import iTicket.Douglas.Exception.OperacionInvalidaException;
import org.springframework.data.domain.Sort;

import java.util.Set;
/**
 * Aqui estara el sort, es como tal una peticion HTTP, en donde como dice el nombre de la clase
 * se ordena dependiendo de lo que el cliente quiere en este caso solo se podra dependiendo de la fecha de creación
 * */

public final class Ordenamiento {

    // Con esto no se pueden hacer objetos de tipo Ordenamiento
    private Ordenamiento() {}

    // Aqui se construye el objeto que Spring necesita para ordenar la consulta del usuario (la consulta es por ejemplo fechaDeCreación, ascendente)
    public static Sort construir(String sort, Set<String> permitidos, Sort porDefecto) {
        // Si el usuario realmente no mando nada en el sort o en la accion, se devuelve el orden por defecto
        if (sort == null || sort.isBlank()) return porDefecto;

        // Aqui se divide la petición en dos partes, primero el campo y luego el orden si es ascendente o descendiente
        String[] partes = sort.split(",");
        String campo = partes[0].trim();

        // El campo enviado es el que debe estar dentro de los permitidos
        if (!permitidos.contains(campo)) {
            throw new OperacionInvalidaException(
                    "No se puede ordenar por '" + campo + "'. Campos permitidos: " + String.join(", ", permitidos));
        }

        // Este solo determina como tal el orden final
        boolean ascendente = partes.length > 1 && partes[1].trim().equalsIgnoreCase("asc");
        return ascendente ? Sort.by(campo).ascending() : Sort.by(campo).descending();
    }

}
