CREATE SEQUENCE Seq_roles
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Roles (
id_rol NUMBER PRIMARY KEY,
nombre_rol VARCHAR2(20) NOT NULL,
CONSTRAINT u_nombre_rol UNIQUE (nombre_rol));
COMMENT ON TABLE Roles IS 'Tabla que almacena los roles de los usuarios dentro del sistema';

CREATE SEQUENCE Seq_areas
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Areas (
id_area NUMBER PRIMARY KEY,
nombre_area VARCHAR2(20) NOT NULL,
CONSTRAINT u_nombre_areal UNIQUE (nombre_area));
COMMENT ON TABLE Areas IS 'Tabla que almacena las áreas de la empresa';


CREATE SEQUENCE Seq_prioridades
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Prioridades (
id_prioridad NUMBER PRIMARY KEY,
nombre_prioridad VARCHAR2(20) NOT NULL,
CONSTRAINT u_nombre_prioridad UNIQUE (nombre_prioridad));
COMMENT ON TABLE Prioridades IS 'Tabla que almacena las prioridades de los tickets';


CREATE SEQUENCE Seq_categorias
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Categorias (
id_categoria NUMBER PRIMARY KEY,
nombre_categoria VARCHAR2(20) NOT NULL,
CONSTRAINT u_nombre_categoria UNIQUE (nombre_categoria));
COMMENT ON TABLE Categorias IS 'Tabla que almacena las categorías de artículos';


CREATE SEQUENCE Seq_tipo_ubicacion
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Tipo_ubicacion (
id_tipo_ubicacion NUMBER PRIMARY KEY,
nombre_tipo_ubicacion VARCHAR2(50) NOT NULL,
CONSTRAINT u_nombre_tipo_ubicacion UNIQUE (nombre_tipo_ubicacion));
COMMENT ON TABLE Tipo_ubicacion IS 'Tabla que almacena los tipos de ubicaciones';
COMMENT ON COLUMN Tipo_ubicacion.id_tipo_ubicacion IS 'Identificador único del tipo de ubicación';
COMMENT ON COLUMN Tipo_ubicacion.nombre_tipo_ubicacion IS 'Nombre del tipo de ubicación';


CREATE SEQUENCE Seq_marcas
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Marcas (
id_marca NUMBER PRIMARY KEY,
nombre_marca VARCHAR2(20) NOT NULL,
CONSTRAINT u_nombre_marca UNIQUE (nombre_marca));
COMMENT ON TABLE Marcas IS 'Tabla que almacena las marcas de los artículos';


CREATE SEQUENCE Seq_modelos
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Modelos (
id_modelo NUMBER PRIMARY KEY,
nombre_modelo VARCHAR2(20) NOT NULL,
id_marca NUMBER NOT NULL,
CONSTRAINT u_nombre_modelo UNIQUE (nombre_modelo),
CONSTRAINT fk_modelo_marca FOREIGN KEY (id_marca) REFERENCES Marcas(id_marca));
COMMENT ON TABLE Modelos IS 'Tabla que almacena los modelos de artículos, cada uno perteneciente a una marca';
COMMENT ON COLUMN Modelos.id_marca IS 'Marca a la que pertenece el modelo';


CREATE SEQUENCE Seq_departamentos
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Departamentos (
id_departamento NUMBER PRIMARY KEY,
nombre_departamento VARCHAR2(20) NOT NULL,
id_area NUMBER NOT NULL,
CONSTRAINT u_nombre_departamento UNIQUE (nombre_departamento),
CONSTRAINT fk_departamento_area FOREIGN KEY (id_area) REFERENCES Areas (id_area));
COMMENT ON TABLE Departamentos IS 'Tabla que almacena los departamentos';
COMMENT ON COLUMN Departamentos.id_departamento IS 'Identificador único del departamento';
COMMENT ON COLUMN Departamentos.nombre_departamento IS 'Nombre del o departamento';


