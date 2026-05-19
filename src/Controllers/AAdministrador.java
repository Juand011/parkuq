package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import services.Parqueadero;
import utilidades.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class AAdministrador {

    private Parqueadero parqueaderoService = new Parqueadero();

    // FXML: Espacios
    @FXML private TextField codigoEspacioField, cantidadEspaciosField;
    @FXML private ComboBox<String> tipoEspacioCombo;
    @FXML private TableView<Espacio> tablaEspacios;
    @FXML private TableColumn<Espacio, String> colCodigoEspacio, colTipoEspacio, colEstadoEspacio;

    // FXML: Usuarios
    @FXML private TextField nombreUsuarioAutField, identificacionUsuarioAutField, buscarUsuarioAutField;
    @FXML private ComboBox<String> tipoUsuarioAutCombo;
    @FXML private TableView<Usuario> tablaUsuariosAutorizados;
    @FXML private TableColumn<Usuario, String> colNombreUsuarioAut, colTipoUsuarioAut;
    @FXML private TableColumn<Usuario, Integer> colIdentificacionUsuarioAut;

    // FXML: Operadores
    @FXML private TextField userAdminField, buscarOperadorField;
    @FXML private PasswordField codigoAdminField;
    @FXML private TableView<Operador> tablaOperadores;
    @FXML private TableColumn<Operador, String> colUserOperador;

    // FXML: Tarifas
    @FXML private TextField tarifaCarroField, tarifaMotoField, tarifaBiciField;

    @FXML private Label usuarioLogueadoLabel;

    private ObservableList<Espacio> obsEspacios = FXCollections.observableArrayList();
    private ObservableList<Usuario> obsUsuarios = FXCollections.observableArrayList();
    private ObservableList<Operador> obsOperadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Lee los archivos y llena las listas de Parqueadero
        parqueaderoService.cargarDatosI();
        parqueaderoService.cargarDatosIniciales();

        // 2. Configura las columnas de las tablas
        configurarTablas();

        // 3. Pasa los datos de las listas cargadas a la interfaz
        obsEspacios.setAll(parqueaderoService.getEspacios());
        obsUsuarios.setAll(parqueaderoService.getUsuarios());
        obsOperadores.setAll(parqueaderoService.getOperadores());

        double[] t = parqueaderoService.cargarTarifas();
        if (t != null && t.length >= 3) {
            tarifaCarroField.setText(String.valueOf(t[0]));
            tarifaMotoField.setText(String.valueOf(t[1]));
            tarifaBiciField.setText(String.valueOf(t[2]));
        }
    }

    private void configurarTablas() {
        colCodigoEspacio.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTipoEspacio.setCellValueFactory(new PropertyValueFactory<>("tipoEspacio"));
        colEstadoEspacio.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colNombreUsuarioAut.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIdentificacionUsuarioAut.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTipoUsuarioAut.setCellValueFactory(new PropertyValueFactory<>("tipoUsuario"));

        colUserOperador.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        tablaEspacios.setItems(obsEspacios);
        tablaUsuariosAutorizados.setItems(obsUsuarios);
        tablaOperadores.setItems(obsOperadores);
    }

    private void actualizarTablas() {
        obsEspacios.setAll(parqueaderoService.getEspacios());
        obsUsuarios.setAll(parqueaderoService.getUsuarios());
        obsOperadores.setAll(parqueaderoService.getOperadores());
    }

    // --- MANEJADORES DE EVENTOS (Handlers) ---

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/SLogin.view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) tablaEspacios.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleGuardarTarifas() {
        try {
            double carro = Double.parseDouble(tarifaCarroField.getText());
            double moto = Double.parseDouble(tarifaMotoField.getText());
            double bici = Double.parseDouble(tarifaBiciField.getText());

            parqueaderoService.guardarTarifas(carro, moto, bici);
            mostrarAlerta("Éxito", "Las tarifas se han guardado correctamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese valores numéricos válidos en las tarifas.");
        }
    }

    @FXML
    private void handleRegistrarEspacio() {
        try {
            String prefijo = codigoEspacioField.getText();
            int cant = Integer.parseInt(cantidadEspaciosField.getText());
            TipoVehiculo tipo = TipoVehiculo.valueOf(tipoEspacioCombo.getValue().toUpperCase());
            for (int i = 0; i < cant; i++) {
                String cod = prefijo + (parqueaderoService.getEspacios().size() + 1);
                parqueaderoService.crearEspacio(new Espacio(cod, tipo, Estado.DISPONIBLE));
            }
            parqueaderoService.guardarEspacios();
            actualizarTablas();
        } catch (Exception e) { mostrarAlerta("Error", "Datos de espacio incorrectos."); }
    }

    @FXML
    private void handleEliminarEspacio() {
        Espacio s = tablaEspacios.getSelectionModel().getSelectedItem();
        if (s != null) {
            parqueaderoService.eliminarEspacio(s.getCodigo());
            parqueaderoService.guardarEspacios();
            actualizarTablas();
        }
    }

    @FXML
    private void handlePonerEnMantenimiento() {
        Espacio s = tablaEspacios.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (s.getEstado() == Estado.FUERA_DE_SERVICIO) parqueaderoService.habilitarEspacio(s);
            else parqueaderoService.deshavilitarEspacio(s);
            parqueaderoService.guardarEspacios();
            tablaEspacios.refresh();
        }
    }

    @FXML
    private void handleRegistrarUsuarioAutorizado() {
        try {
            Usuario u = new Usuario(nombreUsuarioAutField.getText(), Integer.parseInt(identificacionUsuarioAutField.getText()), TipoUsuario.valueOf(tipoUsuarioAutCombo.getValue().toUpperCase()));
            if (parqueaderoService.crearUsuario(u)) {
                parqueaderoService.guardarUsuarios();
                actualizarTablas();
            }
        } catch (Exception e) { mostrarAlerta("Error", "Datos de usuario inválidos."); }
    }

    @FXML
    private void handleEliminarUsuario() {
        Usuario u = tablaUsuariosAutorizados.getSelectionModel().getSelectedItem();
        if (u != null) {
            parqueaderoService.eliminarUsuario(u.getId());
            parqueaderoService.guardarUsuarios();
            actualizarTablas();
        }
    }

    @FXML
    private void handleBuscarUsuario() {
        String q = buscarUsuarioAutField.getText().toLowerCase().trim();
        obsUsuarios.setAll(parqueaderoService.getUsuarios().stream()
                .filter(u -> u.getNombre().toLowerCase().contains(q) || String.valueOf(u.getId()).contains(q))
                .collect(Collectors.toList()));
        tablaUsuariosAutorizados.refresh();
    }

    @FXML
    private void handleRegistrarAdmin() {
        String nombreOperador = userAdminField.getText().trim();
        String contrasenaOperador = codigoAdminField.getText().trim();

        if (nombreOperador.isEmpty() || contrasenaOperador.isEmpty()) {
            mostrarAlerta("Error", "❌ Por favor complete todos los campos (Usuario y Contraseña).");
            return;
        }

        if (contrasenaOperador.length() < 6) {
            mostrarAlerta("Error", "❌ La contraseña del operador debe tener al menos 6 caracteres.");
            return;
        }

        int idUnico;
        boolean idExiste;
        do {
            idUnico = (int) (Math.random() * 10000) + 1;
            idExiste = verificarIdOperadorExistente(idUnico);
        } while (idExiste);

        String codigoUnico = "OP" + idUnico;

        try {
            Operador op = new Operador(nombreOperador, idUnico, codigoUnico, Roll.OPERADOR, contrasenaOperador);

            if (parqueaderoService.crearOperador(op)) {
                parqueaderoService.guardarOperadores();
                actualizarTablas();

                userAdminField.clear();
                codigoAdminField.clear();

                mostrarAlerta("Éxito", "✅ Operador registrado con éxito.\nCódigo asignado: " + codigoUnico);
            } else {
                mostrarAlerta("Error", "❌ El operador ya se encuentra registrado en el sistema.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en flujo de registro: " + e.getMessage());
            mostrarAlerta("Error", "Error interno al procesar el registro del operador.");
        }
    }

    private boolean verificarIdOperadorExistente(int id) {
        String ruta = "src/Controllers/operadores.txt";
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 2) {
                    try {
                        int idExistente = Integer.parseInt(datos[1]);
                        if (idExistente == id) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error al parsear ID de operador: " + datos[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al comprobar duplicados de operador: " + e.getMessage());
        }
        return false;
    }

    @FXML
    private void handleEliminarOperador() {
        Operador o = tablaOperadores.getSelectionModel().getSelectedItem();
        if (o != null) {
            parqueaderoService.eliminarOperador(o.getId());
            parqueaderoService.guardarOperadores();
            actualizarTablas();
        }
    }

    @FXML
    private void handleBuscarOperador() {
        String q = buscarOperadorField.getText().toLowerCase();
        obsOperadores.setAll(parqueaderoService.getOperadores().stream()
                .filter(o -> o.getNombre().toLowerCase().contains(q))
                .collect(Collectors.toList()));
    }

    /**
     * LÓGICA DE RESTABLECIMIENTO DE FÁBRICA EN LOS TXT PL ANOS
     */
    @FXML
    private void handleRestablecerDatosFabrica(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación Crítica");
        alert.setHeaderText("¿Restablecer el sistema por completo?");
        alert.setContentText("Esta acción borrará de forma irreversible espacios, históricos de pagos, usuarios registrados, operadores creados y tarifas. Los archivos volverán a su estado base de fábrica.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String carpetaBase = "src/Controllers/";
            try {
                // A. espacios.txt (Escribe estructura vacía)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "espacios.txt", false))) {
                    bw.write("Codigo|TipoEspacio|Estado");
                    bw.newLine();
                }
                // B. HistoricoPagos.txt (Escribe estructura vacía)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "HistoricoPagos.txt", false))) {
                    bw.write("Placa Vehículo|Tipo Unidad|Valor Cobrado|Fecha Recaudo|Tipo Usuario");
                    bw.newLine();
                }
                // C. VehiculosEstacionados.txt (Escribe estructura vacía)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "VehiculosEstacionados.txt", false))) {
                    bw.write("Placa Vehículo|Tipo de Unidad|Nombre Conductor|Celda Asignada|Estampado Hora Ingreso|Tipo Usuario");
                    bw.newLine();
                }
                // D. usuarios.txt (Escribe estructura vacía)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "usuarios.txt", false))) {
                    bw.write("Nombre|Identificacion|TipoUsuario");
                    bw.newLine();
                }
                // E. operadores.txt (Limpia por completo)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "operadores.txt", false))) {
                    // Vacío absoluto
                }
                // F. tarifas.txt (Valores por defecto a 0.0)
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(carpetaBase + "tarifas.txt", false))) {
                    bw.write("0.0|0.0|0.0");
                }

                // Sincronizar las listas de negocio en memoria RAM
                parqueaderoService.getEspacios().clear();
                parqueaderoService.getUsuarios().clear();
                parqueaderoService.getOperadores().clear();

                // Actualizar interfaz gráfica inmediatamente
                actualizarTablas();

                tarifaCarroField.setText("0.0");
                tarifaMotoField.setText("0.0");
                tarifaBiciField.setText("0.0");

                mostrarAlerta("Éxito", "¡El sistema ha sido restablecido exitosamente! Los archivos han sido formateados.");
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al intentar escribir en los archivos planos de almacenamiento: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m);
        a.showAndWait();
    }
}