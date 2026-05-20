package Test;

import model.Espacio;
import org.junit.jupiter.api.Test;
import utilidades.Estado;
import utilidades.TipoVehiculo;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class EspacioTest {

    @Test
    void testConstructor() {
        // Corregido: Se añade Estado.DISPONIBLE porque tu constructor exige 3 parámetros
        Espacio espacio = new Espacio("A1", TipoVehiculo.CARRO, Estado.DISPONIBLE);

        assertEquals("A1", espacio.getCodigo());
        assertEquals(TipoVehiculo.CARRO, espacio.getTipoEspacio());
        assertEquals(Estado.DISPONIBLE, espacio.getEstado());
    }

    @Test
    void testSettersAndGetters() {
        Espacio espacio = new Espacio("B1", TipoVehiculo.MOTO, Estado.DISPONIBLE);

        espacio.setCodigo("C2");
        espacio.setTipoEspacio(TipoVehiculo.CARRO);
        espacio.setEstado(Estado.OCUPADO);

        assertEquals("C2", espacio.getCodigo());
        assertEquals(TipoVehiculo.CARRO, espacio.getTipoEspacio());
        assertEquals(Estado.OCUPADO, espacio.getEstado());
    }

    @Test
    void testNormalizar() {
        Espacio espacio = new Espacio("A1", TipoVehiculo.CARRO, Estado.DISPONIBLE);

        String resultado = espacio.normalizar("  ÁRBOL Ñandú  ");

        assertEquals("arbolnandu", resultado);
    }

    @Test
    void testToString() {
        Espacio espacio = new Espacio("A1", TipoVehiculo.CARRO, Estado.DISPONIBLE);

        String esperado = "Codigo: A1 Tipo de espacio: CARRO Estado: DISPONIBLE";

        assertEquals(esperado, espacio.toString());
    }
}