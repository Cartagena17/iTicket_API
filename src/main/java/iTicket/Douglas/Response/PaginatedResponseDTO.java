package iTicket.Douglas.Response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Wrapper genérico para respuestas paginadas.
 * Encapsula el contenido de una página junto con sus metadatos de paginación.
 *
 * @param <T> Tipo del elemento contenido en la página.
 */
@Getter @Setter
public class PaginatedResponseDTO<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public PaginatedResponseDTO(List<T> content, int currentPage, int totalPages, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
