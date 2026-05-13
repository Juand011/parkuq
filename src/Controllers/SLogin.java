package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Administrador;
import services.Parqueadero;

public class SLogin {

    @FXML
    private TextField nombreUsuarioField;

    @FXML
    private PasswordField contrasenaField;

    @FXML
    private TextField codigoAdminField;

    @FXML
    private Label mensajeLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button registrarButton;

    // Instancia para acceder a los datos
    private Parqueadero miParqueadero = new Parqueadero();

    @FXML
    public void initialize() {
        // Cargamos los datos del TXT al abrir la ventana
        miParqueadero.cargarDatosIniciales();
        System.out.println("✅ SLogin listo. Usuarios cargados: " + miParqueadero.getAdministradores().size());
    }

    @FXML
    private void handleLogin() {
        String usuario = nombreUsuarioField.getText();
        String password = contrasenaField.getText();
        String codigo = codigoAdminField.getText();

        if (usuario.isEmpty() || password.isEmpty() || codigo.isEmpty()) {
            mensajeLabel.setText("❌ Por favor, rellene todos los campos.");
            return;
        }

        // Buscamos si existe un administrador que coincida con usuario, contraseña y código
        boolean accesoConcedido = false;

        for (Administrador admin : miParqueadero.getAdministradores()) {
            // Comparamos los 3 campos clave
            if (admin.getNombre().equalsIgnoreCase(usuario) &&
                    admin.getContrasena().equals(password) &&
                    admin.getCodigo().equals(codigo)) {

                accesoConcedido = true;
                break;
            }
        }

        if (accesoConcedido) {
            mensajeLabel.setStyle("-fx-text-fill: green;");
            mensajeLabel.setText("✅ Acceso concedido. Entrando al sistema...");
            // Aquí llamarías a la siguiente ventana
        } else {
            mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeLabel.setText("❌ Datos incorrectos. Verifique usuario, clave y código.");
        }
    }

    @FXML
    private void handleIrARegistro() {
        // Como ya hay un admin, podrías mostrar un mensaje informativo
        mensajeLabel.setText("Ya existe un administrador registrado.");
    }
}