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
import services.PagoServices;
import services.Parqueadero;
import utilidades.Estado;
import utilidades.EstadoVehiculo;
import utilidades.TipoUsuario;
import utilidades.TipoVehiculo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class OOperador {

    private Parqueadero parqueaderoService = new Parqueadero();
    private PagoServices pagoServices = new PagoServices();
    private Vehiculo vehiculoSeleccionadoSalida = null;

    private double valorFinalPorHoraCalculado = 0.0;
    private long horasCobradasCalculadas = 0;
    private double valorTotalNetoCalculado = 0.0;

    @FXML private TableView<Vehiculo> tablaVehiculosEstacionados;
    @FXML private TableColumn<Vehiculo, String> colPlaca;
    @FXML private TableColumn<Vehiculo, TipoVehiculo> colTipoVehiculo;
    @FXML private TableColumn<Vehiculo, String> colConductor;
    @FXML private TableColumn<Vehiculo, Espacio> colEspacioOcupado;
    @FXML private TableColumn<Vehiculo, LocalDateTime> colHoraIngreso;

    @FXML private Label descuentoLabel;
    @FXML private Label disponiblesLabel;
    @FXML private ComboBox<String> espacioAsignadoCombo;
    @FXML private TextField horaIngresoField;
    @FXML private TextField horaSalidaField;
    @FXML private TextField identificacionConductorField;
    @FXML private Label mensajeIngresoLabel;
    @FXML private Label mensajeSalidaLabel;
    @FXML private TextField nombreConductorField;
    @FXML private Label ocupadosLabel;
    @FXML private TextField placaIngresoField;
    @FXML private TextField placaSalidaField;
    @FXML private DatePicker reporteFechaPicker;
    @FXML private Label reporteIngresosLabel;
    @FXML private Label reporteTiempoPromedioLabel;
    @FXML private Label reporteTotalVehiculosLabel;
    @FXML private Label tarifaHoraLabel;
    @FXML private Label tiempoTotalLabel;
    @FXML private ComboBox<String> tipoVehiculoIngresoCombo;
    @FXML private Label totalEspaciosLabel;
    @FXML private Label totalPagarLabel;
    @FXML private Label usuarioLogueadoLabel;

    private ObservableList<Vehiculo> obsVehiculosEstacionados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        parqueaderoService.cargarDatosI();
        parqueaderoService.cargarDatosIniciales();
        pagoServices.setVehiculos(parqueaderoService.getVehiculos());

        if (parqueaderoService.getUsuarios() == null || parqueaderoService.getUsuarios().isEmpty()) {
            cargarUsuariosDesdeTxt();
        }

        parqueaderoService.getEspacios().clear();
        cargarEspaciosDesdeTxt();

        cargarVehiculosEstacionadosDesdeTxt();
        cargarHistoricoPagosDesdeTxt();

        configurarTablas();
        actualizarTablasYEstadisticas();

        // Autoseleccionar la fecha de hoy para inicializar los reportes visuales en caliente
        if (reporteFechaPicker != null && reporteFechaPicker.getValue() == null) {
            reporteFechaPicker.setValue(java.time.LocalDate.now());
            ejecutarCalculoReporteDiario();
        }

        tipoVehiculoIngresoCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filtrarEspaciosPorCategoria(newValue);
        });
    }

    private void cargarVehiculosEstacionadosDesdeTxt() {
        String ruta = "src/Controllers/VehiculosEstacionados.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return;

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            pagoServices.getVehiculos().clear();

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.contains("Placa Vehículo")) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 5) {
                    String placa = datos[0].trim().toUpperCase();
                    TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(datos[1].trim().toUpperCase());
                    String conductor = datos[2].trim();
                    String codigoCelda = datos[3].trim();
                    LocalDateTime horaIngreso = LocalDateTime.parse(datos[4].trim(), formatoHora);

                    TipoUsuario tipoU = TipoUsuario.INVITADO;
                    if (datos.length >= 6) {
                        tipoU = TipoUsuario.valueOf(datos[5].trim().toUpperCase());
                    } else {
                        for (Usuario u : parqueaderoService.getUsuarios()) {
                            if (u.getNombre().equalsIgnoreCase(conductor)) {
                                tipoU = u.getTipoUsuario();
                                break;
                            }
                        }
                    }

                    Vehiculo v = new Vehiculo(placa, tipoVehiculo, conductor, horaIngreso, tipoU);
                    v.setEstadoVehiculo(EstadoVehiculo.ADENTRO);

                    for (Espacio e : parqueaderoService.getEspacios()) {
                        if (e.getCodigo().equals(codigoCelda)) {
                            e.setEstado(Estado.OCUPADO);
                            e.setVehiculoAsignado(v);
                            v.setEspacioAsignado(e);
                            break;
                        }
                    }
                    pagoServices.getVehiculos().add(v);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error al recuperar VehiculosEstacionados.txt: " + e.getMessage());
        }
    }

    private void cargarHistoricoPagosDesdeTxt() {
        String ruta = "src/Controllers/HistoricoPagos.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return;

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        double tarifaCarro = 60.0;
        double tarifaMoto = 15.0;
        double tarifaBici = 10.0;

        try {
            double[] preciosMaestros = parqueaderoService.cargarTarifas();
            if (preciosMaestros != null && preciosMaestros.length >= 3) {
                if (preciosMaestros[0] > 0) tarifaCarro = preciosMaestros[0];
                if (preciosMaestros[1] > 0) tarifaMoto = preciosMaestros[1];
                if (preciosMaestros[2] > 0) tarifaBici = preciosMaestros[2];
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudieron obtener las tarifas dinámicas del servicio, usando tarifas de contingencia.");
        }

        double tarifaMasAlta = Math.max(tarifaCarro, Math.max(tarifaMoto, tarifaBici));

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            pagoServices.getPagos().clear();

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.contains("Placa")) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 5) {
                    String placa = datos[0].trim();
                    TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(datos[1].trim().toUpperCase());
                    double valorPagado = Double.parseDouble(datos[2].trim());
                    LocalDateTime fechaPago = LocalDateTime.parse(datos[3].trim(), formatoHora);
                    TipoUsuario tipoU = TipoUsuario.valueOf(datos[4].trim().toUpperCase());

                    if (valorPagado <= 0.0) {
                        if (tipoU == TipoUsuario.INVITADO) {
                            valorPagado = tarifaMasAlta * 1.20;
                        } else {
                            if (tipoVehiculo == TipoVehiculo.CARRO) valorPagado = tarifaCarro;
                            else if (tipoVehiculo == TipoVehiculo.MOTO) valorPagado = tarifaMoto;
                            else if (tipoVehiculo == TipoVehiculo.BICICLETA) valorPagado = tarifaBici;
                        }
                    }

                    Vehiculo vAux = new Vehiculo(placa, tipoVehiculo, "Histórico", fechaPago.minusHours(1), tipoU);
                    vAux.setEstadoVehiculo(EstadoVehiculo.FUERA);
                    vAux.setHoraSalida(fechaPago);

                    Tarifa tAux = new Tarifa(tipoVehiculo, valorPagado, 0);
                    Pago p = new Pago(vAux, tAux);
                    p.setFecha(fechaPago);
                    p.setValorPagar(valorPagado);

                    pagoServices.getPagos().add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error al recuperar HistoricoPagos.txt: " + e.getMessage());
        }
    }

    private void cargarUsuariosDesdeTxt() {
        String ruta = "src/Controllers/usuarios.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            ruta = "src/controllers/usuarios.txt";
            archivo = new File(ruta);
            if (!archivo.exists()) return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.contains("Nombre")) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 3) {
                    String nombre = datos[0].trim();
                    int id = Integer.parseInt(datos[1].trim());
                    TipoUsuario tipoUsuario = TipoUsuario.valueOf(datos[2].trim().toUpperCase());

                    Usuario user = new Usuario(nombre, id, tipoUsuario);
                    parqueaderoService.getUsuarios().add(user);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error analizando usuarios.txt: " + e.getMessage());
        }
    }

    private void cargarEspaciosDesdeTxt() {
        String ruta = "src/Controllers/espacios.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            ruta = "src/controllers/espacios.txt";
            archivo = new File(ruta);
            if (!archivo.exists()) return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.contains("Codigo")) continue;

                String[] datos = linea.split("\\|");
                if (datos.length >= 3) {
                    String codigo = datos[0].trim();
                    TipoVehiculo tipoEspacio = TipoVehiculo.valueOf(datos[1].trim().toUpperCase());
                    Estado estado = Estado.valueOf(datos[2].trim().toUpperCase());

                    Espacio esp = new Espacio(codigo, tipoEspacio, estado);
                    parqueaderoService.getEspacios().add(esp);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error analizando espacios.txt: " + e.getMessage());
        }
    }

    private void configurarTablas() {
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colTipoVehiculo.setCellValueFactory(new PropertyValueFactory<>("tipoVehiculo"));
        colConductor.setCellValueFactory(new PropertyValueFactory<>("nombreConductor"));
        colEspacioOcupado.setCellValueFactory(new PropertyValueFactory<>("espacioAsignado"));
        colHoraIngreso.setCellValueFactory(new PropertyValueFactory<>("horaIngreso"));
        tablaVehiculosEstacionados.setItems(obsVehiculosEstacionados);
    }

    private void actualizarTablasYEstadisticas() {
        ArrayList<Vehiculo> activos = pagoServices.getVehiculos().stream()
                .filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO)
                .collect(Collectors.toCollection(ArrayList::new));
        obsVehiculosEstacionados.setAll(activos);

        int total = parqueaderoService.getEspacios().size();
        long ocupados = parqueaderoService.getEspacios().stream().filter(e -> e.getEstado() == Estado.OCUPADO).count();
        long disponibles = total - ocupados;

        totalEspaciosLabel.setText(String.valueOf(total));
        ocupadosLabel.setText(String.valueOf(ocupados));
        disponiblesLabel.setText(String.valueOf(disponibles));

        filtrarEspaciosPorCategoria(tipoVehiculoIngresoCombo.getValue());
    }

    private void filtrarEspaciosPorCategoria(String tipoVehiculoSeleccionado) {
        if (tipoVehiculoSeleccionado == null) {
            espacioAsignadoCombo.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            TipoVehiculo filtroEnum = TipoVehiculo.valueOf(tipoVehiculoSeleccionado.toUpperCase().trim());

            ArrayList<String> codigosFiltrados = parqueaderoService.getEspacios().stream()
                    .filter(e -> e.getEstado() == Estado.DISPONIBLE)
                    .filter(e -> e.getTipoEspacio() == filtroEnum)
                    .map(Espacio::getCodigo)
                    .collect(Collectors.toCollection(ArrayList::new));

            espacioAsignadoCombo.setItems(FXCollections.observableArrayList(codigosFiltrados));

        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Tipo de vehículo inválido.");
            espacioAsignadoCombo.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    void handleRegistrarIngreso(ActionEvent event) {
        mensajeIngresoLabel.setText("");
        try {
            String placa = placaIngresoField.getText().trim().toUpperCase();
            String tipoString = tipoVehiculoIngresoCombo.getValue();
            String conductor = nombreConductorField.getText().trim();
            String idConductor = identificacionConductorField.getText().trim();
            String codEspacio = espacioAsignadoCombo.getValue();

            if (placa.isEmpty() || tipoString == null || conductor.isEmpty() || idConductor.isEmpty() || codEspacio == null) {
                mensajeIngresoLabel.setStyle("-fx-text-fill: #e74c3c;");
                mensajeIngresoLabel.setText("Error: Todos los campos son obligatorios.");
                return;
            }

            boolean placaExiste = pagoServices.getVehiculos().stream()
                    .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa) && v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO);

            if (placaExiste) {
                mensajeIngresoLabel.setStyle("-fx-text-fill: #e74c3c;");
                mensajeIngresoLabel.setText("❌ Error: La placa '" + placa + "' ya está registrada adentro.");
                return;
            }

            boolean conductorOcupado = pagoServices.getVehiculos().stream()
                    .anyMatch(v -> v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO && v.getNombreConductor().equalsIgnoreCase(conductor));

            if (conductorOcupado) {
                mensajeIngresoLabel.setStyle("-fx-text-fill: #e74c3c;");
                mensajeIngresoLabel.setText("❌ Error: El conductor ya posee un vehículo estacionado.");
                return;
            }

            Espacio espacioAsignar = null;
            for (Espacio e : parqueaderoService.getEspacios()) {
                if (e.getCodigo().equals(codEspacio)) {
                    espacioAsignar = e;
                    break;
                }
            }

            if (espacioAsignar == null || espacioAsignar.getEstado() != Estado.DISPONIBLE) {
                mensajeIngresoLabel.setStyle("-fx-text-fill: #e74c3c;");
                mensajeIngresoLabel.setText("Error: El espacio ya no está disponible.");
                return;
            }

            TipoUsuario tipoU = TipoUsuario.INVITADO;
            Optional<Usuario> usuarioEncontrado = parqueaderoService.getUsuarios().stream()
                    .filter(u -> String.valueOf(u.getId()).equals(idConductor))
                    .findFirst();

            if (usuarioEncontrado.isPresent()) {
                tipoU = usuarioEncontrado.get().getTipoUsuario();
            }

            TipoVehiculo tipoVehiculoEnum = TipoVehiculo.valueOf(tipoString.toUpperCase().trim());

            Vehiculo nuevoVehiculo = new Vehiculo(placa, tipoVehiculoEnum, conductor, LocalDateTime.now(), tipoU);
            nuevoVehiculo.setEstadoVehiculo(EstadoVehiculo.ADENTRO);

            espacioAsignar.setEstado(Estado.OCUPADO);
            espacioAsignar.setVehiculoAsignado(nuevoVehiculo);
            nuevoVehiculo.setEspacioAsignado(espacioAsignar);

            pagoServices.getVehiculos().add(nuevoVehiculo);
            parqueaderoService.guardarEspacios();

            guardarEnVehiculosEstacionados();

            mensajeIngresoLabel.setStyle("-fx-text-fill: #27ae60;");
            mensajeIngresoLabel.setText("✅ Ingresado correctamente como [" + tipoU + "] en celda " + codEspacio);
            limpiarCamposIngreso();
            actualizarTablasYEstadisticas();

        } catch (Exception e) {
            mensajeIngresoLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeIngresoLabel.setText("Error: " + e.getMessage());
        }
    }

    private void guardarEnVehiculosEstacionados() {
        String ruta = "src/Controllers/VehiculosEstacionados.txt";
        File archivo = new File(ruta);

        if (archivo.getParentFile() != null && !archivo.getParentFile().exists()) {
            archivo.getParentFile().mkdirs();
        }

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
            writer.write("Placa Vehículo|Tipo de Unidad|Nombre Conductor|Celda Asignada|Estampado Hora Ingreso|Tipo Usuario");
            writer.newLine();

            for (Vehiculo v : pagoServices.getVehiculos()) {
                if (v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO) {
                    String celda = (v.getEspacioAsignado() != null) ? v.getEspacioAsignado().getCodigo() : "N/A";
                    String linea = v.getPlaca() + "|" +
                            v.getTipoVehiculo() + "|" +
                            v.getNombreConductor() + "|" +
                            celda + "|" +
                            v.getHoraIngreso().format(formatoHora) + "|" +
                            v.getTipoUsuario();
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error en VehiculosEstacionados.txt: " + e.getMessage());
        }
    }

    private void registrarEnHistoricoPagos(Pago p) {
        String ruta = "src/Controllers/HistoricoPagos.txt";
        File archivo = new File(ruta);

        if (archivo.getParentFile() != null && !archivo.getParentFile().exists()) {
            archivo.getParentFile().mkdirs();
        }

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean existeCabecera = archivo.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            if (!existeCabecera) {
                writer.write("Placa Vehículo|Tipo Unidad|Valor Cobrado|Fecha Recaudo|Tipo Usuario");
                writer.newLine();
            }
            String linea = p.getVehiculo().getPlaca() + "|" +
                    p.getVehiculo().getTipoVehiculo() + "|" +
                    p.getValorPagar() + "|" +
                    p.getFecha().format(formatoHora) + "|" +
                    p.getVehiculo().getTipoUsuario();
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("❌ Error al escribir en HistoricoPagos.txt: " + e.getMessage());
        }
    }

    @FXML
    void handleBuscarVehiculoSalida(ActionEvent event) {
        mensajeSalidaLabel.setText("");
        String placa = placaSalidaField.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mensajeSalidaLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeSalidaLabel.setText("Por favor ingrese una placa.");
            return;
        }

        vehiculoSeleccionadoSalida = null;
        for (Vehiculo v : pagoServices.getVehiculos()) {
            if (v.getPlaca().equalsIgnoreCase(placa) && v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO) {
                vehiculoSeleccionadoSalida = v;
                break;
            }
        }

        if (vehiculoSeleccionadoSalida != null) {
            mensajeSalidaLabel.setStyle("-fx-text-fill: #27ae60;");
            mensajeSalidaLabel.setText("✅ Vehículo de tipo [" + vehiculoSeleccionadoSalida.getTipoUsuario() + "] encontrado.");
            horaSalidaField.setText(LocalTime.now().toString().substring(0, 5));

            double tarifaCarro = 60.0;
            double tarifaMoto = 15.0;
            double tarifaBici = 10.0;

            try {
                double[] preciosMaestros = parqueaderoService.cargarTarifas();
                if (preciosMaestros != null && preciosMaestros.length >= 3) {
                    if (preciosMaestros[0] > 0) tarifaCarro = preciosMaestros[0];
                    if (preciosMaestros[1] > 0) tarifaMoto = preciosMaestros[1];
                    if (preciosMaestros[2] > 0) tarifaBici = preciosMaestros[2];
                }
            } catch (Exception e) {
                // Failsafe
            }

            double tarifaMasAlta = Math.max(tarifaCarro, Math.max(tarifaMoto, tarifaBici));
            double precioBasePorHora = 0;
            TipoVehiculo enumTipo = vehiculoSeleccionadoSalida.getTipoVehiculo();

            if (enumTipo == TipoVehiculo.CARRO) precioBasePorHora = tarifaCarro;
            else if (enumTipo == TipoVehiculo.MOTO) precioBasePorHora = tarifaMoto;
            else if (enumTipo == TipoVehiculo.BICICLETA) precioBasePorHora = tarifaBici;

            Duration d = Duration.between(vehiculoSeleccionadoSalida.getHoraIngreso(), LocalDateTime.now());
            long minutosTotales = d.toMinutes();

            if (minutosTotales <= 0) minutosTotales = 1;
            horasCobradasCalculadas = (minutosTotales + 59) / 60;

            TipoUsuario userTipo = vehiculoSeleccionadoSalida.getTipoUsuario();

            if (userTipo == TipoUsuario.INVITADO) {
                valorFinalPorHoraCalculado = tarifaMasAlta * 1.20;
                descuentoLabel.setText("$ " + String.format("%.2f", tarifaMasAlta * 0.20) + " (Recargo 20%)");
            } else {
                valorFinalPorHoraCalculado = precioBasePorHora;
                descuentoLabel.setText("$ 0.00 (Tarifa Plana Institucional)");
            }

            valorTotalNetoCalculado = valorFinalPorHoraCalculado * horasCobradasCalculadas;

            tiempoTotalLabel.setText(minutosTotales + " min (" + horasCobradasCalculadas + "h)");
            tarifaHoraLabel.setText("$ " + String.format("%.2f", valorFinalPorHoraCalculado));
            totalPagarLabel.setText("$ " + String.format("%.2f", valorTotalNetoCalculado));

        } else {
            mensajeSalidaLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeSalidaLabel.setText("❌ El vehículo no está estacionado o ya se registró su salida.");
            limpiarDetallesCobro();
        }
    }

    @FXML
    void handleRegistrarSalida(ActionEvent event) {
        mensajeSalidaLabel.setText("");
        String placaIntroducida = placaSalidaField.getText().trim().toUpperCase();

        if (vehiculoSeleccionadoSalida == null && !placaIntroducida.isEmpty()) {
            for (Vehiculo v : pagoServices.getVehiculos()) {
                if (v.getPlaca().equalsIgnoreCase(placaIntroducida) && v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO) {
                    vehiculoSeleccionadoSalida = v;
                    break;
                }
            }
        }

        if (vehiculoSeleccionadoSalida == null) {
            mensajeSalidaLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeSalidaLabel.setText("❌ Error: Busque o digite una placa de vehículo válida adentro.");
            return;
        }

        try {
            LocalDateTime momentoSalida = LocalDateTime.now();
            vehiculoSeleccionadoSalida.setHoraSalida(momentoSalida);

            double tarifaCarro = 60.0;
            double tarifaMoto = 15.0;
            double tarifaBici = 10.0;

            try {
                double[] preciosMaestros = parqueaderoService.cargarTarifas();
                if (preciosMaestros != null && preciosMaestros.length >= 3) {
                    if (preciosMaestros[0] > 0) tarifaCarro = preciosMaestros[0];
                    if (preciosMaestros[1] > 0) tarifaMoto = preciosMaestros[1];
                    if (preciosMaestros[2] > 0) tarifaBici = preciosMaestros[2];
                }
            } catch (Exception e) {
                // Failsafe
            }

            double tarifaMasAlta = Math.max(tarifaCarro, Math.max(tarifaMoto, tarifaBici));
            double precioBasePorHora = 0;
            TipoVehiculo enumTipo = vehiculoSeleccionadoSalida.getTipoVehiculo();

            if (enumTipo == TipoVehiculo.CARRO) precioBasePorHora = tarifaCarro;
            else if (enumTipo == TipoVehiculo.MOTO) precioBasePorHora = tarifaMoto;
            else if (enumTipo == TipoVehiculo.BICICLETA) precioBasePorHora = tarifaBici;

            Duration d = Duration.between(vehiculoSeleccionadoSalida.getHoraIngreso(), momentoSalida);
            long minutosTotales = d.toMinutes();
            if (minutosTotales <= 0) minutosTotales = 1;

            long horasA_Cobrar = (minutosTotales + 59) / 60;
            double precioPorHoraFinal = (vehiculoSeleccionadoSalida.getTipoUsuario() == TipoUsuario.INVITADO)
                    ? (tarifaMasAlta * 1.20) : precioBasePorHora;

            double liquidacionFinalDefinitiva = precioPorHoraFinal * horasA_Cobrar;

            Tarifa tarifaAplicada = new Tarifa(enumTipo, precioPorHoraFinal, 0);
            Pago nuevoPago = new Pago(vehiculoSeleccionadoSalida, tarifaAplicada);

            nuevoPago.setFecha(momentoSalida);
            nuevoPago.setValorPagar(liquidacionFinalDefinitiva);

            // ACTUALIZACIÓN DE MEMORIA RAM EN CALIENTE
            if (!pagoServices.getPagos().contains(nuevoPago)) {
                pagoServices.getPagos().add(nuevoPago);
            }
            pagoServices.crearPago(nuevoPago);
            registrarEnHistoricoPagos(nuevoPago);

            mensajeSalidaLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            mensajeSalidaLabel.setText(String.format("✅ Salida Procesada: Cobrado $%.2f por %d hora(s).", liquidacionFinalDefinitiva, horasA_Cobrar));

            if (vehiculoSeleccionadoSalida.getEspacioAsignado() != null) {
                vehiculoSeleccionadoSalida.getEspacioAsignado().setEstado(Estado.DISPONIBLE);
                vehiculoSeleccionadoSalida.getEspacioAsignado().setVehiculoAsignado(null);
            }

            vehiculoSeleccionadoSalida.setEstadoVehiculo(EstadoVehiculo.FUERA);
            vehiculoSeleccionadoSalida.setEspacioAsignado(null);

            parqueaderoService.guardarEspacios();
            guardarEnVehiculosEstacionados();

            vehiculoSeleccionadoSalida = null;
            placaSalidaField.clear();

            // Refrescar paneles visuales y recalcular los 3 reportes automáticamente
            actualizarTablasYEstadisticas();
            ejecutarCalculoReporteDiario();
            ejecutarCalculoReporteMensual();
            ejecutarCalculoReporteAnual();

        } catch (Exception e) {
            mensajeSalidaLabel.setStyle("-fx-text-fill: #e74c3c;");
            mensajeSalidaLabel.setText("❌ Error de procesamiento: " + e.getMessage());
        }
    }

    /**
     * MOTORES DE CÁLCULO CENTRALIZADOS (Tiempo Real en Memoria)
     */
    private void ejecutarCalculoReporteDiario() {
        if (reporteFechaPicker != null && reporteFechaPicker.getValue() != null) {
            java.time.LocalDate fechaBuscada = reporteFechaPicker.getValue();

            double totalIngresosNetos = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().toLocalDate().equals(fechaBuscada))
                    .mapToDouble(Pago::getValorPagar)
                    .sum();

            long totalVehiculosDia = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().toLocalDate().equals(fechaBuscada))
                    .count();

            double promedioPermanencia = 0.0;
            try {
                promedioPermanencia = pagoServices.tiempoPromedioDePermanencia(fechaBuscada.atStartOfDay());
            } catch (Exception e) {
                // Evita rupturas por falta de muestras estadísticas temporales
            }

            reporteTotalVehiculosLabel.setText("Reporte Diario (" + fechaBuscada + ") - Vehículos procesados: " + totalVehiculosDia);
            reporteIngresosLabel.setText("Ingresos generados: $ " + String.format("%.2f", totalIngresosNetos));
            reporteTiempoPromedioLabel.setText("Tiempo promedio de estadía: " + String.format("%.2f", promedioPermanencia) + " horas");
        }
    }

    private void ejecutarCalculoReporteMensual() {
        if (reporteFechaPicker != null && reporteFechaPicker.getValue() != null) {
            int anioBuscado = reporteFechaPicker.getValue().getYear();
            int mesBuscado = reporteFechaPicker.getValue().getMonthValue();

            double totalIngresosNetos = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().getYear() == anioBuscado && p.getFecha().getMonthValue() == mesBuscado)
                    .mapToDouble(Pago::getValorPagar)
                    .sum();

            long totalVehiculosMes = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().getYear() == anioBuscado && p.getFecha().getMonthValue() == mesBuscado)
                    .count();

            reporteTotalVehiculosLabel.setText("Reporte Mensual (" + mesBuscado + " / " + anioBuscado + ") - Salidas totales: " + totalVehiculosMes);
            reporteIngresosLabel.setText("Ingresos generados: $ " + String.format("%.2f", totalIngresosNetos));
            reporteTiempoPromedioLabel.setText("Tiempo promedio de estadía: Calculado a nivel diario.");
        }
    }

    private void ejecutarCalculoReporteAnual() {
        if (reporteFechaPicker != null && reporteFechaPicker.getValue() != null) {
            int anioBuscado = reporteFechaPicker.getValue().getYear();

            double totalIngresosNetos = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().getYear() == anioBuscado)
                    .mapToDouble(Pago::getValorPagar)
                    .sum();

            long totalVehiculosAnio = pagoServices.getPagos().stream()
                    .filter(p -> p != null && p.getFecha() != null && p.getFecha().getYear() == anioBuscado)
                    .count();

            reporteTotalVehiculosLabel.setText("Reporte Anual (" + anioBuscado + ") - Salidas totales: " + totalVehiculosAnio);
            reporteIngresosLabel.setText("Ingresos generados: $ " + String.format("%.2f", totalIngresosNetos));
            reporteTiempoPromedioLabel.setText("Tiempo promedio de estadía: Calculado a nivel diario.");
        }
    }

    /**
     * CONTROLADORES DE EVENTOS DE BOTONES
     */
    @FXML
    void handleGenerarReporteDiario(ActionEvent event) {
        if (reporteFechaPicker.getValue() == null) {
            reporteTotalVehiculosLabel.setText("Seleccione una fecha de control.");
            return;
        }
        ejecutarCalculoReporteDiario();
    }

    @FXML
    void handleGenerarReporteMensual(ActionEvent event) {
        if (reporteFechaPicker.getValue() == null) {
            reporteTotalVehiculosLabel.setText("Seleccione una fecha para extraer el mes.");
            return;
        }
        ejecutarCalculoReporteMensual();
    }

    @FXML
    void handleGenerarReporteAnual(ActionEvent event) {
        if (reporteFechaPicker.getValue() == null) {
            reporteTotalVehiculosLabel.setText("Seleccione una fecha para extraer el año.");
            return;
        }
        ejecutarCalculoReporteAnual();
    }

    @FXML
    void handleActualizarVehiculos(ActionEvent event) {
        actualizarTablasYEstadisticas();
        ejecutarCalculoReporteDiario();
        ejecutarCalculoReporteMensual();
        ejecutarCalculoReporteAnual();
    }

    @FXML
    void handleCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/SLogin.view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) totalEspaciosLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al intentar retornar a la pantalla de Login: " + e.getMessage());
        }
    }

    private void limpiarCamposIngreso() {
        placaIngresoField.clear();
        nombreConductorField.clear();
        identificacionConductorField.clear();
        tipoVehiculoIngresoCombo.setValue(null);
        espacioAsignadoCombo.setValue(null);
    }

    private void limpiarDetallesCobro() {
        tiempoTotalLabel.setText("--");
        tarifaHoraLabel.setText("--");
        descuentoLabel.setText("--");
        totalPagarLabel.setText("$ --");
        valorFinalPorHoraCalculado = 0.0;
        horasCobradasCalculadas = 0;
        valorTotalNetoCalculado = 0.0;
    }

    public void setUsuarioLogueado(String nombreUsuario) {
        if (usuarioLogueadoLabel != null) {
            usuarioLogueadoLabel.setText("Operador: " + nombreUsuario);
        }
    }
}