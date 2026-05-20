package model;

import utilidades.Util;
import utilidades.Roll;

import java.text.Normalizer;

public class Operador extends Persona implements Util {
    private String codigo;
    private Roll roll;
    private String contrasena;

    public Operador(String nombre, int id, String codigo, Roll roll, String contrasena) {
        super(nombre, id);
        this.codigo = codigo;
        this.roll = roll;
        this.contrasena = contrasena;
    }

    @Override
    public String normalizar(String texto){
        return Normalizer.normalize(texto,Normalizer.Form.NFD)
                .replaceAll("\\p{M}","")
                .toLowerCase();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    @Override
    public String toString(){
        return super.toString()+ " Codigo: "+codigo;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;

    }

    public Roll getRoll() {
        return roll;
    }

    public void setRoll(Roll roll) {
        this.roll = roll;
    }
}
