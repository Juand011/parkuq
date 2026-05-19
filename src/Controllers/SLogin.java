package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Administrador;
import model.Operador;
import services.Parqueadero;
import utilidades.Roll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SLogin {

    @FXML private TextField nombreUsuarioField;
    @FXML private PasswordField contrasenaField;
    @FXML private TextField codigoAdminField;
    @FXML private Label mensajeLabel;

    private Parqueadero miParqueadero = new Parqueadero();

    @FXML
    public void initialize() {
        miParqueadero.cargarDatosIniciales();
    }

    @FXML
    private void handleLogin() {
        String usuario = nombreUsuarioField.getText().trim();
        String password = contrasenaField.getText().trim();
        String codigo = codigoAdminField.getText().trim();

        if (usuario.isEmpty() || password.isEmpty() || codigo.isEmpty()) {
            mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeLabel.setText("❌ Por favor, rellene todos los campos.");
            return;
        }

        // 1. INTENTAR AUTENTICAR COMO ADMINISTRADOR
        for (Administrador admin : miParqueadero.getAdministradores()) {
            if (admin.getNombre().equalsIgnoreCase(usuario) &&
                    admin.getContrasena().equals(password) &&
                    admin.getCodigo().equals(codigo)) {

                mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
                mensajeLabel.setText("✅ Acceso concedido como Administrador.");

                // Redirige al panel de administración
                irAPlataforma("/app/resources/ADMINISTRADOS.fxml", "Panel de Administración - ParkuQ", null);
                return;
            }
        }

        // 2. INTENTAR AUTENTICAR COMO OPERADOR
        ArrayList<Operador> operadoresRegistrados = cargarOperadoresDesdeArchivo();
        for (Operador oper : operadoresRegistrados) {
            if (oper.getNombre().equalsIgnoreCase(usuario) &&
                    oper.getContrasena().equals(password) &&
                    oper.getCodigo().equals(codigo)) {

                mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
                mensajeLabel.setText("✅ Acceso concedido como Operador.");

                // Redirige al panel de operador enviando el nombre del operador actual
                irAPlataforma("/app/resources/OPERADOR.fxml", "Panel de Operador - ParkuQ", oper.getNombre());
                return;
            }
        }

        // 3. SI NO COINCIDIÓ NINGUNO
        mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
        mensajeLabel.setText("❌ Datos incorrectos (Usuario, contraseña o código inválido).");
    }

    /**
     * Lee el archivo txt de operadores mapeando la estructura exacta de tu backend
     */
    private ArrayList<Operador> cargarOperadoresDesdeArchivo() {
        ArrayList<Operador> lista = new ArrayList<>();
        String ruta = "src/Controllers/operadores.txt";
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            System.err.println("⚠️ El archivo de operadores no existe en la ruta: " + archivo.getAbsolutePath());
            return lista;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 5) {
                    String nombre = datos[0].trim();
                    int id = Integer.parseInt(datos[1].trim());
                    String codigo = datos[2].trim();
                    Roll roll = Roll.valueOf(datos[3].trim().toUpperCase());
                    String contrasena = datos[4].trim();

                    Operador op = new Operador(nombre, id, codigo, roll, contrasena);
                    lista.add(op);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error al leer operadores en Login: " + e.getMessage());
        }
        return lista;
    }

    @FXML
    private void handleIrARegistro() {
        mensajeLabel.setStyle("-fx-text-fill: orange;");
        mensajeLabel.setText("Ya existe un administrador registrado.");
    }

    /**
     * Carga las vistas FXML dinámicamente y configura el contexto de sesión de forma segura
     */
    private void irAPlataforma(String rutaFxml, String tituloVentana, String nombreOperador) {
        try {
            URL resource = getClass().getResource(rutaFxml);

            // Mecanismos de contingencia si difieren nombres de archivos en rutas relativas
            if (resource == null && rutaFxml.contains("ADMINISTRADOS")) {
                resource = getClass().getResource("/app/resources/AAdministrador.view.fxml");
            }
            if (resource == null && rutaFxml.contains("OPERADOR")) {
                resource = getClass().getResource("/app/resources/OOperador.view.fxml");
            }

            if (resource == null) {
                System.err.println("❌ Error: No se localizó la interfaz en: " + rutaFxml);
                mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
                mensajeLabel.setText("❌ Error crítico: Vista de destino no encontrada.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());

            // LOGICA ESPECÍFICA SI ENTRA UN OPERADOR: Inyecta el usuario activo en OOperador
            if (nombreOperador != null) {
                Object controller = loader.getController();
                if (controller instanceof OOperador) {
                    OOperador oOperadorController = (OOperador) controller;
                    oOperadorController.setUsuarioLogueado(nombreOperador);
                }
            }

            Stage stage = (Stage) nombreUsuarioField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeLabel.setText("❌ Error al cargar la interfaz de control.");
        }
    }
}