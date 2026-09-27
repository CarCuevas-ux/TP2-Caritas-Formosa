package ar.caritas.donaciones.dao;
import ar.caritas.donaciones.modelo.Beneficiario;
import ar.caritas.donaciones.util.ConexionBD;
import java.sql.*;

public class BeneficiarioDAO {
    public Beneficiario buscarPorDni(String dni) throws SQLException {
        String sql="SELECT id_beneficiario,dni,nombre,apellido FROM beneficiario WHERE dni=?";
        try(Connection c=ConexionBD.obtenerConexion(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,dni);
            try(ResultSet r=p.executeQuery()){
                if(r.next()){
                    Beneficiario b=new Beneficiario(r.getString("dni"),r.getString("nombre"),r.getString("apellido"));
                    b.setId(r.getInt("id_beneficiario")); return b;
                }
            }
        }
        return null;
    }
}