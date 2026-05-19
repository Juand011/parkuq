package Controllers;

// IMPORTS CORRECTOS PARA JAVAFX (NO javax.swing)
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Administrador;
import utilidades.Roll;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PLogin {

    @FXML
    private Button cancelarButton;

    @FXML
    private TextField codigoAdminField;

    @FXML
    private Label codigoAyudaLabel;

    @FXML
    private PasswordField confirmarContrasenaField;

    @FXML
    private PasswordField contrasenaField;

    @FXML
    private Label mensajeLabel;

    @FXML
    private  TextField nombreUsuarioField;

    @FXML
    private Button registrarButton;
/// ///////////////////////HACER CONTRASEÑA EN ADMINISTRADOR /////////////
    // Este método se ejecuta automáticamente al cargar la ventana
    @FXML
    public void initialize() {
        System.out.println("Ventana de registro cargada");
        // Configuración inicial si es necesaria
        codigoAyudaLabel.setText("El código único es proporcionado por el sistema");
        mostrarUltimosRegistros();
    }

    // Método para el botón Registrarse
    private static String generarCodigoValido() {
        String nuevoCodigo;
        boolean codigoExiste;

        do {
            // Generar código aleatorio
            int numeroAleatorio = (int) (Math.random() * 10000) + 1;
            nuevoCodigo = "ADMIN" + numeroAleatorio;

            // Verificar si ya existe en el archivo
            codigoExiste = verificarCodigoExistente(nuevoCodigo);

            if (codigoExiste) {
                System.out.println("⚠️ Código " + nuevoCodigo + " ya existe, generando uno nuevo...");
            }

        } while (codigoExiste); // Repetir hasta encontrar un código único

        System.out.println("✅ Código único generado: " + nuevoCodigo);
        return nuevoCodigo;
    }

    // Método para verificar si un código ya existe en el archivo
    private static boolean verificarCodigoExistente(String codigo) {
        String ruta = "src/Controllers/administradores.txt";
        File archivo = new File(ruta);

        // Si el archivo no existe, no hay códigos que verificar
        if (!archivo.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // El formato es: nombre|id|codigo|roll
                String[] datos = linea.split("\\|");
                if (datos.length >= 3) {
                    String codigoExistente = datos[2]; // El código está en la posición 2
                    if (codigoExistente.equals(codigo)) {
                        return true; // Código ya existe
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo: " + e.getMessage());
        }

        return false; // Código no existe
    }
    private static final String CODIGO_VALIDO =  generarCodigoValido();


    private static int generarId() {
        int nuevoId;
        boolean idExiste;

        do {
            // Generar ID aleatorio
            nuevoId = (int) (Math.random() * 10000) + 1;

            // Verificar si ya existe en el archivo
            idExiste = verificarIdExistente(nuevoId);

            if (idExiste) {
                System.out.println("⚠️ ID " + nuevoId + " ya existe, generando uno nuevo...");
            }

        } while (idExiste); // Repetir hasta encontrar un ID único

        System.out.println("✅ ID único generado: "  + nuevoId);
        return nuevoId;
    }

    private static void mostrarUltimosRegistros() {/// Prueba leer ids ///
        String ruta = "src/Controllers/administradores.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            List<Integer> ids = new ArrayList<>();

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 2) {
                    try {
                        int id = Integer.parseInt(datos[1]);
                        ids.add(id);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error al parsear ID: " + datos[1]);
                    }
                }
            }

            System.out.println("\n=== ÚLTIMOS 5 IDs EN EL ARCHIVO ===");
            int start = Math.max(0, ids.size() - 5);
            for (int i = start; i < ids.size(); i++) {
                System.out.println((i+1) + ". ID: " + ids.get(i));
            }
            System.out.println("Total de IDs: " + ids.size());

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    // Método para verificar si un ID ya existe en el archivo
    private static boolean verificarIdExistente(int id) {
        String ruta = "src/Controllers/administradores.txt";
        File archivo = new File(ruta);

        // Si el archivo no existe, no hay IDs que verificar
        if (!archivo.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // El formato es: nombre|id|codigo|roll
                String[] datos = linea.split("\\|");
                if (datos.length >= 2) {
                    try {
                        int idExistente = Integer.parseInt(datos[1]); // El ID está en la posición 1
                        if (idExistente == id) {
                            return true; // ID ya existe
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error al parsear ID: " + datos[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo: " + e.getMessage());
        }

        return false; // ID no existe
    }

    private static final int ID_VALIDO = generarId();


    private static List<Administrador> listaAdministradores = new ArrayList<>();

    private static List<Administrador> ListaAdministradores() {
        return listaAdministradores;
    }
    private static void guardarAdministradoresEnArchivo(Administrador administrador) {
        // Al quitar las barras del inicio, Java empieza a buscar desde la raíz del proyecto
        String ruta = "src/Controllers/administradores.txt";

        // Creamos un objeto File para asegurarnos de que el directorio exista
        File archivo = new File(ruta);

        // Verificación de seguridad: si las carpetas no existen, las crea
        if (archivo.getParentFile() != null && !archivo.getParentFile().exists()) {
            archivo.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            // Concatenamos todo en una sola línea usando el separador "|"
            writer.write(administrador.getNombre() + "|" +
                    administrador.getId() + "|" +
                    administrador.getCodigo() + "|" +
                    administrador.getRoll() + "|" +
                    administrador.getContrasena());

            writer.newLine(); // Salto de línea para el siguiente registro

            System.out.println("✅ Administrador guardado exitosamente con contraseña.");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar en el archivo: " + e.getMessage());
        }
    }
    private  Administrador CrearAdministrador() {
        Administrador administrador = new Administrador(nombreUsuarioField.getText(), ID_VALIDO, CODIGO_VALIDO, Roll.ADMINISTRADOR, contrasenaField.getText());
        administrador.setNombre(nombreUsuarioField.getText());
        administrador.setCodigo(CODIGO_VALIDO);
        administrador.setId(ID_VALIDO);
        administrador.setRoll(Roll.ADMINISTRADOR);
        administrador.setContrasena(contrasenaField.getText());
        listaAdministradores.add(administrador);
        guardarAdministradoresEnArchivo(administrador);
        return administrador;
    }
    @FXML
    private void handleRegistrar() {
        // Obtener valores de los campos
        String nombreUsuario = nombreUsuarioField.getText();
        String contrasena = contrasenaField.getText();
        String confirmarContrasena = confirmarContrasenaField.getText();
        String codigoAdmin = codigoAdminField.getText();

        // Validaciones
        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            mensajeLabel.setText("❌ Complete todos los campos de contraseña y Usuario");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            mensajeLabel.setText("❌ Las contraseñas no coinciden");
            return;
        }

        if (contrasena.length() < 6) {
            mensajeLabel.setText("❌ La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!codigoAdmin.equals(CODIGO_VALIDO)) {
            mensajeLabel.setText("su Código único es = " + CODIGO_VALIDO);
            return;
        }

        // ✅ ✅ ✅ AQUÍ ESTÁ LO QUE TE FALTA - CREAR Y GUARDAR EL ADMINISTRADOR
        Administrador nuevoAdmin = CrearAdministrador();

        // Si todo está bien
        mensajeLabel.setStyle("-fx-text-fill: green;");
        mensajeLabel.setText("✅ Administrador registrado exitosamente");

        // Cerrar ventana después de 2 segundos
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    Stage stage = (Stage) registrarButton.getScene().getWindow();
                    stage.close();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para el botón Cancelar
    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}