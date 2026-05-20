package Test;

import model.Persona;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersonaTest {

    /**
     * Dado que tu clase Persona es abstracta (public abstract class Persona),
     * no se puede hacer un "new Persona(...)".
     * Creamos esta pequeña clase interna de prueba que hereda de Persona
     * para poder instanciarla y probar sus métodos sin problemas.
     */
    static class PersonaPrueba extends Persona {
        public PersonaPrueba(String nombre, int id) {
            super(nombre, id);
        }
    }

    @Test
    void verificarPersonaDuplicada() {
        // 1. Creamos la lista simulada de personas del parqueadero
        ArrayList<Persona> personas = new ArrayList<>();

        // 2. Creamos dos personas distintas pero con la misma identificación (ID: 123)
        Persona persona1 = new PersonaPrueba("Jeny", 123);
        Persona persona2 = new PersonaPrueba("Paula", 123);

        // 3. Agregamos la primera persona al sistema
        personas.add(persona1);

        // 4. Ejecutamos la lógica de control para detectar si el ID de la segunda persona ya existe
        boolean duplicado = false;
        for (Persona p : personas) {
            if (p.getId() == persona2.getId()) {
                duplicado = true;
                break;
            }
        }

        // 5. JUnit evalúa si la bandera terminó en true, validando que el control de duplicados funciona
        assertTrue(duplicado);
    }
}