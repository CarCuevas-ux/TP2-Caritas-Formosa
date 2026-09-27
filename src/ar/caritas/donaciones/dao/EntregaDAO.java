package ar.caritas.donaciones.dao;
import ar.caritas.donaciones.util.ConexionBD;
import java.sql.*;

public class EntregaDAO {
    public void registrar(int idBeneficiario,int idSolicitud,String tipo,String descripcion,int cantidad) throws SQLException {
        String e="INSERT INTO entrega(fecha,observaciones,id_beneficiario,id_solicitud) VALUES(CURDATE(),'Entrega registrada',?,?)";
        String de="INSERT INTO detalle_entrega(id_entrega,tipo_elemento,descripcion,cantidad) VALUES(?,?,?,?)";
        String act="UPDATE detalle_solicitud SET cantidad_atendida=LEAST(cantidad_solicitada,cantidad_atendida+?) WHERE id_solicitud=? AND tipo_elemento=?";
        String est="""UPDATE solicitud s SET estado=
          CASE
            WHEN NOT EXISTS (SELECT 1 FROM detalle_solicitud d WHERE d.id_solicitud=s.id_solicitud AND d.cantidad_atendida<d.cantidad_solicitada) THEN 'ATENDIDA'
            WHEN EXISTS (SELECT 1 FROM detalle_solicitud d WHERE d.id_solicitud=s.id_solicitud AND d.cantidad_atendida>0) THEN 'PARCIALMENTE_ATENDIDA'
            ELSE 'PENDIENTE'
          END WHERE s.id_solicitud=?""";
        try(Connection c=ConexionBD.obtenerConexion()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement(e,Statement.RETURN_GENERATED_KEYS)){
                p.setInt(1,idBeneficiario);p.setInt(2,idSolicitud);p.executeUpdate();
                try(ResultSet k=p.getGeneratedKeys()){
                    k.next(); int idEntrega=k.getInt(1);
                    try(PreparedStatement d=c.prepareStatement(de)){
                        d.setInt(1,idEntrega);d.setString(2,tipo);d.setString(3,descripcion);d.setInt(4,cantidad);d.executeUpdate();
                    }
                }
                try(PreparedStatement a=c.prepareStatement(act)){
                    a.setInt(1,cantidad);a.setInt(2,idSolicitud);a.setString(3,tipo);a.executeUpdate();
                }
                try(PreparedStatement s=c.prepareStatement(est)){s.setInt(1,idSolicitud);s.executeUpdate();}
                c.commit();
            } catch(Exception ex){c.rollback();throw ex;}
        }
    }
}