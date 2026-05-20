package Test;

import model.Operador;
import org.junit.jupiter.api.Test;
import utilidades.Roll;

import static org.junit.jupiter.api.Assertions.*;

public class OperadorTest {

    @Test
    void testConstructor() {
        // Creamos un operador con datos de prueba válidos
        Operador operador = new Operador("Jhoan Doe", 4567, "OPE-102", Roll.OPERADOR, "claveOperador");

        // Validamos atributos heredados de la clase Persona
        assertEquals("Jhoan Doe", operador.getNombre());
        assertEquals(4567, operador.getId());

        // Validamos atributos propios de la clase Operador
        assertEquals("OPE-102", operador.getCodigo());
        assertEquals(Roll.OPERADOR, operador.getRoll()); // Compara directamente el Enum
        assertEquals("claveOperador", operador.getContrasena());
    }

    @Test
    void testSettersAndGetters() {
        Operador operador = new Operador("Temporal", 0, "TEMP", Roll.OPERADOR, "123");

        // Modificamos sus atributos mediante los métodos de acceso
        operador.setNombre("Diana Marcela");
        operador.setId(998877);
        operador.setCodigo("OPE-777");
        operador.setRoll(Roll.OPERADOR);

        // NOTA: Si tu método setContrasena() no recibe parámetros en tu clase actual,
        // esta línea fallará. Corrígelo en Operador.java a: public void setContrasena(String contrasena)
        operador.setContrasena("operadorSeguro2026");

        // Verificamos que los cambios se hayan guardado con éxito
        assertEquals("Diana Marcela", operador.getNombre());
        assertEquals(998877, operador.getId());
        assertEquals("OPE-777", operador.getCodigo());
        assertEquals(Roll.OPERADOR, operador.getRoll());
        assertEquals("operadorSeguro2026", operador.getContrasena());
    }

    @Test
    void testNormalizar() {
        Operador operador = new Operador("Test", 1, "T-1", Roll.OPERADOR, "abc");

        // Comprobamos el comportamiento de normalizar (pasa a minúsculas y remueve tildes)
        String resultado = operador.normalizar("CÁNDIDO ÉXITO");

        assertEquals("candido exito", resultado);
    }

    @Test
    void testToString() {
        Operador operador = new Operador("Mateo Restrepo", 1122, "OPE-005", Roll.OPERADOR, "pass123");

        // El formato esperado une el toString de Persona con el código único del operador
        String esperado = "Nombre: Mateo Restrepo Identificación: 1122 Codigo: OPE-005";

        assertEquals(esperado, operador.toString());
    }
}