CREATE SEQUENCE Seq_usuarios
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Usuarios (
id_usuario NUMBER PRIMARY KEY,
nombre_usuario VARCHAR2(20) NOT NULL,
correo VARCHAR2(30) NOT NULL,
clave VARCHAR2(20) NOT NULL,
imagen VARCHAR2(300),
id_rol NUMBER NOT NULL,
id_departamento NUMBER NOT NULL,
CONSTRAINT u_correo UNIQUE (correo),
CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
CONSTRAINT fk_usuario_departamento FOREIGN KEY (id_departamento) REFERENCES Departamentos (id_departamento));
COMMENT ON TABLE Usuarios IS 'Tabla que almacena la información de los usuarios del sistema';
COMMENT ON COLUMN Usuarios.id_usuario IS 'Identificador único del usuario';
COMMENT ON COLUMN Usuarios.nombre_usuario IS 'Nombre del usuario';
COMMENT ON COLUMN Usuarios.correo IS 'Correo electrónico del usuario';
COMMENT ON COLUMN Usuarios.clave IS 'Contraseña del usuario';
COMMENT ON COLUMN Usuarios.imagen IS 'Referencia de la imagen de perfil del usuario';
COMMENT ON COLUMN Usuarios.id_rol IS 'Rol asignado al usuario';
COMMENT ON COLUMN Usuarios.id_departamento IS 'departamento a la que pertenece el usuario';


CREATE SEQUENCE Seq_ubicaciones
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Ubicaciones (
id_ubicacion NUMBER PRIMARY KEY,
nombre_ubicacion VARCHAR2(50) NOT NULL,
id_tipo_ubicacion NUMBER,
CONSTRAINT u_nombre_ubicacion UNIQUE (nombre_ubicacion),
CONSTRAINT fk_ubicacion_tipo FOREIGN KEY (id_tipo_ubicacion) REFERENCES Tipo_ubicacion(id_tipo_ubicacion));
COMMENT ON TABLE Ubicaciones IS 'Tabla que almacena las ubicaciones de los artículos';
COMMENT ON COLUMN Ubicaciones.id_tipo_ubicacion IS 'Tipo de ubicación registrada';


CREATE SEQUENCE Seq_articulos
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Articulos (
id_articulo NUMBER PRIMARY KEY,
codigo VARCHAR2(20) NOT NULL,
id_modelo NUMBER,
id_categoria NUMBER NOT NULL,
id_ubicacion NUMBER NOT NULL,
CONSTRAINT u_codigo UNIQUE (codigo),
CONSTRAINT fk_articulo_modelo FOREIGN KEY (id_modelo) REFERENCES Modelos(id_modelo),
CONSTRAINT fk_articulo_categoria FOREIGN KEY (id_categoria) REFERENCES Categorias(id_categoria),
CONSTRAINT fk_articulo_ubicacion FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion));
COMMENT ON TABLE Articulos IS 'Tabla que almacena los artículos (equipos y mobiliarios) registrados';
COMMENT ON COLUMN Articulos.id_articulo IS 'Identificador único del artículo';
COMMENT ON COLUMN Articulos.codigo IS 'Código único asignado al artículo';
COMMENT ON COLUMN Articulos.id_modelo IS 'Modelo del artículo (a través del cual se conoce su marca)';
COMMENT ON COLUMN Articulos.id_categoria IS 'Categoría del artículo';
COMMENT ON COLUMN Articulos.id_ubicacion IS 'Ubicación del artículo';


CREATE SEQUENCE Seq_tickets
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Tickets (
id_ticket NUMBER PRIMARY KEY,
asunto VARCHAR2(100) NOT NULL,
descripcion VARCHAR2(100) NOT NULL,
descripcion_falla VARCHAR2(100),
descripcion_solucion VARCHAR2(100),
fecha_vencimiento DATE,
codigo NUMBER,
id_departamento NUMBER NOT NULL,
id_usuario_creador NUMBER NOT NULL,
id_usuario_tecnico NUMBER NOT NULL,
id_prioridad NUMBER NOT NULL,
estado VARCHAR2(50) NOT NULL,
CONSTRAINT fk_ticket_departamento FOREIGN KEY (id_departamento) REFERENCES Departamentos (id_departamento),
CONSTRAINT u_codigo_tickets UNIQUE (codigo),
CONSTRAINT fk_ticket_usuario_creador FOREIGN KEY (id_usuario_creador) REFERENCES Usuarios(id_usuario),
CONSTRAINT fk_ticket_usuario_tecnico FOREIGN KEY (id_usuario_tecnico) REFERENCES Usuarios(id_usuario),
CONSTRAINT fk_ticket_prioridad FOREIGN KEY (id_prioridad) REFERENCES Prioridades(id_prioridad));
COMMENT ON TABLE Tickets IS 'Tabla que almacena los tickets generados por los usuarios';
COMMENT ON COLUMN Tickets.id_ticket IS 'Identificador único del ticket';
COMMENT ON COLUMN Tickets.asunto IS 'Título o asunto del ticket';
COMMENT ON COLUMN Tickets.descripcion IS 'Descripción general del ticket';
COMMENT ON COLUMN Tickets.descripcion_falla IS 'Descripción de la falla reportada dado por el tecnico';
COMMENT ON COLUMN Tickets.descripcion_solucion IS 'Descripción de la solución aplicada dado por el tecnico';
COMMENT ON COLUMN Tickets.fecha_vencimiento IS 'Fecha límite de resolución del ticket';
COMMENT ON COLUMN Tickets.id_usuario_creador IS 'Usuario que creó el ticket';
COMMENT ON COLUMN Tickets.id_usuario_tecnico IS 'Técnico asignado al ticket';
COMMENT ON COLUMN Tickets.id_prioridad IS 'Prioridad asignada al ticket';
COMMENT ON COLUMN Tickets.estado IS 'Estado actual del ticket';
COMMENT ON COLUMN Tickets.id_departamento IS 'Departamento asignado del ticket';
COMMENT ON COLUMN Tickets.codigo IS 'codigo del ticket';


