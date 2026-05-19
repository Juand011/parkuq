package model;

import utilidades.EstadoVehiculo;
import utilidades.TipoUsuario;
import utilidades.TipoVehiculo;
import utilidades.Util;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Vehiculo implements Util {
    private String placa;
    private TipoVehiculo vehiculo;
    private String nombreConductor;
    private LocalDateTime horaIngreso;
    private LocalDateTime horaSalida;
    private Espacio espacioAsignado;
    private EstadoVehiculo estadoVehiculo;
    private TipoUsuario tipoUsuario;

    public Vehiculo(String placa, TipoVehiculo vehiculo, String nombreConductor, LocalDateTime horaIngreso, TipoUsuario tipoUsuario) {
        this.placa = placa;
        this.vehiculo = vehiculo;
        this.nombreConductor = nombreConductor;
        this.horaIngreso = horaIngreso;
        this.tipoUsuario = tipoUsuario;
    }
    public Vehiculo( TipoVehiculo vehiculo, String nombreConductor, LocalDateTime horaIngreso, TipoUsuario tipoUsuario) {
        this.vehiculo = vehiculo;
        this.nombreConductor = nombreConductor;
        this.horaIngreso = horaIngreso;
        this.tipoUsuario = tipoUsuario;
    }

    public Vehiculo(TipoVehiculo vehiculo, String nombreConductor, LocalDateTime horaIngreso, LocalDateTime horaSalida, Espacio espacioAsignado, EstadoVehiculo estadoVehiculo,TipoUsuario tipoUsuario){
        this.vehiculo = vehiculo;
        this.nombreConductor = nombreConductor;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.espacioAsignado = espacioAsignado;
        espacioAsignado.setVehiculoAsignado(this);
        this.estadoVehiculo = estadoVehiculo;
        this.tipoUsuario = tipoUsuario;
    }


    @Override
    public String normalizar(String texto){
        return Normalizer.normalize(texto,Normalizer.Form.NFD)
                .replaceAll("\\p{M}","")
                .replaceAll("\\s+","").trim()
                .toLowerCase();
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getPlaca() {
        return placa;
    }

    public TipoVehiculo getTipoVehiculo() {
        return vehiculo;
    }
    public void setTipoVehiculo(TipoVehiculo vehiculo) {}

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(TipoVehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public LocalDateTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalDateTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Espacio getEspacioAsignado() {
        return espacioAsignado;
    }

    public void setEspacioAsignado(Espacio espacioAsignado) {
        this.espacioAsignado = espacioAsignado;
    }

    public EstadoVehiculo getEstadoVehiculo() {
        return estadoVehiculo;
    }

    public void setEstadoVehiculo(EstadoVehiculo estadoVehiculo) {
        this.estadoVehiculo = estadoVehiculo;
    }

    public String formatearHora(ZonedDateTime hora){
        DateTimeFormatter formato = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).
                withLocale(Locale.getDefault());
        return hora.format(formato);
    }

    @Override
    public String toString (){
        return " Placa: "+(placa != null ? placa : "Este vehiculo no tiene placa")+" \n"+
                " Tipo de vehiculo: "+vehiculo+" \n"+
                " Nombre del conductor: "+nombreConductor+" \n"+
                " Hora de ingreso: "+formatearHora(horaIngreso.atZone(ZoneId.systemDefault()))+" \n"+
                " Hora de salida: "+(horaSalida != null ? formatearHora(horaSalida.atZone(ZoneId.systemDefault())) : "Sin registro")+" \n"+
                " Espacio asignado: "+(espacioAsignado != null ? espacioAsignado : "Sin espacio")+" \n"+
                " Estado del vehiculo: "+estadoVehiculo+" \n";
    }
}
