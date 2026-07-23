package iTicket.Douglas.Areas.Service;

    import iTicket.Douglas.Areas.DTO.AreaDTO;
    import iTicket.Douglas.Areas.Entity.AreaEntity;
    import iTicket.Douglas.Areas.Repository.AreaRepository;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class AreaService {

        private final AreaRepository repo;

        public AreaDTO nuevaArea(@Valid AreaDTO dto) {

            try {
                //Convertir a Entity
                AreaEntity entity = convertirAEntity(dto);
                //Guardar en la base de datos
                AreaEntity entitySave = repo.save(entity);
                //Devuelvo una respuesta
                return convertirADTO(entitySave);
            } catch (Exception e) {
                log.error("Error al guardar el área"+e.getMessage());
                throw new RuntimeException("No se pudo guardar el área");
            }

        }

        public List<AreaDTO> obtenerTodo() {
            try {
                List<AreaEntity> data = repo.findAll();
                return data.stream().map(this::convertirADTO).collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error al obtener las áreas: " + e.getMessage());
                throw new RuntimeException("No se pudieron cargar las áreas");
            }
        }

        public AreaDTO obtenerPorId(Long id) {
            Optional<AreaEntity> entidadOpcional = repo.findById(id);
            // Si existe, lo convertimos a DTO y lo retornamos; si no, retornamos null
            if (entidadOpcional.isPresent()) {
                return convertirADTO(entidadOpcional.get());
            }
            return null;
        }

        public AreaDTO editarArea(Long id,@Valid AreaDTO dto) {
            try {
                // Buscar si el área existe
                Optional<AreaEntity> registroExistente = repo.findById(id);
                if (registroExistente.isPresent()) {
                    AreaEntity entidad = registroExistente.get();
                    entidad.setNombreArea(dto.getNombreArea());
                    AreaEntity datosGuardados = repo.save(entidad);
                    return convertirADTO(datosGuardados);
                }
                return null;
            } catch (Exception e) {
                log.error("Error al editar el área: " + e.getMessage());
                return null;
            }
        }

        public boolean eliminarArea(Long id) {
            // Verificamos que el registro exista antes de eliminar
            if (repo.existsById(id)) {
                repo.deleteById(id);
                return true; // Se eliminó correctamente
            }
            return false; // No se encontró el registro
        }

        private AreaEntity convertirAEntity(@Valid AreaDTO dto) {
            AreaEntity objEntity = new AreaEntity();
            objEntity.setNombreArea(dto.getNombreArea());
            return objEntity;
        }

        private AreaDTO convertirADTO(@Valid AreaEntity entity) {
            AreaDTO objDTO = new AreaDTO();
            objDTO.setIdArea(entity.getIdArea());
            objDTO.setNombreArea(entity.getNombreArea());
            return objDTO;
        }
    }
