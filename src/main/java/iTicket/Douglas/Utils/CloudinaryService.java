package iTicket.Douglas.Utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    //Sube la imagen a una carpeta de Cloudinary y devuelve la URL pública(https)
    public String subirImagen(MultipartFile archivo, String carpeta) {
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.asMap(
                    "folder", carpeta,
                    "public_id", UUID.randomUUID().toString(),
                    "resource_type", "image"
            ));
            return resultado.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Error al subir la imagen a Cloudinary: " + e.getMessage());
            throw new RuntimeException("No se pudo subir la imagen", e);
        }
    }

    //Elimina una imagen usando su public_id
    public void eliminarImagen(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Error al eliminar la imagen de Cloudinary: " + e.getMessage());
        }
    }

    //Extrae el public_id a partir de la URL guardada, para poder borrarla después
    public String extraerPublicId(String urlSegura, String carpeta) {
        String nombreArchivo = urlSegura.substring(urlSegura.lastIndexOf("/") + 1);
        String sinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));
        return carpeta + "/" + sinExtension;
    }
}