CREATE SEQUENCE Seq_detalle_ts
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Detalle_TS (
id_detalle_TS NUMBER PRIMARY KEY,
id_ticket NUMBER NOT NULL,
nombre_software VARCHAR2(50),
version VARCHAR2(20),
ubicacion VARCHAR2(200),
CONSTRAINT fk_detalle_ts_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket));
COMMENT ON TABLE Detalle_TS IS 'Tabla que almacena el detalle de tickets relacionados a software';
COMMENT ON COLUMN Detalle_TS.id_detalle_TS IS 'Identificador único del detalle de software';
COMMENT ON COLUMN Detalle_TS.id_ticket IS 'Ticket relacionado';
COMMENT ON COLUMN Detalle_TS.nombre_software IS 'Nombre del software relacionado con el ticket';
COMMENT ON COLUMN Detalle_TS.version IS 'Versión del software relacionado con el ticket';
COMMENT ON COLUMN Detalle_TS.ubicacion IS 'Ubicación del equipo donde está instalado el software';


CREATE SEQUENCE Seq_detalle_tna
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Detalle_TNA (
id_detalle_TNA NUMBER PRIMARY KEY,
id_ticket NUMBER NOT NULL,
descripcion_ubicacion VARCHAR2(200),
CONSTRAINT fk_detalle_tna_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket));
COMMENT ON TABLE Detalle_TNA IS 'Tabla que almacena el detalle de tickets que no están relacionados a un artículo específico';
COMMENT ON COLUMN Detalle_TNA.id_detalle_TNA IS 'Identificador único del detalle';
COMMENT ON COLUMN Detalle_TNA.id_ticket IS 'Ticket relacionado';
COMMENT ON COLUMN Detalle_TNA.descripcion_ubicacion IS 'Descripción de la ubicación relacionada al ticket';


CREATE SEQUENCE Seq_detalle_ta
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Detalle_TA (
id_detalle_TA NUMBER PRIMARY KEY,
id_ticket NUMBER NOT NULL,
id_articulos NUMBER NOT NULL,
CONSTRAINT fk_detalle_ta_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket),
CONSTRAINT fk_detalle_ta_articulo FOREIGN KEY (id_articulos) REFERENCES Articulos(id_articulo));
COMMENT ON TABLE Detalle_TA IS 'Tabla que almacena el detalle de tickets relacionados a un artículo específico';
COMMENT ON COLUMN Detalle_TA.id_detalle_TA IS 'Identificador único del detalle';
COMMENT ON COLUMN Detalle_TA.id_ticket IS 'Ticket relacionado';
COMMENT ON COLUMN Detalle_TA.id_articulos IS 'Artículo relacionado con el ticket';


