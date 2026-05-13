package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.Parqueadero;
import java.net.URL;

public class Main extends Application {

    private Parqueadero miParqueadero = new Parqueadero();

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Intentamos cargar los administradores desde el archivo TXT
        miParqueadero.cargarDatosIniciales();

        String rutaFxml;
        String titulo;

        // 2. Lógica de las dos entradas corregida:
        if (miParqueadero.getAdministradores().isEmpty()) {
            // Si no hay nadie -> Login (que es tu registro inicial)
            rutaFxml = "/app/resources/Login.view.fxml";
            titulo = "Registro de Administrador - ParkuQ";
        } else {
            // Si ya hay alguien -> SLogin (segundo login)
            rutaFxml = "/app/resources/SLogin.view.fxml";
            titulo = "Inicio de Sesión - ParkuQ";
        }

        // 3. Carga del recurso con validación
        URL resource = getClass().getResource(rutaFxml);

        if (resource == null) {
            // Si falla, intentamos una ruta alternativa común en IntelliJ
            rutaFxml = rutaFxml.replace("/app/resources/", "/resources/");
            resource = getClass().getResource(rutaFxml);
        }

        if (resource == null) {
            throw new RuntimeException("No se encontró el archivo FXML. Revisa que el nombre sea exacto: " + rutaFxml);
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            System.out.println("✅ Iniciando en: " + rutaFxml);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar la escena:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}