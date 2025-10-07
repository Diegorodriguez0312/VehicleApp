/**
 * Fachada que expone los métodos básicos.
 * Sólo incluye el paso directo (sin reglas), para que los estudiantes
 * implementen las reglas de negocio en esta clase.
 */
package com.garaje.facade;

import com.garaje.model.Vehiculo;
import com.garaje.persistence.VehiculoDAO;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Fachada para operaciones sobre vehículos. Deben agregarse reglas de negocio
 * antes de llamar al DAO.
 */
@Stateless
public class VehiculoFacade {

    @Resource(lookup = "jdbc/garageDB")  	
    private DataSource ds;
    
    // Lista predefinida de colores válidos
    private static final List<String> COLORES_VALIDOS = Arrays.asList(
        "Rojo", "Blanco", "Negro", "Azul", "Gris"
    );

    /**
     * Lista todos los vehículos. Debe documentar excepciones si se agregan
     * reglas.
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca vehículo por id. Manejar errores en llamada.
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega vehículo. Debe validar con reglas de negocio antes de agregar. Por
     * ejemplo, no agregar si la placa ya existe, si propietario está vacío,
     * etc.
     */
    public void agregar(Vehiculo v) throws SQLException, IllegalArgumentException {
        // Validaciones de negocio
           System.out.println("DEBUG: Entrando a VehiculoFacade.agregar()");
    System.out.println("DEBUG: DataSource = " + ds);
        validarCamposBasicos(v);
        validarPropietario(v.getPropietario());
        validarLongitudMinima(v.getMarca(), "marca", 3);
        validarLongitudMinima(v.getModelo(), "modelo", 3);
        validarLongitudMinima(v.getPlaca(), "placa", 3);
        validarColor(v.getColor());
        validarAntiguedadModelo(v.getModelo());
        validarSQLInjection(v);
        
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            
            // Verificar que la placa no esté duplicada
            if (dao.existePlaca(v.getPlaca())) {
                throw new IllegalArgumentException("La placa " + v.getPlaca() + " ya existe en el sistema");
            }
            
            dao.agregar(v);
            
            // Simular notificación para Ferrari
            if ("Ferrari".equalsIgnoreCase(v.getMarca())) {
                enviarNotificacionFerrari(v);
            }
        }
    }

    /**
     * Actualiza vehículo; incluir reglas de negocio.
     */
    public void actualizar(Vehiculo v) throws SQLException, IllegalArgumentException {
        // Validaciones de negocio
        validarCamposBasicos(v);
        validarPropietario(v.getPropietario());
        validarLongitudMinima(v.getMarca(), "marca", 3);
        validarLongitudMinima(v.getModelo(), "modelo", 3);
        validarLongitudMinima(v.getPlaca(), "placa", 3);
        validarColor(v.getColor());
        validarAntiguedadModelo(v.getModelo());
        validarSQLInjection(v);
        
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            
            // Verificar que el vehículo existe
            Vehiculo vehiculoExistente = dao.buscarPorId(v.getId());
            if (vehiculoExistente == null) {
                throw new IllegalArgumentException("El vehículo con ID " + v.getId() + " no existe");
            }
            
            // Verificar que la placa no esté duplicada en otro vehículo
            if (dao.existePlaca(v.getPlaca()) && !v.getPlaca().equals(vehiculoExistente.getPlaca())) {
                throw new IllegalArgumentException("La placa " + v.getPlaca() + " ya existe en otro vehículo");
            }
            
            dao.actualizar(v);
        }
    }

    /**
     * Elimina vehículo por id.
     */
    public void eliminar(int id) throws SQLException, IllegalArgumentException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            
            // Verificar que el vehículo existe
            Vehiculo vehiculo = dao.buscarPorId(id);
            if (vehiculo == null) {
                throw new IllegalArgumentException("El vehículo con ID " + id + " no existe");
            }
            
            // No permitir eliminar si el propietario es "Administrador"
            if ("Administrador".equalsIgnoreCase(vehiculo.getPropietario())) {
                throw new IllegalArgumentException("No se puede eliminar un vehículo cuyo propietario es 'Administrador'");
            }
            
            dao.eliminar(id);
        }
    }
    
    // Métodos de validación privados
    
    /**
     * Valida que los campos básicos no sean nulos o vacíos
     */
    private void validarCamposBasicos(Vehiculo v) {
        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }
        if (v.getMarca() == null || v.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("La marca es obligatoria");
        }
        if (v.getModelo() == null || v.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo es obligatorio");
        }
        if (v.getColor() == null || v.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("El color es obligatorio");
        }
        if (v.getPropietario() == null || v.getPropietario().trim().isEmpty()) {
            throw new IllegalArgumentException("El propietario es obligatorio");
        }
    }
    
    /**
     * Valida que el propietario tenga al menos 5 caracteres
     */
    private void validarPropietario(String propietario) {
        if (propietario.trim().length() < 5) {
            throw new IllegalArgumentException("El propietario debe tener al menos 5 caracteres");
        }
    }
    
    /**
     * Valida que un campo tenga al menos la longitud mínima especificada
     */
    private void validarLongitudMinima(String campo, String nombreCampo, int longitudMinima) {
        if (campo.trim().length() < longitudMinima) {
            throw new IllegalArgumentException("El " + nombreCampo + " debe tener al menos " + longitudMinima + " caracteres");
        }
    }
    
    /**
     * Valida que el color esté en la lista predefinida
     */
    private void validarColor(String color) {
        if (!COLORES_VALIDOS.contains(color)) {
            throw new IllegalArgumentException("El color debe ser uno de: " + COLORES_VALIDOS);
        }
    }
    
    /**
     * Valida que el modelo no tenga más de 20 años de antigüedad
     */
    private void validarAntiguedadModelo(String modelo) {
        try {
            int añoModelo = Integer.parseInt(modelo);
            int añoActual = Calendar.getInstance().get(Calendar.YEAR);
            int añoLimite = añoActual - 20;
            
            if (añoModelo < añoLimite) {
                throw new IllegalArgumentException("El modelo no puede tener más de 20 años de antigüedad. Año mínimo permitido: " + añoLimite);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El modelo debe ser un año válido (formato numérico)");
        }
    }
    
    /**
     * Simula validación contra SQL Injection
     */
    private void validarSQLInjection(Vehiculo v) {
        String[] camposSospechosos = {v.getPlaca(), v.getMarca(), v.getModelo(), v.getColor(), v.getPropietario()};
        String[] patronesSospechosos = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "exec", "execute", "union", "select", "insert", "update", "delete", "drop", "create", "alter"};
        
        for (String campo : camposSospechosos) {
            if (campo != null) {
                String campoLower = campo.toLowerCase();
                for (String patron : patronesSospechosos) {
                    if (campoLower.contains(patron.toLowerCase())) {
                        throw new IllegalArgumentException("El campo contiene caracteres o patrones sospechosos que podrían indicar SQL Injection");
                    }
                }
            }
        }
    }
    
    /**
     * Simula el envío de notificación para vehículos Ferrari
     */
    private void enviarNotificacionFerrari(Vehiculo v) {
        System.out.println("=== NOTIFICACIÓN FERRARI ===");
        System.out.println("Se ha registrado un nuevo vehículo Ferrari:");
        System.out.println("Placa: " + v.getPlaca());
        System.out.println("Modelo: " + v.getModelo());
        System.out.println("Color: " + v.getColor());
        System.out.println("Propietario: " + v.getPropietario());
        System.out.println("=============================");
        // En una implementación real, aquí se enviaría un email, SMS, etc.
    }
}
