package com.garaje.servlet;

import com.garaje.facade.VehiculoFacade;
import com.garaje.model.Vehiculo;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/vehicles")
public class VehicleServlet extends HttpServlet {

    @EJB
    private VehiculoFacade vehiculoFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            request.setAttribute("vehicles", vehiculoFacade.listar());
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
        }

    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String marca = request.getParameter("marca");
    String modelo = request.getParameter("modelo");
    String color = request.getParameter("color");
    String placa = request.getParameter("placa");
    String propietario = request.getParameter("propietario");

    try {
        Vehiculo newVehiculo = new Vehiculo();
        newVehiculo.setMarca(marca);
        newVehiculo.setModelo(modelo);
        newVehiculo.setColor(color);
        newVehiculo.setPlaca(placa);
        newVehiculo.setPropietario(propietario);

        vehiculoFacade.agregar(newVehiculo);

        // ✅ Mensaje de éxito
        request.setAttribute("mensaje", "✅ Vehículo agregado correctamente");
        request.setAttribute("vehicles", vehiculoFacade.listar());
        request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

    } catch (Exception e) {
    e.printStackTrace(); // 👈 Esto muestra el error real en la consola de GlassFish
    request.setAttribute("error", "❌ Error: " + e.getMessage());
    try {
        request.setAttribute("vehicles", vehiculoFacade.listar());
    } catch (SQLException ex) {
        Logger.getLogger(VehicleServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
    request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
}

}

}
