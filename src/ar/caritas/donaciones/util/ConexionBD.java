package ar.caritas.donaciones.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {
    private static final String URL =
        "jdbc:mysql://localhost:3306/caritas_donaciones?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires";
    private static final String USUARIO = "root";
    private static final String CLAVE = "CAMBIAR_CLAVE";

    private ConexionBD() {}
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}