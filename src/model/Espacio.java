package model;

import utilidades.Estado;
import utilidades.TipoVehiculo;
import utilidades.Util;

import java.text.Normalizer;

public class Espacio implements Util {
    private String codigo;
    private TipoVehiculo tipoEspacio;
    private Estado estado;
    private Vehiculo vehiculoAsignado;

    public Espacio(String codigo, TipoVehiculo tipoEspacio, Estado estado){
        this.codigo = codigo;
        this.tipoEspacio = tipoEspacio;
        this.estado = estado;

    }
    @Override
    public String normalizar(String texto){
        return Normalizer.normalize(texto,Normalizer.Form.NFD)
                .replaceAll("\\p{M}","")
                .replaceAll("\\s+","").trim()
                .toLowerCase();

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoVehiculo getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoVehiculo tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Vehiculo getVehiculoAsignado() {
        return vehiculoAsignado;
    }

    public void setVehiculoAsignado(Vehiculo vehiculoAsignado) {
        this.vehiculoAsignado = vehiculoAsignado;
    }
    @Override

    public String toString(){
        return "Codigo: "+codigo+" Tipo de espacio: "+tipoEspacio+" Estado: "+estado;
    }
}

