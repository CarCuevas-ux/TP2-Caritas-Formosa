package ar.caritas.donaciones.modelo;
public class Beneficiario {
    private int id;
    private final String dni, nombre, apellido;
    public Beneficiario(String dni,String nombre,String apellido){
        this.dni=dni; this.nombre=nombre; this.apellido=apellido;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getDni(){return dni;} public String getNombre(){return nombre;}
    public String getApellido(){return apellido;}
    public String toString(){return dni+" - "+apellido+", "+nombre;}
}