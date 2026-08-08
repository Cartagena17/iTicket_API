package iTicket.Douglas.Tickets.Service;

import iTicket.Douglas.Articulos.Entity.ArticuloEntity;
import iTicket.Douglas.Articulos.Repository.ArticuloRepository;
import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Service.BitacoraService;
import iTicket.Douglas.Departamentos.Entity.DepartamentoEntity;
import iTicket.Douglas.Departamentos.Repository.DepartamentoRepository;
import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Entity.DetalleGEntity;
import iTicket.Douglas.DetalleGeneral.Repository.DetalleGRepository;
import iTicket.Douglas.DetalleGeneral.Service.DetalleGService;
import iTicket.Douglas.DetalleTA.DTO.DetalleTADTO;
import iTicket.Douglas.DetalleTA.Entity.DetalleTAEntity;
import iTicket.Douglas.DetalleTA.Repository.DetalleTARepository;
import iTicket.Douglas.DetalleTA.Service.DetalleTAService;
import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Entity.DetalleTSEntity;
import iTicket.Douglas.DetalleTS.Repository.DetalleTSRepository;
import iTicket.Douglas.DetalleTS.Service.DetalleTSService;
import iTicket.Douglas.Evidencias.Entity.EvidenciaEntity;
import iTicket.Douglas.Tickets.DTO.*;
import iTicket.Douglas.Tickets.Entity.TicketEntity;
import iTicket.Douglas.Tickets.Repository.TicketRepository;
import iTicket.Douglas.Tickets.Specification.TicketSpecifications;
import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import iTicket.Douglas.Usuarios.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository repo;
    private final DepartamentoRepository departamentosRepo;
    private final UsuarioRepository usuariosRepo;
    private final DetalleGService detalleGService;
    private final DetalleGRepository detalleGRepo;
    private final DetalleTAService detalleTAService;
    private final DetalleTARepository detalleTARepo;
    private final DetalleTSService detalleTSService;
    private final DetalleTSRepository detalleTSRepo;
    private final BitacoraService bitacoraService;
    private final ArticuloRepository articuloRepo;

    @Transactional
    public TicketDTO nuevoTicket(@Valid TicketDTO dto){
        try {
            dto.setCodigo(generarCodigo());
            dto.setEstado("Nuevo");
            TicketEntity entity = convertirAEntity(dto);
            TicketEntity entitySave = repo.save(entity);

            crearDetalleSegunTipo(entitySave, dto);
            registrarBitacora(entitySave, entitySave.getCreador().getIdUsuario());
            return convertirADTOCompleto(entitySave);
        }catch (Exception e){
            log.error("Error al registrar el ticket " + e.getMessage());
            throw new RuntimeException("Error al registrar el ticket");
        }
    }

    @Transactional
    private void crearDetalleSegunTipo(TicketEntity ticket, TicketDTO dto) {
        switch (dto.getTipoTicket()) {
            case "General" -> {
                if (dto.getDescripcionUbicacion() == null || dto.getDescripcionUbicacion().isBlank()) {
                    throw new RuntimeException("Debe indicar la ubicación del problema.");
                }
                DetalleGDTO detalleDto = new DetalleGDTO();
                detalleDto.setTicket(ticket.getIdTicket());
                detalleDto.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                detalleGService.nuevoDetalleG(detalleDto);
            }
            case "Articulo" -> {
                if (dto.getCodigosArticulos() == null || dto.getCodigosArticulos().isEmpty()) {
                    throw new RuntimeException("Debe agregar al menos un código de equipo/mobiliario.");
                }

                List<ArticuloEntity> articulos = new ArrayList<>();
                for (String codigo : dto.getCodigosArticulos()) {
                    ArticuloEntity articulo = articuloRepo.findByCodigoArticulo(codigo).orElseThrow(() -> new RuntimeException("No existe ningún artículo con código: " + codigo));
                    articulos.add(articulo);
                }

                // Validar que todos los artículos compartan la misma ubicación física
                long ubicacionesDistintas = articulos.stream().map(a -> a.getUbicacion().getId()).distinct().count();

                if (ubicacionesDistintas > 1) {
                    throw new RuntimeException("Los artículos seleccionados deben estar en la misma ubicación. " + "No puedes reportar en un solo ticket equipos de distintos lugares.");
                }

                for (ArticuloEntity articulo : articulos) {
                    DetalleTADTO detalleDto = new DetalleTADTO();
                    detalleDto.setIdTicket(ticket.getIdTicket());
                    detalleDto.setIdArticulo(articulo.getIdArticulo());
                    detalleTAService.nuevoDetalle(detalleDto);
                }
            }
            case "Software" -> {
                if (dto.getDetallesSoftware() == null || dto.getDetallesSoftware().isEmpty()) {
                    throw new RuntimeException("Debe agregar al menos un software a instalar.");
                }
                for (DetalleTSDTO sw : dto.getDetallesSoftware()) {
                    sw.setTicket(ticket.getIdTicket());
                    detalleTSService.nuevoDetalleTS(sw);
                }
            }
            default -> throw new RuntimeException("Tipo de ticket no reconocido: " + dto.getTipoTicket());
        }
    }

    @Transactional
    private void registrarBitacora(TicketEntity ticket, Long idUsuarioAccion) {
        BitacoraDTO bitacoraDto = new BitacoraDTO();
        bitacoraDto.setIdTicket(ticket.getIdTicket());
        bitacoraDto.setCodigoTicket(ticket.getCodigo());
        bitacoraDto.setAsuntoTicket(ticket.getAsunto());
        bitacoraDto.setUsuario(idUsuarioAccion);
        bitacoraDto.setNuevoEstado(ticket.getEstado());
        bitacoraService.nuevaBitacora(bitacoraDto);
    }

    public String generarCodigo() {
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Long siguienteValor = repo.obtenerSiguienteCodigo();
        //"%05d" establece que el número de dígitos mínimo es de 5, si el número es menor, lo rellena con 0
        String correlativo = String.format("%05d", siguienteValor);

        return "#" + fecha + "-" + correlativo;
    }

    public List<TicketDTO> obtenerTodo() {
        List<TicketEntity> data = repo.findAll();
        return data.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TicketDTO buscarTicket(Long id) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        return entidadOpcional.map(this::convertirADTOCompleto).orElse(null);
    }

    @Transactional
    public boolean eliminarData(Long id, Long idUsuarioSolicitante){
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();
        UsuarioEntity solicitante = buscarUsuario(idUsuarioSolicitante);
        String rolSolicitante = solicitante.getRol().getNombreRol();
        boolean esCreador = ticket.getCreador().getIdUsuario().equals(idUsuarioSolicitante);
        boolean esAdmin = "Administrador".equalsIgnoreCase(rolSolicitante);

        if (esAdmin) {
            boolean resuelto = "Resuelto".equals(ticket.getEstado());
            boolean cerrado = "Cerrado".equals(ticket.getEstado());

            if (resuelto || cerrado) {
                throw new RuntimeException("No se puede eliminar un ticket ya resuelto y evaluado.");
            }

        } else if (esCreador) {
            if (!"Nuevo".equals(ticket.getEstado())) {
                throw new RuntimeException("No puedes eliminar un ticket que ya fue asignado.");
            }

        } else {
            throw new RuntimeException("No tienes permisos para eliminar este ticket.");
        }

        repo.delete(ticket);
        return true;
    }

    @Transactional
    public TicketDTO actualizarTicket(Long id, @Valid TicketDTO dto){
        try {
            Optional<TicketEntity> entidadOpcional = repo.findById(id);
            if (entidadOpcional.isPresent()){
                TicketEntity entidad = entidadOpcional.get();

                entidad.setAsunto(dto.getAsunto());
                entidad.setDescripcion(dto.getDescripcion());
                //Busca el objeto Departamento antes de asignarlo
                entidad.setDepartamento(buscarDepartamento(dto.getDepartamento()));
                entidad.setDescripcionFalla(dto.getDescripcionFalla());
                entidad.setDescripcionSolucion(dto.getDescripcionSolucion());
                entidad.setFechaVencimiento(dto.getFechaVencimiento());
                //Busca el objeto Usuario antes de asignarlo
                if (dto.getTecnicoAsignado() != null) {
                    entidad.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
                }
                entidad.setPrioridad(dto.getPrioridad());
                entidad.setTipoTicket(dto.getTipoTicket());
                entidad.setEstado(dto.getEstado());

                TicketEntity datosGuardados = repo.save(entidad);
                return convertirADTOCompleto(datosGuardados);
            }
            return null;
        }catch (Exception e){
            log.error("Oops, ocurrió un error al procesar la información");
            return null;
        }
    }

    public TicketDTO buscarPorCodigo(String codigo) {
        try {
            Optional<TicketEntity> registro = repo.findByCodigo(codigo);
            if (registro.isPresent()){
                return convertirADTO(registro.get());
            }
            log.warn("No existe ningún ticket con código: " + codigo);
            return null;
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return null;
        }
    }

    public List<TicketDTO> buscarPorAsunto(String asunto) {
        try {
            List<TicketEntity> registros = repo.findByAsuntoContainingIgnoreCase(asunto);
            if (!registros.isEmpty()){
                return registros.stream().map(this::convertirADTO).collect(Collectors.toList());
            }
            log.warn("No existe ningún ticket con asunto: " + asunto);
            return Collections.emptyList();
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return Collections.emptyList();
        }
    }

    //Método reutilizable para obtener un objeto Departamento
    private DepartamentoEntity buscarDepartamento(Long id) {
        Optional<DepartamentoEntity> departamentoOp = departamentosRepo.findById(id);
        if (departamentoOp.isPresent()){
            return departamentoOp.get();
        }
        log.warn("No existe ningún departamento con ID: " + id);
        throw new RuntimeException("No existe ningún departamento con ID: " + id);
    }

    //Método reutilizable para obtener un objeto Usuario
    private UsuarioEntity buscarUsuario(Long id) {
        Optional<UsuarioEntity> usuarioOp = usuariosRepo.findById(id);
        if (usuarioOp.isPresent()){
            return usuarioOp.get();
        }
        log.warn("No existe ningún usuario con ID: " + id);
        throw new RuntimeException("No existe ningún usuario con ID: " + id);
    }

    //Método para convertir DTO a Entity
    private TicketEntity convertirAEntity(@Valid TicketDTO dto) {
        TicketEntity objEntity = new TicketEntity();
        objEntity.setCodigo(dto.getCodigo());
        objEntity.setAsunto(dto.getAsunto());
        objEntity.setDescripcion(dto.getDescripcion());
        objEntity.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        objEntity.setDescripcionFalla(dto.getDescripcionFalla());
        objEntity.setDescripcionSolucion(dto.getDescripcionSolucion());
        objEntity.setFechaVencimiento(dto.getFechaVencimiento());
        objEntity.setPrioridad(dto.getPrioridad());
        objEntity.setTipoTicket(dto.getTipoTicket());
        objEntity.setCreador(buscarUsuario(dto.getCreador()));
        objEntity.setEstado(dto.getEstado());
        //Verificar si el id del tecnico viene en el dto(según la lógica, no debería venir al crearse un ticket)
        if (dto.getTecnicoAsignado() != null) {
            objEntity.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        }
        return objEntity;
    }

    //Método para convertir Entity a DTO
    private TicketDTO convertirADTO(@Valid TicketEntity entity) {
        TicketDTO objDTO = new TicketDTO();

        objDTO.setIdTicket(entity.getIdTicket());
        objDTO.setCodigo(entity.getCodigo());
        objDTO.setAsunto(entity.getAsunto());
        objDTO.setDescripcion(entity.getDescripcion());
        objDTO.setDepartamento(entity.getDepartamento().getIdDepartamento());
        objDTO.setNombreDepartamento(entity.getDepartamento().getNombreDepartamento());
        objDTO.setDescripcionFalla(entity.getDescripcionFalla());
        objDTO.setDescripcionSolucion(entity.getDescripcionSolucion());
        objDTO.setFechaVencimiento(entity.getFechaVencimiento());
        objDTO.setPrioridad(entity.getPrioridad());
        objDTO.setTipoTicket(entity.getTipoTicket());
        objDTO.setEstado(entity.getEstado());
        objDTO.setCreador(entity.getCreador().getIdUsuario());
        objDTO.setNombreCreador(entity.getCreador().getNombreUsuario());
        objDTO.setCorreoCreador(entity.getCreador().getCorreo());
        objDTO.setFechaCreacion(entity.getFechaCreacion());
        if (entity.getTecnicoAsignado() != null) {
            objDTO.setTecnicoAsignado(entity.getTecnicoAsignado().getIdUsuario());
            objDTO.setNombreTecnico(entity.getTecnicoAsignado().getNombreUsuario());
            objDTO.setCorreoTecnico(entity.getTecnicoAsignado().getCorreo());
        }

        return objDTO;
    }

    //Versión completa, solo para cuando se necesita el detalle según el tipo y evidencias
    private TicketDTO convertirADTOCompleto(TicketEntity entity) {
        TicketDTO objDTO = convertirADTO(entity);
        obtenerDetallePorTipo(entity, objDTO);
        return objDTO;
    }

    @Transactional
    public boolean asignarTicket(Long id, @Valid TicketAsignacionDTO dto, Long idUsuarioAdmin) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);

        if (entidadOpcional.isPresent()){
            TicketEntity ticket = entidadOpcional.get();
            ticket.setFechaVencimiento(dto.getFechaVencimiento());
            ticket.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
            ticket.setPrioridad(dto.getPrioridad());
            ticket.setEstado("Asignado");

            repo.save(ticket);
            registrarBitacora(ticket, idUsuarioAdmin);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean reporteTicket(Long id, @Valid TicketResolucionDTO dto, Long idUsuarioTecnico) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);

        if (entidadOpcional.isPresent()){
            TicketEntity ticket = entidadOpcional.get();

            if (ticket.getTecnicoAsignado() == null || !ticket.getTecnicoAsignado().getIdUsuario().equals(idUsuarioTecnico)) {
                throw new RuntimeException("Solo el usuario asignado puede hacer un reporte de este ticket");
            }
            if (!List.of("En proceso", "En espera", "Vencido").contains(ticket.getEstado())) {
                throw new RuntimeException("El ticket no se encuentra en un estado que permita generar el reporte");
            }
            ticket.setDescripcionFalla(dto.getDescripcionFalla());
            ticket.setDescripcionSolucion(dto.getDescripcionSolucion());
            ticket.setEstado("Resuelto");
            repo.save(ticket);
            registrarBitacora(ticket, idUsuarioTecnico);
            return true;
        }
        return false;
    }

    //Metodo para cargar los indicadores de numero de tickets según el estado
    public TickteIndicadoresEstadoDTO obtenerIndicadoresDepartamento(Long idUsuario) {
        try{
            Optional<UsuarioEntity> entidadOpcional = usuariosRepo.findById(idUsuario);
            if (entidadOpcional.isEmpty()){
                log.warn("No existe ningún usuario con ID: " + idUsuario);
                return null;
            }
            UsuarioEntity usuario = entidadOpcional.get();
            String departamentoAdmin = usuario.getDepartamento().getNombreDepartamento();

            List<Object[]> filas = repo.contarTicketsPorEstadoYDepartamento(departamentoAdmin);

            Map<String, Long> conteos = new HashMap<>();
            for (Object[] fila : filas) {
                conteos.put((String) fila[0], (Long) fila[1]);
            }

            long resueltos = conteos.getOrDefault("Resuelto", 0L);
            long asignados = conteos.getOrDefault("Asignado", 0L);
            long enProgreso = conteos.getOrDefault("En proceso", 0L);
            long enEspera = conteos.getOrDefault("En espera", 0L);
            long cerrados = conteos.getOrDefault("Cerrado", 0L);
            long nuevos = conteos.getOrDefault("Nuevo", 0L);
            long vencidos = conteos.getOrDefault("Vencido", 0L);

            return new TickteIndicadoresEstadoDTO(resueltos, asignados, enProgreso, enEspera, nuevos, cerrados, vencidos);
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return new TickteIndicadoresEstadoDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    //Metodo para cargar los indicadores de numero de tickets según el estado por idUsuario
    public TickteIndicadoresEstadoDTO obtenerIndicadoresPropios(Long idUsuario) {
        try{
            List<Object[]> filas = repo.contarTicketsPorEstadoUsuario(idUsuario);

            Map<String, Long> conteos = new HashMap<>();
            for (Object[] fila : filas) {
                conteos.put((String) fila[0], (Long) fila[1]);
            }

            long resueltos = conteos.getOrDefault("Resuelto", 0L);
            long asignados = conteos.getOrDefault("Asignado", 0L);
            long enProgreso = conteos.getOrDefault("En proceso", 0L);
            long enEspera = conteos.getOrDefault("En espera", 0L);
            long cerrados = conteos.getOrDefault("Cerrado", 0L);
            long nuevos = conteos.getOrDefault("Nuevo", 0L);
            long vencidos = conteos.getOrDefault("Vencido", 0L);

            return new TickteIndicadoresEstadoDTO(resueltos, asignados, enProgreso, enEspera, nuevos, cerrados, vencidos);
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return new TickteIndicadoresEstadoDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    public List<TicketDTO> obtenerAprobacionesPendientes(int limite, Long idUsuarioAdmin) {
        try{
            UsuarioEntity admin = buscarUsuario(idUsuarioAdmin);
            String departamentoAdmin = admin.getDepartamento().getNombreDepartamento();

            Pageable pageable = PageRequest.of(0, limite, Sort.by("idTicket").ascending());
            Page<TicketEntity> pagina = repo.findByEstadoAndDepartamento_NombreDepartamentoIgnoreCase("Nuevo", departamentoAdmin, pageable);
            return pagina.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
        }catch (Exception e){
            log.error("Ocurrió un error durante el proceso");
            return null;
        }

    }

    //Metodo para mostrar el detalle segun el tipo de ticket
    private void obtenerDetallePorTipo(TicketEntity entity, TicketDTO objDTO){
        objDTO.setEvidencias(entity.getEvidencias().stream().map(EvidenciaEntity::getEvidenciaUrl).collect(Collectors.toList()));
        switch (entity.getTipoTicket()) {
            case "General":
                DetalleGEntity detalle = detalleGRepo.findByTicket_IdTicket(entity.getIdTicket())
                        .orElseThrow(() -> new RuntimeException("Advertencia: el ticket " + entity.getCodigo() + " es de tipo General pero no tiene detalle registrado."));
                objDTO.setUbicacion(detalle.getDescripcionUbicacion());
                break;
            case "Articulo":
                List<DetalleTAEntity> detalles = detalleTARepo.findByTicket_IdTicket(entity.getIdTicket());

                if(detalles.isEmpty()){
                    throw new RuntimeException("Advertencia: el ticket " + entity.getCodigo() + " es de tipo Articulo pero no tiene ningún detalle registrado.");
                }

                objDTO.setCodigosArticulos(detalles.stream().map(d -> d.getArticulo().getCodigoArticulo()).collect(Collectors.toList()));

                detalles.stream().findFirst().ifPresent(d -> objDTO.setUbicacion(d.getArticulo().getUbicacion().getNombreUbicacion()));
                break;
            case "Software":
                List<DetalleTSEntity> detallesS = detalleTSRepo.findByTicket_IdTicket(entity.getIdTicket());

                if (detallesS.isEmpty()) {
                    throw new RuntimeException("Advertencia: el ticket " + entity.getCodigo() + " es de tipo Software pero no tiene ningún detalle registrado.");
                }

                objDTO.setDetallesSoftware(detallesS.stream().map(d -> {
                    DetalleTSDTO detallesSoftware = new DetalleTSDTO();
                    detallesSoftware.setNombreSoftware(d.getNombreSoftware());
                    detallesSoftware.setVersion(d.getVersion());
                    return detallesSoftware;}).collect(Collectors.toList())
                );

                detallesS.stream().findFirst().ifPresent(d -> objDTO.setUbicacion(d.getUbicacion().getNombreUbicacion()));
                break;
        }
    }

    @Transactional
    public boolean editarComoCreador(Long id, @Valid TicketEdicionCreadorDTO dto, Long idUsuario) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        //El creador solo puede editar el ticket mientras el estado sea "Nuevo"
        if (!ticket.getCreador().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El usuario no tiene permiso para editar este ticket");
        }
        if (!"Nuevo".equals(ticket.getEstado())) {
            throw new RuntimeException("Solo se puede editar el ticket mientras está en estado 'Nuevo'");
        }

        ticket.setAsunto(dto.getAsunto());
        ticket.setDescripcion(dto.getDescripcion());
        ticket.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        repo.save(ticket);

        actualizarDetalleSegunTipo(ticket, dto);
        return true;
    }

    @Transactional
    private void actualizarDetalleSegunTipo(TicketEntity ticket, TicketEdicionCreadorDTO dto) {
        switch (ticket.getTipoTicket()) {
            case "General" -> {
                if (dto.getDescripcionUbicacion() == null || dto.getDescripcionUbicacion().isBlank()) {
                    throw new RuntimeException("Debe indicar la ubicación del problema.");
                }
                DetalleGDTO detalleDto = new DetalleGDTO();
                detalleDto.setTicket(ticket.getIdTicket());
                detalleDto.setDescripcionUbicacion(dto.getDescripcionUbicacion());
                detalleGService.actualizarDetalle(detalleDto);
            }
            case "Articulo" -> {
                if (dto.getCodigosArticulos() == null || dto.getCodigosArticulos().isEmpty()) {
                    throw new RuntimeException("Debe agregar al menos un código de equipo/mobiliario.");
                }
                detalleTAService.reemplazarDetalles(ticket, dto.getCodigosArticulos());
            }
            case "Software" -> {
                if (dto.getDetallesSoftware() == null || dto.getDetallesSoftware().isEmpty()) {
                    throw new RuntimeException("Debe agregar al menos un software a instalar.");
                }
                detalleTSService.reemplazarDetalles(ticket, dto.getDetallesSoftware());
            }
            default -> throw new RuntimeException("Tipo de ticket no reconocido: " + ticket.getTipoTicket());
        }
    }

    @Transactional
    public boolean editarComoAdmin(Long id, @Valid TicketAsignacionDTO dto, Long idUsuarioAdmin) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        List<String> estadosReasignables = List.of("Asignado", "En proceso", "En espera", "Vencido");
        if (!estadosReasignables.contains(ticket.getEstado())) {
            throw new RuntimeException("El ticket ya no puede reasignarse en su estado actual: " + ticket.getEstado());
        }

        ticket.setFechaVencimiento(dto.getFechaVencimiento());
        ticket.setPrioridad(dto.getPrioridad());
        ticket.setTecnicoAsignado(buscarUsuario(dto.getTecnicoAsignado()));
        ticket.setEstado("Asignado"); //El ticket vuelve a ser Asignado

        repo.save(ticket);
        registrarBitacora(ticket, idUsuarioAdmin);
        return true;
    }

    //El estado solo puede ser seleccionado entre "En proceso" y "En espera" por el tecnico
    @Transactional
    public boolean editarEstado(Long id, @Valid TicketEstadoDTO dto, Long idUsuario) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();

        if (ticket.getTecnicoAsignado() == null || !ticket.getTecnicoAsignado().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El usuario no tiene permiso para cambiar el estado de este ticket");
        }

        List<String> estadosPermitidos = List.of("En proceso", "En espera");
        if (!estadosPermitidos.contains(dto.getEstado())) {
            throw new RuntimeException("Solo puedes mover el ticket entre 'En proceso' y 'En espera'. Para marcarlo como resuelto, crea el reporte técnico.");
        }
        if (!List.of("Asignado", "En proceso", "En espera").contains(ticket.getEstado())) {
            throw new RuntimeException("El ticket no se encuentra en un estado que permita este cambio");
        }

        ticket.setEstado(dto.getEstado());
        repo.save(ticket);
        registrarBitacora(ticket, idUsuario);
        return true;
    }

    @Transactional
    public boolean reasignarDepartamento(Long id, @Valid TicketReasignarDepDTO dto) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            return false;
        }

        TicketEntity ticket = entidadOpcional.get();
        ticket.setDepartamento(buscarDepartamento(dto.getDepartamento()));
        repo.save(ticket);
        return true;
    }

    public TicketPaginaDTO obtenerTicketsPorDepartamento(Long idUsuarioAdmin, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha){
         UsuarioEntity admin = buscarUsuario(idUsuarioAdmin);
         String departamentoAdmin = admin.getDepartamento().getNombreDepartamento();

         Specification<TicketEntity> spec = TicketSpecifications.conDepartamento(departamentoAdmin);

         if (busqueda != null && !busqueda.isBlank()){
             spec = spec.and(TicketSpecifications.conBusqueda(busqueda));
         }
         if (prioridad != null && !prioridad.isBlank()){
             spec = spec.and(TicketSpecifications.conPrioridad(prioridad));
         }
         if (estado != null && !estado.isBlank()){
             spec = spec.and(TicketSpecifications.conEstado(estado));
         }
        if (fecha != null) {
            spec = spec.and(TicketSpecifications.conFechaCreacion(fecha));
        }

        //pagina-1, Spring Data las cuenta desde 0, pero en la interfaz la primera es la 1
        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idTicket").descending());
        Page<TicketEntity> resultado = repo.findAll(spec, pageable);

        List<TicketDTO> tickets = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());

        return new TicketPaginaDTO(tickets, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    public TicketPaginaDTO obtenerTicketsAsignados(Long idUsuario, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha) {
        Specification<TicketEntity> spec = TicketSpecifications.conTecnico(idUsuario);

        if (busqueda != null && !busqueda.isBlank()){
            spec = spec.and(TicketSpecifications.conBusqueda(busqueda));
        }
        if (prioridad != null && !prioridad.isBlank()){
            spec = spec.and(TicketSpecifications.conPrioridad(prioridad));
        }
        if (estado != null && !estado.isBlank()){
            spec = spec.and(TicketSpecifications.conEstado(estado));
        }
        if (fecha != null) {
            spec = spec.and(TicketSpecifications.conFechaCreacion(fecha));
        }

        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idTicket").descending());
        Page<TicketEntity> resultado = repo.findAll(spec, pageable);

        List<TicketDTO> tickets = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
        return new TicketPaginaDTO(tickets, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    public TicketPaginaDTO obtenerTicketsPorUsuario(Long idUsuario, int pagina, int tamano, String busqueda, String prioridad, String estado, LocalDate fecha) {
        Specification<TicketEntity> spec = TicketSpecifications.conUsuario(idUsuario);

        if (busqueda != null && !busqueda.isBlank()){
            spec = spec.and(TicketSpecifications.conBusqueda(busqueda));
        }
        if (prioridad != null && !prioridad.isBlank()){
            spec = spec.and(TicketSpecifications.conPrioridad(prioridad));
        }
        if (estado != null && !estado.isBlank()){
            spec = spec.and(TicketSpecifications.conEstado(estado));
        }
        if (fecha != null) {
            spec = spec.and(TicketSpecifications.conFechaCreacion(fecha));
        }

        Pageable pageable = PageRequest.of(pagina - 1, tamano, Sort.by("idTicket").descending());
        Page<TicketEntity> resultado = repo.findAll(spec, pageable);

        List<TicketDTO> tickets = resultado.getContent().stream().map(this::convertirADTO).collect(Collectors.toList());
        return new TicketPaginaDTO(tickets, resultado.getTotalElements(), resultado.getTotalPages(), pagina);
    }

    @Transactional
    public boolean actualizarEstado(Long id, @Valid TicketEstadoDTO dto) {
        Optional<TicketEntity> entidadOpcional = repo.findById(id);
        if (entidadOpcional.isEmpty()) {
            throw new RuntimeException("El ticket con ID: " + id + " no existe");
        }

        TicketEntity ticket = entidadOpcional.get();
        ticket.setEstado(dto.getEstado());
        repo.save(ticket);
        return true;
    }
}
