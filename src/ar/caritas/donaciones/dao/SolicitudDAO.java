package ar.caritas.donaciones.dao;
import ar.caritas.donaciones.modelo.SolicitudPendiente;
import ar.caritas.donaciones.util.ConexionBD;
import java.sql.*;
import java.util.*;

public class SolicitudDAO {
    public int registrar(int idBeneficiario,String motivo,boolean emergencia,
                         String tipo,String descripcion,int cantidad) throws SQLException {
        String cab="INSERT INTO solicitud(fecha,motivo,emergencia,estado,id_beneficiario) VALUES(CURDATE(),?,?,'PENDIENTE',?)";
        String det="INSERT INTO detalle_solicitud(id_solicitud,tipo_elemento,descripcion,cantidad_solicitada,cantidad_atendida) VALUES(?,?,?,?,0)";
        try(Connection c=ConexionBD.obtenerConexion()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement(cab,Statement.RETURN_GENERATED_KEYS)){
                p.setString(1,motivo); p.setBoolean(2,emergencia); p.setInt(3,idBeneficiario);
                p.executeUpdate();
                try(ResultSet k=p.getGeneratedKeys()){
                    if(!k.next()) throw new SQLException("No se obtuvo el ID de solicitud.");
                    int id=k.getInt(1);
                    try(PreparedStatement d=c.prepareStatement(det)){
                        d.setInt(1,id); d.setString(2,tipo); d.setString(3,descripcion); d.setInt(4,cantidad);
                        d.executeUpdate();
                    }
                    c.commit(); return id;
                }
            } catch(Exception e){ c.rollback(); throw e; }
        }
    }

    public List<SolicitudPendiente> buscarCompatibles(String tipo) throws SQLException {
        String q=""" 
        SELECT s.id_solicitud,b.dni,CONCAT(b.apellido,', ',b.nombre) beneficiario,
               ba.nombre barrio,ds.tipo_elemento,ds.descripcion,
               ds.cantidad_solicitada-ds.cantidad_atendida pendiente
        FROM solicitud s
        JOIN beneficiario b ON b.id_beneficiario=s.id_beneficiario
        JOIN barrio ba ON ba.id_barrio=b.id_barrio
        JOIN detalle_solicitud ds ON ds.id_solicitud=s.id_solicitud
        WHERE s.estado IN ('PENDIENTE','PARCIALMENTE_ATENDIDA')
          AND ds.tipo_elemento=? AND ds.cantidad_atendida<ds.cantidad_solicitada
        ORDER BY s.fecha
        """;
        List<SolicitudPendiente> lista=new ArrayList<>();
        try(Connection c=ConexionBD.obtenerConexion();PreparedStatement p=c.prepareStatement(q)){
            p.setString(1,tipo);
            try(ResultSet r=p.executeQuery()){
                while(r.next()) lista.add(new SolicitudPendiente(
                    r.getInt("id_solicitud"),r.getString("dni"),r.getString("beneficiario"),
                    r.getString("barrio"),r.getString("tipo_elemento"),r.getString("descripcion"),
                    r.getInt("pendiente")));
            }
        }
        return lista;
    }
}