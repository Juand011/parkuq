package Test;

import model.Espacio;
import model.Pago;
import model.Tarifa;
import model.Vehiculo;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import utilidades.Estado;
import utilidades.EstadoVehiculo;
import utilidades.TipoUsuario;
import utilidades.TipoVehiculo;

import static org.junit.jupiter.api.Assertions.*;

public class PagoTest {

    // Método auxiliar para crear un vehículo válido para las pruebas y evitar repetir código
    private Vehiculo crearVehiculoMock() {
        return new Vehiculo(
                "XYZ123",
                TipoVehiculo.CARRO,
                "Juan Perez",
                LocalDateTime.now(),
                TipoUsuario.ESTUDIANTE
        );
    }

    @Test
    void testConstructor() {
        // Corregido: Creamos el vehículo y la tarifa usando constructores reales
        Vehiculo vehiculo = crearVehiculoMock();
        Tarifa tarifa = new Tarifa(TipoVehiculo.CARRO, 2000.0);

        Pago pago = new Pago(vehiculo, tarifa);

        assertEquals(vehiculo, pago.getVehiculo());
        assertEquals(tarifa, pago.getTarifa());
    }

    @Test
    void testSettersAndGetters() {
        Pago pago = new Pago(null, null);

        Vehiculo vehiculo = crearVehiculoMock();
        Tarifa tarifa = new Tarifa(TipoVehiculo.MOTO, 1000.0);

        pago.setVehiculo(vehiculo);
        pago.setTarifa(tarifa);

        assertEquals(vehiculo, pago.getVehiculo());
        assertEquals(tarifa, pago.getTarifa());
    }

    @Test
    void testToString() {
        Vehiculo vehiculo = crearVehiculoMock();
        // Asignamos un estado de vehículo para evitar valores null en el toString de Vehiculo
        vehiculo.setEstadoVehiculo(EstadoVehiculo.ADENTRO);

        Tarifa tarifa = new Tarifa(TipoVehiculo.CARRO, 2000.0, 0.0);

        Pago pago = new Pago(vehiculo, tarifa);
        pago.setValorPagar(2000.0); // Asignamos el valor esperado de la prueba

        // El resultado esperado debe mapear el toString() de Vehiculo, luego Tarifa, el precio y la fecha
        String esperado = vehiculo + "\n" + tarifa + "\n" + "Debe pagar $2000.0\n" + "Fecha: Sin registro";

        assertEquals(esperado, pago.toString());
    }
}