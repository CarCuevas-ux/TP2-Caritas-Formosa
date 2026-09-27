package ar.caritas.donaciones.dao;
import ar.caritas.donaciones.util.ConexionBD;
import java.sql.*;

public class DonacionDAO {
    public int registrarAnonima(String tipo,String descripcion,int cantidad) throws SQLException {
        String cab="INSERT INTO donacion(fecha,observaciones,id_donante) VALUES(CURDATE(),'Donación anónima',NULL)";
        String det="INSERT INTO detalle_donacion(id_donacion,tipo_elemento,descripcion,cantidad) VALUES(?,?,?,?)";
        try(Connection c=ConexionBD.obtenerConexion()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement(cab,Statement.RETURN_GENERATED_KEYS)){
                p.executeUpdate();
                try(ResultSet k=p.getGeneratedKeys()){
                    if(!k.next()) throw new SQLException("No se obtuvo ID de donación.");
                    int id=k.getInt(1);
                    try(PreparedStatement d=c.prepareStatement(det)){
                        d.setInt(1,id); d.setString(2,tipo); d.setString(3,descripcion); d.setInt(4,cantidad);
                        d.executeUpdate();
                    }
                    c.commit(); return id;
                }
            } catch(Exception e){c.rollback();throw e;}
        }
    }
}