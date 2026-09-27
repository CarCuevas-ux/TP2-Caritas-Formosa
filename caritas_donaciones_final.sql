CREATE DATABASE IF NOT EXISTS caritas_donaciones
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE caritas_donaciones;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    clave_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    rol ENUM('DIRECTOR','SECRETARIO') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE sede (
    id_sede INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150),
    telefono VARCHAR(30)
);

CREATE TABLE barrio (
    id_barrio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_sede INT NOT NULL,
    FOREIGN KEY (id_sede) REFERENCES sede(id_sede)
);

CREATE TABLE grupo_familiar (
    id_grupo_familiar INT AUTO_INCREMENT PRIMARY KEY,
    fecha_registro DATE NOT NULL,
    observaciones VARCHAR(255)
);

CREATE TABLE beneficiario (
    id_beneficiario INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    domicilio VARCHAR(150),
    telefono VARCHAR(30),
    id_barrio INT NOT NULL,
    id_grupo_familiar INT NOT NULL,
    FOREIGN KEY (id_barrio) REFERENCES barrio(id_barrio),
    FOREIGN KEY (id_grupo_familiar) REFERENCES grupo_familiar(id_grupo_familiar)
);

CREATE TABLE solicitud (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    motivo VARCHAR(255),
    emergencia BOOLEAN NOT NULL DEFAULT FALSE,
    estado ENUM('PENDIENTE','PARCIALMENTE_ATENDIDA','ATENDIDA','CANCELADA')
           NOT NULL DEFAULT 'PENDIENTE',
    id_beneficiario INT NOT NULL,
    FOREIGN KEY (id_beneficiario) REFERENCES beneficiario(id_beneficiario)
);

CREATE TABLE detalle_solicitud (
    id_detalle_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    tipo_elemento VARCHAR(60) NOT NULL,
    descripcion VARCHAR(150),
    cantidad_solicitada INT NOT NULL,
    cantidad_atendida INT NOT NULL DEFAULT 0,
    CHECK (cantidad_solicitada > 0),
    CHECK (cantidad_atendida >= 0 AND cantidad_atendida <= cantidad_solicitada),
    FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud)
);

CREATE TABLE donante (
    id_donante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80),
    apellido_razon_social VARCHAR(120),
    telefono VARCHAR(30),
    email VARCHAR(120)
);

CREATE TABLE donacion (
    id_donacion INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    observaciones VARCHAR(255),
    id_donante INT NULL,
    FOREIGN KEY (id_donante) REFERENCES donante(id_donante)
);

CREATE TABLE detalle_donacion (
    id_detalle_donacion INT AUTO_INCREMENT PRIMARY KEY,
    id_donacion INT NOT NULL,
    tipo_elemento VARCHAR(60) NOT NULL,
    descripcion VARCHAR(150),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    FOREIGN KEY (id_donacion) REFERENCES donacion(id_donacion)
);

CREATE TABLE entrega (
    id_entrega INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    observaciones VARCHAR(255),
    id_beneficiario INT NOT NULL,
    id_solicitud INT NULL,
    FOREIGN KEY (id_beneficiario) REFERENCES beneficiario(id_beneficiario),
    FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud)
);

CREATE TABLE detalle_entrega (
    id_detalle_entrega INT AUTO_INCREMENT PRIMARY KEY,
    id_entrega INT NOT NULL,
    tipo_elemento VARCHAR(60) NOT NULL,
    descripcion VARCHAR(150),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    FOREIGN KEY (id_entrega) REFERENCES entrega(id_entrega)
);

-- Datos de demostración (sin contraseñas reales)
INSERT INTO sede(nombre,direccion) VALUES
('Sede Central','Formosa Capital'),
('Sede Parroquial Norte','Formosa Capital');

INSERT INTO barrio(nombre,id_sede) VALUES ('Centro',1),('Eva Perón',2);
INSERT INTO grupo_familiar(fecha_registro,observaciones) VALUES
(CURDATE(),'Familia de demostración'),
(CURDATE(),'Familia afectada por inundación');

INSERT INTO beneficiario(dni,nombre,apellido,domicilio,id_barrio,id_grupo_familiar) VALUES
('30111222','Ana','Gómez','Domicilio de demostración',1,1),
('32222333','Luis','Pérez','Domicilio de demostración',2,2);

INSERT INTO solicitud(fecha,motivo,emergencia,estado,id_beneficiario) VALUES
(CURDATE(),'Necesidad habitual',FALSE,'PENDIENTE',1),
(CURDATE(),'Inundación',TRUE,'PENDIENTE',2);

INSERT INTO detalle_solicitud
(id_solicitud,tipo_elemento,descripcion,cantidad_solicitada,cantidad_atendida) VALUES
(1,'MERCADERIA','Módulo alimentario',1,0),
(2,'CAMA','Cama de una plaza',1,0),
(2,'COLCHON','Colchón de una plaza',2,0);

INSERT INTO donacion(fecha,observaciones,id_donante)
VALUES (CURDATE(),'Donación anónima',NULL);

INSERT INTO detalle_donacion(id_donacion,tipo_elemento,descripcion,cantidad)
VALUES (1,'COLCHON','Colchón de una plaza',1);

-- Consulta central: necesidades compatibles
SELECT s.id_solicitud, b.dni, b.nombre, b.apellido, ba.nombre AS barrio,
       ds.tipo_elemento, ds.descripcion,
       (ds.cantidad_solicitada-ds.cantidad_atendida) AS cantidad_pendiente
FROM solicitud s
JOIN beneficiario b ON b.id_beneficiario=s.id_beneficiario
JOIN barrio ba ON ba.id_barrio=b.id_barrio
JOIN detalle_solicitud ds ON ds.id_solicitud=s.id_solicitud
WHERE s.estado IN ('PENDIENTE','PARCIALMENTE_ATENDIDA')
  AND ds.tipo_elemento='COLCHON'
  AND ds.cantidad_atendida < ds.cantidad_solicitada;

-- Demanda por barrio
SELECT ba.nombre, COUNT(DISTINCT s.id_solicitud) AS solicitudes
FROM barrio ba
JOIN beneficiario b ON b.id_barrio=ba.id_barrio
JOIN solicitud s ON s.id_beneficiario=b.id_beneficiario
GROUP BY ba.id_barrio,ba.nombre ORDER BY solicitudes DESC;

-- Demanda por sede
SELECT se.nombre, COUNT(DISTINCT s.id_solicitud) AS solicitudes
FROM sede se
JOIN barrio ba ON ba.id_sede=se.id_sede
JOIN beneficiario b ON b.id_barrio=ba.id_barrio
JOIN solicitud s ON s.id_beneficiario=b.id_beneficiario
GROUP BY se.id_sede,se.nombre ORDER BY solicitudes DESC;

-- Ejemplos CRUD
UPDATE solicitud SET estado='PARCIALMENTE_ATENDIDA' WHERE id_solicitud=2;
-- DELETE FROM detalle_solicitud WHERE id_detalle_solicitud = ?;
