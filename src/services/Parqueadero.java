package services;

import utilidades.*;
import model.*;
import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Parqueadero {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Vehiculo> vehiculos;
    private ArrayList<Espacio> espacios;
    private ArrayList<Operador> operadores;
    private ArrayList<Administrador> administradores;
    private ArrayList<Tarifa> tarifas;

    public Parqueadero() {
        this.usuarios = new ArrayList<>();
        this.vehiculos = new ArrayList<>();
        this.espacios = new ArrayList<>();
        this.operadores = new ArrayList<>();
        this.administradores = new ArrayList<>();
        this.tarifas = new ArrayList<>();
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public ArrayList<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(ArrayList<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public ArrayList<Espacio> getEspacios() {
        return espacios;
    }

    public void setEspacios(ArrayList<Espacio> espacios) {
        this.espacios = espacios;
    }

    public ArrayList<Operador> getOperadores() {
        return operadores;
    }

    public void setOperadores(ArrayList<Operador> operadores) {
        this.operadores = operadores;
    }

    public ArrayList<Administrador> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(ArrayList<Administrador> administradores) {
        this.administradores = administradores;
    }

    public ArrayList<Tarifa> getTarifas() {
        return tarifas;
    }

    public void setTarifas(ArrayList<Tarifa> tarifas) {
        this.tarifas = tarifas;
    }

    // ================ CRUD PARA USUARIO ================

    public Usuario buscarUsuario(int id) {
        for (Usuario s : usuarios) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public boolean crearUsuario(Usuario usuario) {
        if (buscarUsuario(usuario.getId()) == null){
            usuarios.add(usuario);
            return true;
        }
        return false;
    }

    public String listarUsuario() {
        if (usuarios.isEmpty()) {
            return "No hay usuarios registrados";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usuarios.size(); i++) {
            sb.append(i + 1).append(". ").append(usuarios.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarUsuario(int id) {
        if (buscarUsuario(id) == null) {
           return false;
        } else {
            return usuarios.removeIf(usuario -> usuario.getId() == id);
        }
    }

    public Usuario actualizarUsuario(Usuario s) {
        if (buscarUsuario(s.getId()) != null) {
            String nombre = JOptionPane.showInputDialog(null, "Ingrese el nuevo nombre del usuario");
            String idS = JOptionPane.showInputDialog(null, "Ingrese el nuevo id del usuario");
            TipoUsuario t = (TipoUsuario) JOptionPane.showInputDialog(null, "Tipos de usuario",
                    "Elija un tipo de usuario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    TipoUsuario.values(),
                    TipoUsuario.values()[0]);
            int idI = Integer.parseInt(idS);
            s.setNombre(nombre);
            s.setId(idI);
            s.setTipoUsuario(t);

        }
        return s;
    }

    // ================ CRUD PARA OPERADOR ================

    public Operador buscarOPerador(int id) {
        for (Operador o : operadores) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

    public boolean crearOperador(Operador operador) {
        if(buscarUsuario(operador.getId()) == null){
            operadores.add(operador);
            return true;
        }
        return false;
    }

    public String listarOperador() {
        if (operadores.isEmpty()) {
          return "Este operador ya existe";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operadores.size(); i++) {
         sb.append(i + 1).append(". ").append(operadores.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarOperador(int id) {

        if (buscarOPerador(id) == null) {
           return false;
        } else {
           return operadores.removeIf(operador -> operador.getId() == id);
        }
    }

    public Operador actualizarOperador(Operador o) {
        if (buscarOPerador(o.getId()) != null) {
            String nombre = JOptionPane.showInputDialog(null, "Ingrese el nuevo nombre del operador");
            String idS = JOptionPane.showInputDialog(null, "Ingrese el nuevo id del operador");
            String codigo = JOptionPane.showInputDialog(null, "Ingrese el nuevo código operador");
            int idI = Integer.parseInt(idS);

            o.setNombre(nombre);
            o.setId(idI);
            o.setCodigo(codigo);

        }
        return o;
    }

    // ================ CRUD PARA ADMINISTRADOR ================

    public Administrador buscarAdministrador(int id) {
        for (Administrador a : administradores) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    public boolean crearAdministrador(Administrador administrador) {
        if (buscarAdministrador(administrador.getId()) == null){
            administradores.add(administrador);
            return true;
        }
        return false;
    }

    public String listarAdministrador() {
        if (administradores.isEmpty()) {
            return "No hay administradores registrados";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < administradores.size(); i++) {
            sb.append(i + 1).append(". ").append(administradores.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarAdministrador(int id) {
        if (buscarAdministrador(id) == null) {
          return false;
        } else {
            return administradores.removeIf(administrador -> administrador.getId() == id);
        }
    }

    public Administrador actualizarAdministrador(Administrador administrador) {
        if (buscarAdministrador(administrador.getId()) != null) {
            String nombre = JOptionPane.showInputDialog(null, "Ingrese el nuevo nombre del administrador");
            String idS = JOptionPane.showInputDialog(null, "Ingrese el nuevo id del administrador");
            String codigo = JOptionPane.showInputDialog(null, "Ingrese el nuevo código administrador");
            int idI = Integer.parseInt(idS);

            administrador.setNombre(nombre);
            administrador.setId(idI);
            administrador.setCodigo(codigo);

        }
        return administrador;
    }

    // ================ CRUD PARA ESPACIO ================

    public Espacio buscarEspacio(String codigo) {
        for (Espacio e : espacios) {
            if (e.normalizar(codigo).equalsIgnoreCase(e.getCodigo())) {
                return e;
            }
        }
        return null;
    }

    public boolean crearEspacio(Espacio espacio) {
        if (buscarEspacio(espacio.getCodigo()) == null) {
            espacios.add(espacio);
            espacio.setEstado(Estado.DISPONIBLE);
            return true;
        }
        return false;
    }

    public String listarEspacio() {
        if (espacios.isEmpty()) {
            return "No hay espacios registrados";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacios.size(); i++) {
           sb.append(i + 1).append(". ").append(espacios.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarEspacio(String codigo) {
        if (buscarEspacio(codigo) == null) {
           return false;
        } else {
           return espacios.removeIf(e -> e.getCodigo().equalsIgnoreCase(e.normalizar(codigo)));
        }
    }

    public Espacio actualizarEspacio(Espacio espacio, Vehiculo vehiculo) {
        if (buscarEspacio(espacio.getCodigo()) == null) {
            return null;
        } else {
            String codigo = JOptionPane.showInputDialog(null, "Ingrese el nuevo codigo");
            TipoVehiculo tipo = (TipoVehiculo) JOptionPane.showInputDialog(null, "Tipos de espacio", "Elija un tipo de espacio",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    TipoVehiculo.values(),
                    TipoVehiculo.values()[0]);
            Estado estado = (Estado) JOptionPane.showInputDialog(null, "Estados", "Elija un estado", JOptionPane.QUESTION_MESSAGE,
                    null,
                    Estado.values(),
                    Estado.values()[0]);
            espacio.setCodigo(codigo);
            espacio.setTipoEspacio(tipo);
            espacio.setEstado(estado);
            espacio.setVehiculoAsignado(vehiculo);
        }
        return espacio;
    }

    //================ METODOS PROPIOS DE ESPACIO ================

    public void deshavilitarEspacio(Espacio espacio){
        if(buscarEspacio(espacio.getCodigo()) != null){
            espacio.setEstado(Estado.FUERA_DE_SERVICIO);
        }
    }

    public void habilitarEspacio(Espacio espacio){
        if(buscarEspacio(espacio.getCodigo()) != null && espacio.getEstado().equals(Estado.FUERA_DE_SERVICIO)){
            espacio.setEstado(Estado.DISPONIBLE);
        }
    }

    // ================ CRUD PARA VEHICULO ================

    public Vehiculo buscarVehiculo(String placa) {
        for (Vehiculo v : vehiculos) {
            // Validamos que coincida la placa Y QUE EL VEHÍCULO NO HAYA SALIDO
            if (v.normalizar(placa).equalsIgnoreCase(v.getPlaca()) && v.getEstadoVehiculo() == EstadoVehiculo.ADENTRO) {
                return v;
            }
        }
        return null; // Si no está adentro, retorna null
    }

    public boolean crearVehiculo(Vehiculo vehiculo) {

        Vehiculo vehiculoExistente = buscarVehiculo(vehiculo.getPlaca());

        if (vehiculoExistente == null) {

            asignarEspacio(vehiculo);

            if (vehiculo.getEspacioAsignado() != null) {
                vehiculo.getEspacioAsignado().setEstado(Estado.OCUPADO);
            }

            vehiculo.setEstadoVehiculo(EstadoVehiculo.ADENTRO);

            vehiculos.add(vehiculo);

            return true;

        } else if (vehiculoExistente.getEstadoVehiculo() == EstadoVehiculo.FUERA) {

            asignarEspacio(vehiculoExistente);

            if (vehiculoExistente.getEspacioAsignado() != null) {

                vehiculoExistente.getEspacioAsignado().setEstado(Estado.OCUPADO);

                vehiculoExistente.setEstadoVehiculo(EstadoVehiculo.ADENTRO);

                vehiculoExistente.setHoraIngreso(LocalDateTime.now());

                vehiculoExistente.setHoraSalida(null);

                return true;
            }
        }

        return false;
    }
    public boolean crearVehiculo(TipoVehiculo tipoVehiculo, Vehiculo vehiculo){
        if(tipoVehiculo.equals(TipoVehiculo.BICICLETA)){
            asignarEspacio(vehiculo);

            if(vehiculo.getEspacioAsignado() != null){
                vehiculo.getEspacioAsignado().setEstado(Estado.OCUPADO);
            }
            vehiculo.setEstadoVehiculo(EstadoVehiculo.ADENTRO);
            vehiculos.add(vehiculo);
            return true;
        }
        return false;
    }


    public String listarVehiculo() {
        if (vehiculos.isEmpty()) {
            return "No hay vehiculos aun";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vehiculos.size(); i++) {
            sb.append(i + 1).append(". ").append(vehiculos.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarVehiculo(String placa) {
        if (buscarVehiculo(placa) == null) {
           return false;
        } else {
           return vehiculos.removeIf(v -> v.getPlaca().equalsIgnoreCase(v.normalizar(placa)));
        }
    }

    public Vehiculo actualizarVehiculo(Vehiculo vehiculo, Espacio espacio) {
        if (buscarVehiculo(vehiculo.getPlaca()) == null) {
            return null;
        } else {
            String placa = JOptionPane.showInputDialog(null, "Ingrese la nueva placa del vehiculo");
            TipoVehiculo tipo = (TipoVehiculo) JOptionPane.showInputDialog(null, "Tipos de vehiculo", "Elija un tipo de vehiculo", JOptionPane.QUESTION_MESSAGE,
                    null,
                    TipoVehiculo.values(),
                    TipoVehiculo.values()[0]);
            String nombreConductor = JOptionPane.showInputDialog(null, "Ingrese el nuevo nombre del conductor");
            String ingreso = JOptionPane.showInputDialog(null, "digite la hora de ingreso en este formato HH:MM");
            LocalDateTime horaIngreso = LocalDateTime.parse(ingreso);
            String salida = JOptionPane.showInputDialog(null, "digite la hora de salida en este formato HH:MM");
            LocalDateTime horaSalida = LocalDateTime.parse(salida);
            EstadoVehiculo estado = (EstadoVehiculo) JOptionPane.showInputDialog(null, "Estados disponibles ", "Elija un estado", JOptionPane.QUESTION_MESSAGE,
                    null,
                    EstadoVehiculo.values(),
                    EstadoVehiculo.values()[0]);
            vehiculo.setPlaca(placa);
            vehiculo.setVehiculo(tipo);
            vehiculo.setNombreConductor(nombreConductor);
            vehiculo.setHoraIngreso(horaIngreso);
            vehiculo.setHoraSalida(horaSalida);
            vehiculo.setEspacioAsignado(espacio);
            vehiculo.setEstadoVehiculo(estado);

        }
        return vehiculo;
    }

    // ================ METODOS PROPIOS DE VEHICULO ================
    public Espacio asignarEspacio(Vehiculo v) {
        for(Espacio e: espacios){
            if(v.getVehiculo().equals(TipoVehiculo.CARRO) && e.getTipoEspacio().equals(TipoVehiculo.CARRO) && e.getEstado().equals(Estado.DISPONIBLE)){
                v.setEspacioAsignado(e);
                e.setVehiculoAsignado(v);
                return e;
            }else if(v.getVehiculo().equals(TipoVehiculo.MOTO) && e.getTipoEspacio().equals(TipoVehiculo.MOTO) && e.getEstado().equals(Estado.DISPONIBLE)){
                v.setEspacioAsignado(e);
                e.setVehiculoAsignado(v);
                return e;
            }else if(v.getVehiculo().equals(TipoVehiculo.BICICLETA) && e.getTipoEspacio().equals(TipoVehiculo.BICICLETA) && e.getEstado().equals(Estado.DISPONIBLE)) {
                v.setEspacioAsignado(e);
                e.setVehiculoAsignado(v);
                return e;
            }
        }
        return null;
    }


    // ================ CRUD PARA TARIFA ================

    public Tarifa buscarTarifa(TipoVehiculo tipoVehiculo) {
        for (Tarifa t : tarifas) {
            if (t.getTipoVehiculo().equals(tipoVehiculo)) {
                return t;
            }
        }
        return null;
    }

    public Tarifa crearTarifa(Tarifa tarifa) {
        if (buscarTarifa(tarifa.getTipoVehiculo()) == null) {
            tarifas.add(tarifa);
        }
        return null;
    }


    public String listarTarifa() {
        if (tarifas.isEmpty()) {
            return "No hay tarifas registradas aun";
        }
        StringBuilder sb =  new StringBuilder();
        for (int i = 0; i < tarifas.size(); i++) {
            sb.append(i + 1).append(". ").append(tarifas.get(i)).append("\n");
        }
        return sb.toString();
    }

    public boolean eliminarTarifa(Tarifa tarifa) {
        if (tarifa != null) {
            tarifas.remove(tarifa);
            return true;
        }
        return false;
    }

    public Tarifa actualizarTarifa(Tarifa tarifa) {
        TipoVehiculo tipo = (TipoVehiculo) JOptionPane.showInputDialog(null, "Elija un tipo de vehicilo", "Tipos de vehiculo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                TipoVehiculo.values(),
                TipoVehiculo.values()[0]);
        String valorHora = JOptionPane.showInputDialog(null, "Ingrese el valor por hora");
        String descuento = JOptionPane.showInputDialog(null, "Ingrese el valor del descuento");
        double valor = Double.parseDouble(valorHora);
        double descuento1 = Double.parseDouble(descuento);

        tarifa.setTipoVehiculo(tipo);
        tarifa.setValorHora(valor);
        tarifa.setDescuento(descuento1);

        return tarifa;
    }

    // ================ METODOS PROPIOS DE LOGIN ================
    public void cargarDatosIniciales() {
        String ruta = "Src/Controllers/administradores.txt";
        this.administradores.clear();

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");

                // Verificamos que tenga 5 partes: nombre|id|codigo|roll|contrasena
                if (partes.length >= 5) {
                    String nombre = partes[0];
                    int id = Integer.parseInt(partes[1]);
                    String codigo = partes[2];
                    // partes[3] es el Roll
                    String contrasena = partes[4];

                    administradores.add(new Administrador(nombre, id, codigo, Roll.ADMINISTRADOR, contrasena));
                }
            }
            System.out.println("✅ Cargados: " + administradores.size() + " administradores.");
        } catch (Exception e) {
            System.out.println("⚠️ Error o archivo vacío. Iniciando modo registro.");
        }
    }

    public boolean hayAdministradores() {
        String ruta = "E:\\INTELIGENT\\Av1\\parkuq\\src\\Controllers\\administradores.txt";
        File archivo = new File(ruta);

        if (!archivo.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            return br.readLine() != null; // Si hay al menos una línea, devuelve true
        } catch (IOException e) {
            return false;
        }
    }
    public void guardarUsuarios() {
        String ruta = "Src/Controllers/usuarios.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta, false))) { // false para sobrescribir con la lista actualizada
            for (Usuario u : getUsuarios()) {
                writer.write(u.getNombre() + "|" +
                        u.getId() + "|" +
                        u.getTipoUsuario());
                writer.newLine();
            }
            System.out.println("✅ Archivo usuarios.txt actualizado.");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar usuarios: " + e.getMessage());
        }

    }
    public void guardarOperadores() {
        String ruta = "src/Controllers/operadores.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta, false))) {
            for (Operador o : getOperadores()) {
                writer.write(o.getNombre() + "|" +
                        o.getId() + "|" +
                        o.getCodigo() + "|" +
                        o.getRoll() + "|" +
                        o.getContrasena());
                writer.newLine();
            }
            System.out.println("✅ Archivo operadores.txt actualizado.");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar operadores: " + e.getMessage());
        }
     }
    public void guardarEspacios() {
        String ruta = "src/Controllers/espacios.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta, false))) {
            for (Espacio e : getEspacios()) {
                writer.write(e.getCodigo() + "|" +
                        e.getTipoEspacio() + "|" +
                        e.getEstado());
                writer.newLine();
            }
            System.out.println("✅ Archivo espacios.txt actualizado.");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar espacios: " + e.getMessage());
        }
    }
    public void guardarTarifas(double carro, double moto, double bici) {
        String ruta = "src/Controllers/tarifas.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta, false))) {
            writer.write(carro + "|" + moto + "|" + bici);
            System.out.println("✅ Tarifas guardadas en archivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cargarDatosI() {
        cargarUsuarios();
        cargarOperadores();
        cargarEspacios();
    }

    private void cargarUsuarios() {
        String ruta = "E:\\INTELIGENT\\Av1\\parkuq\\src\\Controllers\\usuarios.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 3) {
                    usuarios.add(new Usuario(datos[0], Integer.parseInt(datos[1]), TipoUsuario.valueOf(datos[2])));
                }
            }
        } catch (IOException e) { System.err.println("Error cargando usuarios: " + e.getMessage()); }
    }

    private void cargarOperadores() {
        String ruta = "E:\\INTELIGENT\\Av1\\parkuq\\src\\Controllers\\operadores.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 5) {
                    // Nombre|ID|Codigo|Rol|Contrasena
                    operadores.add(new Operador(datos[0], Integer.parseInt(datos[1]), datos[2], Roll.valueOf(datos[3]), datos[4]));
                }
            }
        } catch (IOException e) { System.err.println("Error cargando operadores: " + e.getMessage()); }
    }

    private void cargarEspacios() {
        String ruta = "E:\\INTELIGENT\\Av1\\parkuq\\src\\Controllers\\espacios.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 3) {
                    // Codigo|TipoVehiculo|Estado
                    espacios.add(new Espacio(datos[0], TipoVehiculo.valueOf(datos[1]), Estado.valueOf(datos[2])));
                }
            }
        } catch (IOException e) { System.err.println("Error cargando espacios: " + e.getMessage()); }
    }

    public double[] cargarTarifas() {
        String ruta = "E:\\INTELIGENT\\Av1\\parkuq\\src\\Controllers\\tarifas.txt";
        File archivo = new File(ruta);
        if (!archivo.exists()) return new double[]{0, 0, 0}; // Valores por defecto

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();
            if (linea != null) {
                String[] datos = linea.split("\\|");
                return new double[]{
                        Double.parseDouble(datos[0]),
                        Double.parseDouble(datos[1]),
                        Double.parseDouble(datos[2])
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0, 0, 0};
    }
}
