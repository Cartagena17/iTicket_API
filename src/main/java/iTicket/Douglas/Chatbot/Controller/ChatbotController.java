package iTicket.Douglas.Chatbot.Controller;

import iTicket.Douglas.Chatbot.DTO.ChatConversationDetailDTO;
import iTicket.Douglas.Chatbot.DTO.ChatConversationSummaryDTO;
import iTicket.Douglas.Chatbot.DTO.ChatbotRequest;
import iTicket.Douglas.Chatbot.DTO.ChatbotResponse;
import iTicket.Douglas.Chatbot.Service.ChatConversationService;
import iTicket.Douglas.Chatbot.Service.ChatbotService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
@CrossOrigin
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatConversationService conversationService;

    @GetMapping(value = "/conversaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ChatConversationSummaryDTO>>> listarConversaciones(
            @RequestParam Long idUsuario
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Conversaciones obtenidas.",
                conversationService.listar(idUsuario)
        ));
    }

    @GetMapping(
            value = "/conversaciones/{idConversacion}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ChatConversationDetailDTO>> obtenerConversacion(
            @PathVariable Long idConversacion,
            @RequestParam Long idUsuario
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Conversación obtenida.",
                conversationService.obtener(idUsuario, idConversacion)
        ));
    }

    @DeleteMapping(
            value = "/conversaciones/{idConversacion}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> eliminarConversacion(
            @PathVariable Long idConversacion,
            @RequestParam Long idUsuario
    ) {
        conversationService.eliminar(idUsuario, idConversacion);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Conversación eliminada.",
                null
        ));
    }

    @PostMapping(
            value = "/mensaje",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ChatbotResponse>> responder(
            @Valid @RequestBody ChatbotRequest request) {

        ChatbotResponse respuesta = chatbotService.responder(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Respuesta generada.",
                        respuesta
                )
        );
    } // Se ocupa siempre la misma ApiResponse que ya hay en la api

    // Si hay algun problema que viene de IllegalStateException envia una respuesta para no enviar el error asi completo
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> manejarFalloDelAsistente(
            IllegalStateException exception) {

        log.warn(
                "Fallo al consultar el chatbot: {}",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ApiResponse<Void>(
                                false,
                                "No fue posible obtener una respuesta del asistente. "
                                        + "Inténtalo más tarde.",
                                null
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> manejarSolicitudInvalida(
            IllegalArgumentException exception
    ) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                false,
                exception.getMessage(),
                null
        ));
    }
}
