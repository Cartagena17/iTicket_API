package iTicket.Douglas.Utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public record ResultadoSubida(String url, String publicId) {}

    //Sube la imagen a una carpeta de Cloudinary y devuelve la URL pública(https) y el public_id
    public ResultadoSubida subirImagen(MultipartFile archivo, String carpeta) {
        try{
            Map<?,?> resultado = cloudinary.uploader().upload(archivo.getBytes(),ObjectUtils.asMap(
                    "folder", carpeta,
                    "resource_type", "auto"
            ));
            String url = resultado.get("secure_url").toString();
            String publicId = resultado.get("public_id").toString();
            return new ResultadoSubida(url,publicId);
        }catch (IOException e) {
            log.error("Error al subir la imagen a Cloudinary: " + e.getMessage());
            throw new RuntimeException("No se pudo subir la imagen", e);
        }
    }

    // Miniatura cuadrada para los avatares
    public String urlMiniatura(String publicId) {
        if (publicId == null || publicId.isBlank()) return null;
        return cloudinary.url()
                .secure(true)
                .transformation(new Transformation()
                        // Aqui se declara el tamaño que tendra la miniatura
                        .width(120).height(120).crop("fill").gravity("face")
                        .quality("auto").fetchFormat("auto"))
                .generate(publicId);
    }

    //La misma imagen a tamaño completo, pero con formato y compresión optimizados
    public String urlOptimizada(String publicId) {
        if (publicId == null || publicId.isBlank()) return null;
        return cloudinary.url()
                .secure(true)
                .transformation(new Transformation().quality("auto").fetchFormat("auto"))
                .generate(publicId);
    }

    //Elimina una imagen usando su public_id
    public void eliminarImagen(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Error al eliminar la imagen de Cloudinary: " + e.getMessage());
        }
    }
}
