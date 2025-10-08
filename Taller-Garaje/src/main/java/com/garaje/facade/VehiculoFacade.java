package com.garaje.facade;

import com.garaje.model.Vehiculo;
import com.garaje.persistence.VehiculoDAO;
import com.garaje.exceptions.BusinessException;

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
     * Lista todos los vehículos.
     *
     * Manejo de excepciones:
     * - Lanza SQLException si hay problemas con la conexión/DAO.
     *
     * @return lista de Vehiculo (puede ser vacía)
     * @throws SQLException error en acceso a datos
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca vehículo por id.
     *
     * Manejo de excepciones:
     * - Lanza SQLException si hay problemas con la conexión/DAO.
     *
     * @param id identificador del vehículo
     * @return Vehiculo encontrado o null si no existe
     * @throws SQLException error en acceso a datos
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega vehículo. Debe validar con reglas de negocio antes de agregar.
     *
     * Reglas de negocio implementadas en este método:
     *  - No permitir agregar un vehículo con la placa duplicada (usa dao.existePlaca()).
     *  - No aceptar propietario vacío o con menos de 5 caracteres.
     *  - Marca, modelo y placa deben tener al menos 3 caracteres.
     *  - El color debe estar dentro de la lista predefinida (Rojo, Blanco, Negro, Azul, Gris).
     *  - No aceptar vehículos con más de 20 años de antigüedad (modelo < año actual - 20).
     *  - Validar patrones sospechosos de SQL Injection en los campos (validación simulada).
     *  - Al agregar un vehículo con marca "Ferrari", se simula una notificación.
     *
     * Excepciones:
     *  - Lanza BusinessException si alguna regla de negocio falla.
     *  - Lanza SQLException si ocurre un error en la capa DAO/DB.
     *
     * @param v vehículo a agregar
     * @throws SQLException si hay error en la base de datos
     * @throws BusinessException si falla alguna regla de negocio
     */
    public void agregar(Vehiculo v) throws SQLException, BusinessException {
        // DEBUG opcional
        System.out.println("DEBUG: Entrando a VehiculoFacade.agregar()");
        System.out.println("DEBUG: DataSource = " + ds);

        // Validaciones de negocio (lanzan BusinessException si fallan)
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
                throw new BusinessException("La placa " + v.getPlaca() + " ya existe en el sistema");
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
     *
     * Reglas de negocio implementadas:
     *  - Mismas validaciones que en agregar (campos mínimos, color válido, antigüedad,
     *    validación SQL simulated).
     *  - Solo actualizar si el vehículo existe (dao.buscarPorId()).
     *  - No permitir que la placa quede duplicada en otro vehículo.
     *
     * Excepciones:
     *  - Lanza BusinessException si falla una regla de negocio.
     *  - Lanza SQLException si hay error en la capa de persistencia.
     *
     * @param v vehículo con datos actualizados
     * @throws SQLException si hay error en la base de datos
     * @throws BusinessException si falla alguna regla de negocio
     */
    public void actualizar(Vehiculo v) throws SQLException, BusinessException {
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
                throw new BusinessException("El vehículo con ID " + v.getId() + " no existe");
            }

            // Verificar que la placa no esté duplicada en otro vehículo
            if (dao.existePlaca(v.getPlaca()) && !v.getPlaca().equals(vehiculoExistente.getPlaca())) {
                throw new BusinessException("La placa " + v.getPlaca() + " ya existe en otro vehículo");
            }

            dao.actualizar(v);
        }
    }

    /**
     * Elimina vehículo por id.
     *
     * Reglas de negocio implementadas:
     *  - El vehículo debe existir.
     *  - No se puede eliminar si el propietario es "Administrador".
     *
     * Excepciones:
     *  - Lanza BusinessException si se viola alguna regla.
     *  - Lanza SQLException si hay error en la capa de persistencia.
     *
     * @param id identificador del vehículo a eliminar
     * @throws SQLException si hay error en la base de datos
     * @throws BusinessException si falla alguna regla de negocio
     */
    public void eliminar(int id) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Verificar que el vehículo existe
            Vehiculo vehiculo = dao.buscarPorId(id);
            if (vehiculo == null) {
                throw new BusinessException("El vehículo con ID " + id + " no existe");
            }

            // No permitir eliminar si el propietario es "Administrador"
            if ("Administrador".equalsIgnoreCase(vehiculo.getPropietario())) {
                throw new BusinessException("No se puede eliminar un vehículo cuyo propietario es 'Administrador'");
            }

            dao.eliminar(id);
        }
    }

    // Métodos de validación privados
    // Todos ellos lanzan BusinessException cuando la validación falla.

    /**
     * Valida que los campos básicos no sean nulos o vacíos.
     *
     * @param v vehículo a validar
     * @throws BusinessException si falta algún campo obligatorio
     */
    private void validarCamposBasicos(Vehiculo v) throws BusinessException {
        if (v == null) {
            throw new BusinessException("El vehículo no puede ser nulo");
        }
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new BusinessException("La placa es obligatoria");
        }
        if (v.getMarca() == null || v.getMarca().trim().isEmpty()) {
            throw new BusinessException("La marca es obligatoria");
        }
        if (v.getModelo() == null || v.getModelo().trim().isEmpty()) {
            throw new BusinessException("El modelo es obligatorio");
        }
        if (v.getColor() == null || v.getColor().trim().isEmpty()) {
            throw new BusinessException("El color es obligatorio");
        }
        if (v.getPropietario() == null || v.getPropietario().trim().isEmpty()) {
            throw new BusinessException("El propietario es obligatorio");
        }
    }

    /**
     * Valida que el propietario tenga al menos 5 caracteres.
     *
     * @param propietario texto del propietario
     * @throws BusinessException si la longitud es menor a 5
     */
    private void validarPropietario(String propietario) throws BusinessException {
        if (propietario == null || propietario.trim().length() < 5) {
            throw new BusinessException("El propietario debe tener al menos 5 caracteres");
        }
    }

    /**
     * Valida que un campo tenga al menos la longitud mínima especificada.
     *
     * @param campo campo a validar
     * @param nombreCampo nombre lógico del campo (para mensaje)
     * @param longitudMinima longitud mínima permitida
     * @throws BusinessException si la longitud es menor a la mínima
     */
    private void validarLongitudMinima(String campo, String nombreCampo, int longitudMinima) throws BusinessException {
        if (campo == null || campo.trim().length() < longitudMinima) {
            throw new BusinessException("El " + nombreCampo + " debe tener al menos " + longitudMinima + " caracteres");
        }
    }

    /**
     * Valida que el color esté en la lista predefinida.
     *
     * @param color color a validar
     * @throws BusinessException si el color no está permitido
     */
    private void validarColor(String color) throws BusinessException {
        if (color == null || !COLORES_VALIDOS.contains(color)) {
            throw new BusinessException("El color debe ser uno de: " + COLORES_VALIDOS);
        }
    }

    /**
     * Valida que el modelo (año) no tenga más de 20 años de antigüedad.
     *
     * @param modelo texto con el año del modelo (se espera numérico)
     * @throws BusinessException si el formato no es numérico o si el vehículo es muy antiguo
     */
    private void validarAntiguedadModelo(String modelo) throws BusinessException {
        if (modelo == null) {
            throw new BusinessException("El modelo es obligatorio");
        }
        try {
            int añoModelo = Integer.parseInt(modelo);
            int añoActual = Calendar.getInstance().get(Calendar.YEAR);
            int añoLimite = añoActual - 20;

            if (añoModelo < añoLimite) {
                throw new BusinessException("El modelo no puede tener más de 20 años de antigüedad. Año mínimo permitido: " + añoLimite);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("El modelo debe ser un año válido (formato numérico)");
        }
    }

    /**
     * Simula validación contra SQL Injection buscando patrones sospechosos en los campos.
     *
     * @param v vehículo cuyos campos se revisan
     * @throws BusinessException si se detecta algún patrón sospechoso
     */
    private void validarSQLInjection(Vehiculo v) throws BusinessException {
        String[] camposSospechosos = {v.getPlaca(), v.getMarca(), v.getModelo(), v.getColor(), v.getPropietario()};
        String[] patronesSospechosos = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "exec", "execute", "union", "select", "insert", "update", "delete", "drop", "create", "alter"};

        for (String campo : camposSospechosos) {
            if (campo != null) {
                String campoLower = campo.toLowerCase();
                for (String patron : patronesSospechosos) {
                    if (campoLower.contains(patron.toLowerCase())) {
                        throw new BusinessException("El campo contiene caracteres o patrones sospechosos que podrían indicar SQL Injection");
                    }
                }
            }
        }
    }

    /**
     * Simula el envío de notificación para vehículos Ferrari.
     *
     * @param v vehículo agregado
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
