package ar.caritas.donaciones;
import ar.caritas.donaciones.dao.*;
public class Main {
    public static void main(String[] args) {
        try {
            BeneficiarioDAO beneficiarios=new BeneficiarioDAO();
            SolicitudDAO solicitudes=new SolicitudDAO();
            DonacionDAO donaciones=new DonacionDAO();

            var b=beneficiarios.buscarPorDni("32222333");
            System.out.println("Beneficiario: "+b);

            int donacion=donaciones.registrarAnonima("COLCHON","Colchón de una plaza",1);
            System.out.println("Donación registrada: "+donacion);

            System.out.println("Solicitudes pendientes compatibles:");
            for(var s:solicitudes.buscarCompatibles("COLCHON")){
                System.out.println(s);
            }
        } catch(Exception e) {
            System.err.println("Error: "+e.getMessage());
        }
    }
}