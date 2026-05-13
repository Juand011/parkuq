package services;

import model.Espacio;
import utilidades.Estado;
import utilidades.EstadoVehiculo;
import utilidades.TipoUsuario;
import model.Pago;
import model.Tarifa;
import model.Vehiculo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class  PagoServices {
    private ArrayList<Pago> pagos;
    private ArrayList<Tarifa> tarifas;
    private ArrayList<Vehiculo> vehiculos;

    Parqueadero p = new Parqueadero();

    public PagoServices(){
        this.pagos = new ArrayList<>();
        this.tarifas = new ArrayList<>();
        this.vehiculos = new ArrayList<>();
    }

    public ArrayList<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(ArrayList<Pago> pagos) {
        this.pagos = pagos;
    }

    public ArrayList<Tarifa> getTarifas() {
        return tarifas;
    }

    public void setTarifas(ArrayList<Tarifa> tarifas) {
        this.tarifas = tarifas;
    }

    public ArrayList<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(ArrayList<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }


    public void crearPago(Pago pago){
        if (pago != null){
            pagos.add(pago);
            pago.getVehiculo().setEstadoVehiculo(EstadoVehiculo.SALIO);
            pago.setValorPagar(calcularPago(pago));
            pago.setFecha(pago.getVehiculo().getHoraSalida());
        }
    }

    public void eliminarPago(Pago pago){
        if(pago != null){
            pagos.remove(pago);
        }
    }
    public String listarPago (){
        if(pagos.isEmpty()){
            return "No hay pagos registrados";
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < pagos.size(); i++){
            sb.append(i+1).append(". ").append(pagos.get(i)).append("\n");
        }
        return sb.toString();
    }
    public double calcularPago(Pago pago) {

        if(pago != null) {

            pago.getVehiculo().setHoraSalida(LocalDateTime.now());

            Duration d = Duration.between(
                    pago.getVehiculo().getHoraIngreso(),
                    pago.getVehiculo().getHoraSalida()
            );

            long minutos = d.toMinutes();
            long horasCobradas = (minutos + 59) / 60;

            Espacio espacio = pago.getVehiculo().getEspacioAsignado();

            if(espacio != null){
                espacio.setEstado(Estado.DISPONIBLE);
                espacio.setVehiculoAsignado(null);

                pago.getVehiculo().setEspacioAsignado(null);
            }

            if(pago.getVehiculo().getTipoUsuario().equals(TipoUsuario.INVITADO)) {

                return horasCobradas * pago.getTarifa().getValorHora();

            } else {

                return horasCobradas *
                        (pago.getTarifa().getValorHora()
                                - pago.getTarifa().getDescuento());
            }
        }

        return -1;
    }

    public double ingresosGeneradosPorDia(LocalDateTime fecha){
        LocalDate fechaBuscada = fecha.toLocalDate();

        return pagos.stream()
                .filter(pago -> pago.getFecha().toLocalDate().equals(fechaBuscada))
                .mapToDouble(Pago::getValorPagar)
                .sum();
    }

    public double tiempoPromedioDePermanencia( LocalDateTime fecha){
        LocalDate fechaBuscada = fecha.toLocalDate();

        return pagos.stream()
                .filter(pago -> pago.getFecha().toLocalDate().equals(fechaBuscada))
                .mapToLong(pago -> Duration.between(pago.getVehiculo().getHoraIngreso(), pago.getFecha()).toHours())
                .average()
                .orElse(0.0);
    }
    public double ingresosGeneradosPormes(int anio, int mes){
        return pagos.stream()
                .filter(p-> p.getFecha().getYear()== anio && p.getFecha().getMonthValue() == mes)
                .mapToDouble(Pago::getValorPagar)
                .sum();
    }

    public double ingresosGeneradosPorAnio(int anio){
        return pagos.stream()
                .filter(p-> p.getFecha().getYear()== anio)
                .mapToDouble(Pago::getValorPagar)
                .sum();
    }

    public double vehiculosQuePermanecireonUnTiempoDado(long horasMinimas) {
        return pagos.stream()
                .filter(pago -> {
                    long horasEstadia = Duration.between(
                            pago.getVehiculo().getHoraIngreso(),
                            pago.getFecha().toLocalTime()
                    ).toHours();

                    return horasEstadia > horasMinimas;
                })
                .count();
    }

}
