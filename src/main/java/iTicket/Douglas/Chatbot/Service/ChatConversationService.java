package iTicket.Douglas.Chatbot.Service;

import iTicket.Douglas.Chatbot.DTO.ChatConversationDetailDTO;
import iTicket.Douglas.Chatbot.DTO.ChatConversationSummaryDTO;
import iTicket.Douglas.Chatbot.DTO.ChatMessageDTO;
import iTicket.Douglas.Chatbot.Entity.ChatConversationEntity;
import iTicket.Douglas.Chatbot.Entity.ChatMessageEntity;
import iTicket.Douglas.Chatbot.Repository.ChatConversationRepository;
import iTicket.Douglas.Chatbot.Repository.ChatMessageRepository;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatConversationService {

    private static final int MAXIMO_CONVERSACIONES = 5; // Maximo de conversaciones que se pueden ver en el historial
    private static final int MAXIMO_MENSAJES_DE_MEMORIA = 12; // Solo se pueden enviar 12 mensajes al chatbot (para no gastar tanto token)
    private static final int LONGITUD_TITULO = 55; // El largo del titulo

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UsuarioRepository usuarioRepository;

    // Se listan las conversaciones dependiendo del usuario
    @Transactional(readOnly = true)
    public List<ChatConversationSummaryDTO> listar(Long idUsuario) {
        validarUsuario(idUsuario);

        return conversationRepository
                .findByUsuario_IdUsuarioOrderByFechaActualizacionDesc(idUsuario)
                .stream()
                .limit(MAXIMO_CONVERSACIONES)
                .map(this::convertirResumen)
                .toList();
    }

    // Se obtienen los mensajes de las conversaciones del usuario
    @Transactional(readOnly = true)
    public ChatConversationDetailDTO obtener(Long idUsuario, Long idConversacion) {
        ChatConversationEntity conversacion = buscarPropia(idUsuario, idConversacion);
        List<ChatMessageDTO> mensajes = messageRepository
                .findByConversacion_IdConversacionOrderByFechaHoraAscIdMensajeAsc(idConversacion)
                .stream()
                .map(this::convertirMensaje)
                .toList();

        return new ChatConversationDetailDTO(
                conversacion.getIdConversacion(),
                conversacion.getTitulo(),
                conversacion.getFechaActualizacion(),
                mensajes
        );
    }

    // Se guardan/obtienen las conversaciones
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> obtenerMemoria(Long idUsuario, Long idConversacion) {
        if (idConversacion == null) {
            return List.of();
        }

        buscarPropia(idUsuario, idConversacion);
        List<ChatMessageEntity> mensajes = messageRepository
                .findByConversacion_IdConversacionOrderByFechaHoraAscIdMensajeAsc(idConversacion);

        // Se ve cuantos mensajes van para que se sepa por asi decirlo el límite
        int desde = Math.max(0, mensajes.size() - MAXIMO_MENSAJES_DE_MEMORIA);
        return mensajes.subList(desde, mensajes.size())
                .stream()
                .map(this::convertirMensaje)
                .toList();
    }

    // Se van guardando en la bd tanto las preguntas del usuario como la respuesta del chat en los mensajes que estan en la conversación
    @Transactional
    public ChatConversationSummaryDTO guardarIntercambio(
            Long idUsuario,
            Long idConversacion,
            String pregunta,
            String respuesta
    ) {
        ChatConversationEntity conversacion = idConversacion == null
                ? crear(idUsuario, pregunta)
                : buscarPropia(idUsuario, idConversacion);

        LocalDateTime ahora = LocalDateTime.now();
        guardarMensaje(conversacion, "user", pregunta, ahora);
        guardarMensaje(conversacion, "assistant", respuesta, ahora.plusNanos(1));

        conversacion.setFechaActualizacion(ahora);
        conversationRepository.save(conversacion);

        return convertirResumen(conversacion);
    }

    // Eliminar una conversación
    @Transactional
    public void eliminar(Long idUsuario, Long idConversacion) {
        conversationRepository.delete(buscarPropia(idUsuario, idConversacion));
    }

    // Al presionar el botón de + en conversaciones se crea una nueva, aunque no significa que ya se creo, se crea si se envia un mensaje
    private ChatConversationEntity crear(Long idUsuario, String primerMensaje) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario indicado."
                ));

        // Se verifica que mientras no hayan mas de 5 conversaciones no se borre nada
        while (conversationRepository.countByUsuario_IdUsuario(idUsuario)
                >= MAXIMO_CONVERSACIONES) {
            ChatConversationEntity masAntigua = conversationRepository
                    .findFirstByUsuario_IdUsuarioOrderByFechaCreacionAsc(idUsuario)
                    .orElseThrow();
            conversationRepository.delete(masAntigua);
            conversationRepository.flush();
        }

        LocalDateTime ahora = LocalDateTime.now();
        ChatConversationEntity conversacion = new ChatConversationEntity();
        conversacion.setUsuario(usuario);
        conversacion.setTitulo(crearTitulo(primerMensaje));
        conversacion.setFechaCreacion(ahora);
        conversacion.setFechaActualizacion(ahora);
        return conversationRepository.save(conversacion);
    }

    // Se guardan los mensajes en la bd al enviarse y al mismo tiempo esta conectado con la conversación
    private void guardarMensaje(
            ChatConversationEntity conversacion,
            String rol,
            String contenido,
            LocalDateTime fecha
    ) {
        ChatMessageEntity mensaje = new ChatMessageEntity();
        mensaje.setConversacion(conversacion);
        mensaje.setRol(rol);
        mensaje.setContenido(contenido.trim());
        mensaje.setFechaHora(fecha);
        messageRepository.save(mensaje);
    }

    // Para poder "buscar/presionar" una conversacion y que se carguen todos los mensajes
    private ChatConversationEntity buscarPropia(Long idUsuario, Long idConversacion) {
        validarUsuario(idUsuario);
        if (idConversacion == null || idConversacion <= 0) {
            throw new IllegalArgumentException("La conversación indicada no es válida.");
        }

        return conversationRepository
                .findByIdConversacionAndUsuario_IdUsuario(idConversacion, idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró esa conversación entre tus chats."
                ));
    }

    // Se valida el id del usuario, para saber su rol
    private void validarUsuario(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("No se pudo identificar al usuario.");
        }
    }

    // El titulo se crea con el primer mensaje
    private String crearTitulo(String mensaje) {
        String titulo = mensaje == null
                ? "Nueva conversación"
                : mensaje.trim().replaceAll("\\s+", " ");

        if (titulo.isBlank()) {
            return "Nueva conversación";
        }

        return titulo.length() <= LONGITUD_TITULO
                ? titulo
                : titulo.substring(0, LONGITUD_TITULO - 1).trim() + "…";
    }

    // Se obtiene el contexto para poder mostrar la conversación (el titulo)
    private ChatConversationSummaryDTO convertirResumen(ChatConversationEntity entidad) {
        return new ChatConversationSummaryDTO(
                entidad.getIdConversacion(),
                entidad.getTitulo(),
                entidad.getFechaActualizacion()
        );
    }

    // Se convierten los mensajes para poder leerlos
    private ChatMessageDTO convertirMensaje(ChatMessageEntity entidad) {
        return new ChatMessageDTO(
                entidad.getIdMensaje(),
                entidad.getRol(),
                entidad.getContenido(),
                entidad.getFechaHora()
        );
    }
}