CREATE SEQUENCE Seq_bitacoras
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Bitacoras (
id_bitacora NUMBER PRIMARY KEY,
id_ticket NUMBER NOT NULL,
id_usuario NUMBER NOT NULL,
nuevo_estado VARCHAR2(50) NOT NULL,
fecha_hora DATE DEFAULT SYSDATE,
CONSTRAINT fk_bitacora_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket),
CONSTRAINT fk_bitacora_usuario FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario));
COMMENT ON TABLE Bitacoras IS 'Tabla que almacena el historial o seguimiento de tickets. El primer registro (estado "Abierto") funciona como fecha de creación del ticket';
COMMENT ON COLUMN Bitacoras.id_bitacora IS 'Identificador único de la bitácora';
COMMENT ON COLUMN Bitacoras.id_ticket IS 'Ticket relacionado';
COMMENT ON COLUMN Bitacoras.id_usuario IS 'Usuario que realizó el cambio';
COMMENT ON COLUMN Bitacoras.nuevo_estado IS 'Nuevo estado asignado al ticket';
COMMENT ON COLUMN Bitacoras.fecha_hora IS 'Fecha y hora del registro en la bitácora';


CREATE SEQUENCE Seq_evaluaciones
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Evaluaciones (
id_evaluacion NUMBER PRIMARY KEY,
calificacion NUMBER NOT NULL,
comentario VARCHAR2(200),
id_ticket NUMBER NOT NULL,
CONSTRAINT fk_evaluacion_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket));
COMMENT ON TABLE Evaluaciones IS 'Tabla que almacena las evaluaciones realizadas a los tickets';
COMMENT ON COLUMN Evaluaciones.id_evaluacion IS 'Identificador único de la evaluación';
COMMENT ON COLUMN Evaluaciones.calificacion IS 'Calificación otorgada al servicio';
COMMENT ON COLUMN Evaluaciones.comentario IS 'Comentario realizado por el usuario';
COMMENT ON COLUMN Evaluaciones.id_ticket IS 'Ticket evaluado';


CREATE SEQUENCE Seq_comentarios
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Comentarios (
id_comentario NUMBER PRIMARY KEY,
comentario VARCHAR2(300) NOT NULL,
fecha_hora DATE DEFAULT SYSDATE,
id_ticket NUMBER NOT NULL,
id_usuario_comentario NUMBER NOT NULL,
CONSTRAINT fk_comentario_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket),
CONSTRAINT fk_comentario_usuario FOREIGN KEY (id_usuario_comentario) REFERENCES Usuarios(id_usuario));
COMMENT ON TABLE Comentarios IS 'Tabla que almacena los comentarios realizados por los usuarios sobre un ticket';
COMMENT ON COLUMN Comentarios.id_comentario IS 'Identificador único del comentario';
COMMENT ON COLUMN Comentarios.comentario IS 'Contenido del comentario';
COMMENT ON COLUMN Comentarios.fecha_hora IS 'Fecha y hora en que se realizó el comentario';
COMMENT ON COLUMN Comentarios.id_ticket IS 'Ticket al que pertenece el comentario';
COMMENT ON COLUMN Comentarios.id_usuario_comentario IS 'Usuario que realizó el comentario';


CREATE SEQUENCE Seq_multimedia_comentarios
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Multimedia_comentarios (
id_multimedia NUMBER PRIMARY KEY,
multimedia_url VARCHAR2(300) NOT NULL,
id_comentario NUMBER NOT NULL,
CONSTRAINT fk_multimedia_comentario FOREIGN KEY (id_comentario) REFERENCES Comentarios(id_comentario));
COMMENT ON TABLE Multimedia_comentarios IS 'Tabla que almacena los archivos multimedia adjuntos a un comentario';
COMMENT ON COLUMN Multimedia_comentarios.id_multimedia IS 'Identificador único del archivo multimedia';
COMMENT ON COLUMN Multimedia_comentarios.multimedia_url IS 'Ruta o referencia del archivo multimedia';
COMMENT ON COLUMN Multimedia_comentarios.id_comentario IS 'Comentario al que pertenece el archivo multimedia';


CREATE SEQUENCE Seq_evidencias
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE TABLE Evidencias (
id_evidencia NUMBER PRIMARY KEY,
evidencia_url VARCHAR2(300) NOT NULL,
id_ticket NUMBER NOT NULL,
CONSTRAINT fk_evidencia_ticket FOREIGN KEY (id_ticket) REFERENCES Tickets(id_ticket));
COMMENT ON TABLE Evidencias IS 'Tabla que almacena los archivos multimedia adjuntos a un ticket';
COMMENT ON COLUMN Evidencias.id_evidencia IS 'Identificador único de la evidencia';
COMMENT ON COLUMN Evidencias.evidencia_url IS 'Ruta o referencia del archivo de evidencia';
COMMENT ON COLUMN Evidencias.id_ticket IS 'Ticket al que pertenece la evidencia';

COMMIT;
