package Test;

import model.Administrador;
import org.junit.jupiter.api.Test;
import utilidades.Roll;

import static org.junit.jupiter.api.Assertions.*;

public class AdministradorTest {

    @Test
    void testConstructor() {
        // Se crea un administrador pasando los datos requeridos por la jerarquía y la clase
        Administrador admin = new Administrador("Carlos Perez", 98765, "ADM-001", Roll.ADMINISTRADOR, "clavedeprueba");

        // Validamos atributos heredados de Persona
        assertEquals("Carlos Perez", admin.getNombre());
        assertEquals(98765, admin.getId());

        // Validamos atributos propios de Administrador
        assertEquals("ADM-001", admin.getCodigo());
        assertEquals(Roll.ADMINISTRADOR.toString(), admin.getRoll());
        assertEquals("clavedeprueba", admin.getContrasena());
    }

    @Test
    void testSettersAndGetters() {
        Administrador admin = new Administrador("Temporal", 0, "TEMP", Roll.OPERADOR, "123");

        // Modificamos todos los valores utilizando los métodos setter
        admin.setNombre("Andrés Lopez");
        admin.setId(112233);
        admin.setCodigo("ADM-999");
        admin.setRoll(Roll.ADMINISTRADOR);
        admin.setContrasena("nuevaClave456");

        // Comprobamos que los getters devuelvan los nuevos valores asignados
        assertEquals("Andrés Lopez", admin.getNombre());
        assertEquals(112233, admin.getId());
        assertEquals("ADM-999", admin.getCodigo());
        assertEquals(Roll.ADMINISTRADOR.toString(), admin.getRoll());
        assertEquals("nuevaClave456", admin.getContrasena());
    }

    @Test
    void testNormalizar() {
        Administrador admin = new Administrador("Mánager", 1, "MNG", Roll.ADMINISTRADOR, "pass");

        // El método normalizar de Administrador quita diacríticos (tildes) y pasa a minúsculas,
        // a diferencia de las otras clases, este NO elimina los espacios intermedios ya que no usa replaceAll("\\s+","")
        String resultado = admin.normalizar("Óscar Épico Ñandú");

        assertEquals("oscar epico ñandu", resultado);
    }

    @Test
    void testToString() {
        Administrador admin = new Administrador("Laura Gómez", 5544, "ADM-002", Roll.ADMINISTRADOR, "admin123");

        // El toString esperado combina el formato de Persona ("Nombre: ... Identificación: ...") con el código
        String esperado = "Nombre: Laura Gómez Identificación: 5544 Codigo: ADM-002";

        assertEquals(esperado, admin.toString());
    }
}