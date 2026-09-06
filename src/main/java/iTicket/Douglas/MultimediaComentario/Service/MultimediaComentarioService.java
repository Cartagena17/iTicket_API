package iTicket.Douglas.MultimediaComentario.Service;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
import iTicket.Douglas.Exception.RecursoNoEncontradoException;
import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.MultimediaComentario.Repository.MultimediaComentarioRepository;
import iTicket.Douglas.Utils.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MultimediaComentarioService {

    private final MultimediaComentarioRepository repo;
    private final ComentarioRepository comentarioRepo;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public MultimediaComentarioDTO subirMultimedia(MultipartFile archivo, Long idComentario) {
        if (!comentarioRepo.existsById(idComentario)) {
            throw new RecursoNoEncontradoException("El comentario con ID: " + idComentario + " no existe");
        }
        MultimediaComentarioDTO dto = new MultimediaComentarioDTO();

        CloudinaryService.ResultadoSubida subida = cloudinaryService.subirImagen(archivo, "iticket/comentarios");
        dto.setMultimediaUrl(subida.url());
        dto.setCloudinaryId(subida.publicId());
        dto.setIdComentario(idComentario);

        return nuevaMultimedia(dto);
    }

    @Transactional
    public MultimediaComentarioDTO nuevaMultimedia(@Valid MultimediaComentarioDTO dto) {
        MultimediaComentarioEntity entity = convertirAEntity(dto);
        MultimediaComentarioEntity entitySave = repo.save(entity);
        log.info("Nueva multimedia registrada: " + entitySave.getId());
        return convertirADTO(entitySave);
    }

    private MultimediaComentarioEntity convertirAEntity(@Valid MultimediaComentarioDTO dto) {
        if (dto.getMultimediaUrl() == null || dto.getMultimediaUrl().isBlank()) {
            throw new IllegalStateException("La URL de la evidencia no puede estar vacía al guardar");
        }
        if (dto.getCloudinaryId() == null || dto.getCloudinaryId().isBlank()) {
            throw new IllegalStateException("El identificador de Cloudinary no puede estar vacío al guardar");
        }
        MultimediaComentarioEntity objEntity = new MultimediaComentarioEntity();
        objEntity.setMultimediaUrl(dto.getMultimediaUrl());
        objEntity.setCloudinaryId(dto.getCloudinaryId());
        ComentarioEntity comentario = comentarioRepo.getReferenceById(dto.getIdComentario());
        objEntity.setComentario(comentario);
        return objEntity;
    }

    private MultimediaComentarioDTO convertirADTO(@Valid MultimediaComentarioEntity entity) {
        MultimediaComentarioDTO objDTO = new MultimediaComentarioDTO();
        objDTO.setId(entity.getId());
        objDTO.setMultimediaUrl(entity.getMultimediaUrl());
        objDTO.setCloudinaryId(entity.getCloudinaryId());
        objDTO.setIdComentario(entity.getComentario().getId());
        return objDTO;
    }

    public List<MultimediaComentarioDTO> obtenerTodo() {
        List<MultimediaComentarioEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public MultimediaComentarioDTO buscarPorId(Long id) {
        MultimediaComentarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una multimedia con id " + id));
        return convertirADTO(entidad);
    }

    @Transactional
    public boolean eliminarData(Long id) {
        var entidad = repo.findById(id);
        if (entidad.isPresent()) {
            cloudinaryService.eliminarImagen(entidad.get().getCloudinaryId());
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public MultimediaComentarioDTO actualizar(Long id, @Valid MultimediaComentarioDTO dto) {
        MultimediaComentarioEntity entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una multimedia con id " + id));

        entidad.setMultimediaUrl(dto.getMultimediaUrl());
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setId(dto.getIdComentario());
        entidad.setComentario(comentario);

        MultimediaComentarioEntity datosGuardados = repo.save(entidad);
        log.info("Multimedia con id " + id + " actualizada");
        return convertirADTO(datosGuardados);
    }
}