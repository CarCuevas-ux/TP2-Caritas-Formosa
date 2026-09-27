package ar.caritas.donaciones.modelo;
public record SolicitudPendiente(int idSolicitud, String dni, String beneficiario,
                                 String barrio, String tipoElemento,
                                 String descripcion, int cantidadPendiente) {}