package com.garaje.servlet;

import com.garaje.facade.VehiculoFacade;
import com.garaje.model.Vehiculo;
import com.garaje.exceptions.BusinessException;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet para manejar operaciones CRUD de vehículos. Aplica reglas de negocio
 * desde el Facade y gestiona mensajes amigables.
 */
@WebServlet("/vehicles")
public class VehicleServlet extends HttpServlet {

    @EJB
    private VehiculoFacade vehiculoFacade;

    /**
     * Maneja las solicitudes GET: listar o preparar edición.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Vehiculo vehiculo = vehiculoFacade.buscarPorId(id);

                if (vehiculo != null) {
                    request.setAttribute("vehiculoEditar", vehiculo);
                } else {
                    request.setAttribute("error", "No se encontró el vehículo con ID " + id);
                }
            }

            // Cargar lista completa siempre
            request.setAttribute("vehicles", vehiculoFacade.listar());
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

        } catch (Exception ex) {
            request.setAttribute("error", "Error al cargar la página: " + ex.getMessage());
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
        }
    }

    /**
     * Maneja las solicitudes POST: agregar, editar o eliminar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                // Eliminar vehículo
                int id = Integer.parseInt(request.getParameter("id"));
                vehiculoFacade.eliminar(id);
                request.setAttribute("mensaje", "🗑️ Vehículo eliminado correctamente.");

            } else if ("update".equals(action)) {
                // Actualizar vehículo existente
                int id = Integer.parseInt(request.getParameter("id"));
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(id);
                vehiculo.setMarca(request.getParameter("marca"));
                vehiculo.setModelo(request.getParameter("modelo"));
                vehiculo.setColor(request.getParameter("color"));
                vehiculo.setPlaca(request.getParameter("placa"));
                vehiculo.setPropietario(request.getParameter("propietario"));

                vehiculoFacade.actualizar(vehiculo);
                request.setAttribute("mensaje", "✏️ Vehículo actualizado correctamente.");

            } else {
                // Agregar nuevo vehículo 
                Vehiculo nuevoVehiculo = new Vehiculo();
                nuevoVehiculo.setMarca(request.getParameter("marca"));
                nuevoVehiculo.setModelo(request.getParameter("modelo"));
                nuevoVehiculo.setColor(request.getParameter("color"));
                nuevoVehiculo.setPlaca(request.getParameter("placa"));
                nuevoVehiculo.setPropietario(request.getParameter("propietario"));

                vehiculoFacade.agregar(nuevoVehiculo);
                // Si es Ferrari, mostrar mensaje especial
                if ("Ferrari".equalsIgnoreCase(nuevoVehiculo.getMarca())) {
                    request.setAttribute("mensaje", "🏎️ ¡Vehículo Ferrari agregado! Notificación enviada.");
                } else {
                    request.setAttribute("mensaje", "✅ Vehículo agregado correctamente.");
                }
            }

        } catch (BusinessException be) {
            request.setAttribute("error", "⚠️ " + be.getMessage());
        } catch (SQLException se) {
            request.setAttribute("error", "❌ Error en base de datos: " + se.getMessage());
            Logger.getLogger(VehicleServlet.class.getName()).log(Level.SEVERE, null, se);
        } catch (Exception e) {
            request.setAttribute("error", "❌ Error inesperado: " + e.getMessage());
            Logger.getLogger(VehicleServlet.class.getName()).log(Level.SEVERE, null, e);
        }

        // Recargar la lista actualizada
        try {
            request.setAttribute("vehicles", vehiculoFacade.listar());
        } catch (SQLException ex) {
            Logger.getLogger(VehicleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
    }
}
