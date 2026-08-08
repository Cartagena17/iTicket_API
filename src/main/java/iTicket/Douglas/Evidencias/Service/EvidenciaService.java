package iTicket.Douglas.Evidencias.Service;

import iTicket.Douglas.Evidencias.DTO.EvidenciaDTO;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Evidencias.Repository.EvidenciaRepository;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EvidenciaService {

    //Inyectar dependencias
    private final EvidenciaRepository repo;
    private final TicketRepository ticketsRepo; //Para buscar si el ticket existe

    @Value("${file.upload-dir}")
    private String uploadDir;

    public EvidenciaService(EvidenciaRepository repo, TicketRepository ticketsRepo) {
        this.repo = repo;
        this.ticketsRepo = ticketsRepo;
    }

    //Método para guardar el archivo físico en disco y registrar la evidencia en BD
    @Transactional
    public EvidenciaDTO subirEvidencia(MultipartFile archivo, Long idTicket) {
        try {
            //Verifica que el ticket exista antes de procesar el archivo
            if (!ticketsRepo.existsById(idTicket)) {
                throw new RuntimeException("El ticket con ID: " + idTicket + " no existe");
            }

            //Genera un nombre único para evitar que dos archivos con el mismo nombre se sobrescriban
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreArchivo = UUID.randomUUID() + extension;

            //Crea la carpeta de destino si aún no existe
            Path carpetaDestino = Paths.get(uploadDir);
            if (!Files.exists(carpetaDestino)) {
                Files.createDirectories(carpetaDestino);
            }

            //Copia el archivo del request hacia la carpeta física
            Path rutaDestino = carpetaDestino.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            //Arma la URL pública
            String urlPublica = "http://localhost:8080/uploads/" + nombreArchivo;

            //Guarda el registro en la tabla Evidencias
            EvidenciaDTO dto = new EvidenciaDTO();
            dto.setEvidenciaUrl(urlPublica);
            dto.setTicket(idTicket);

            return nuevaEvidencia(dto);
        } catch (IOException e) {
            log.error("Error al guardar el archivo físico: " + e.getMessage());
            throw new RuntimeException("Error al guardar el archivo físico", e);
        }
    }

    @Transactional
    private String obtenerExtension(@Nullable String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return "";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
    }


    //Método para crear evidencias
    @Transactional
    public EvidenciaDTO nuevaEvidencia(@Valid EvidenciaDTO dto){
        try {
            EvidenciaEntity entity = convertirAEntity(dto);
            //Guardar en la base
            EvidenciaEntity entitySave = repo.save(entity);
            //Devolver e de entitySave como DTO
            return convertirADTO(entitySave);
        }catch (Exception e){
            log.error("Error al registrar la evidencia " + e.getMessage());
            return null;
        }
    }

    //Método para obtener evidencias por Ticket
    public List<EvidenciaDTO> obtenerEvidenciasPorTicket(Long idTicket) {
        //Buscar el ticket relacionado en la base
        Optional<TicketEntity> entidadOpcional = ticketsRepo.findById(idTicket);

        if (entidadOpcional.isEmpty()) {
            throw new RuntimeException("El ticket con ID: " + idTicket + " no existe");
        }
        TicketEntity ticket = entidadOpcional.get();
        //Buscar evidencias asociadas al objeto ticket
        List<EvidenciaEntity> data = repo.findByTicket(ticket);

        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
        //.map le da los atributos que vienen del entity a los atributos del DTO
    }

    //Método para eliminar
    @Transactional
    public boolean eliminarData(Long id) {
        if (repo.existsById(id)){ //Validar si existe
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    //Método para obtener todas las evidencias
    public List<EvidenciaDTO> obtenerTodo() {
        List<EvidenciaEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    //Método para obtener evidencias por su id
    public EvidenciaDTO buscarEvidencia(Long id) {
        Optional<EvidenciaEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTO).orElse(null);
    }

    //Método para convertir los DTO a Entity
    private EvidenciaEntity convertirAEntity(@Valid EvidenciaDTO dto) {
        EvidenciaEntity objEntity = new EvidenciaEntity();

        objEntity.setEvidenciaUrl(dto.getEvidenciaUrl());

        //getReferenceById sirve para evitar hacer un SELECT completo y retornar un proxy solo con el id
        TicketEntity ticket = ticketsRepo.getReferenceById(dto.getTicket());

        objEntity.setTicket(ticket);
        return objEntity;
    }

    //Método para convertir Entity a DTO
    private EvidenciaDTO convertirADTO(@Valid EvidenciaEntity entity) {
        EvidenciaDTO objDTO = new EvidenciaDTO();
        objDTO.setIdEvidencia(entity.getIdEvidencia());
        objDTO.setEvidenciaUrl(entity.getEvidenciaUrl());

        //Para traer el objeto ticket guardado en el entity y solo tomar el id del ticket
        if (entity.getTicket() != null) {
            objDTO.setTicket(entity.getTicket().getIdTicket());
        }
        return objDTO;
    }


}
