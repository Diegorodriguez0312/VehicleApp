<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Vehículos - Taller Garaje</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                padding: 20px;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                background: rgba(255, 255, 255, 0.95);
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }

            .header {
                background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
                color: white;
                padding: 30px;
                text-align: center;
            }

            .header h1 {
                font-size: 2.5em;
                margin-bottom: 10px;
                font-weight: 300;
            }

            .header p {
                font-size: 1.1em;
                opacity: 0.9;
            }

            .content {
                padding: 40px;
            }

            .form-section {
                background: #f8f9fa;
                border-radius: 15px;
                padding: 30px;
                margin-bottom: 40px;
                border-left: 5px solid #3498db;
            }

            .form-section h2 {
                color: #2c3e50;
                margin-bottom: 25px;
                font-size: 1.8em;
                font-weight: 500;
            }

            .form-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                margin-bottom: 25px;
            }

            .form-group {
                display: flex;
                flex-direction: column;
            }

            .form-group label {
                color: #555;
                margin-bottom: 8px;
                font-weight: 500;
                font-size: 0.95em;
            }

            .form-group input {
                padding: 12px 15px;
                border: 2px solid #e1e8ed;
                border-radius: 8px;
                font-size: 1em;
                transition: all 0.3s ease;
                background: white;
            }

            .form-group input:focus {
                outline: none;
                border-color: #3498db;
                box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
            }

            .btn {
                background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
                color: white;
                padding: 12px 30px;
                border: none;
                border-radius: 8px;
                font-size: 1em;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
            }

            .table-section {
                margin-top: 40px;
            }

            .table-section h2 {
                color: #2c3e50;
                margin-bottom: 25px;
                font-size: 1.8em;
                font-weight: 500;
            }

            .table-container {
                background: white;
                border-radius: 15px;
                overflow: hidden;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th {
                background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
                color: white;
                padding: 20px 15px;
                text-align: left;
                font-weight: 500;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                font-size: 0.9em;
            }

            td {
                padding: 18px 15px;
                border-bottom: 1px solid #f1f3f4;
                transition: background-color 0.3s ease;
            }

            tr:hover td {
                background-color: #f8f9fa;
            }

            tr:nth-child(even) {
                background-color: #fafbfc;
            }

            .empty-state {
                text-align: center;
                padding: 60px 20px;
                color: #7f8c8d;
            }

            .empty-state h3 {
                font-size: 1.5em;
                margin-bottom: 15px;
                color: #95a5a6;
            }

            .empty-state p {
                font-size: 1.1em;
            }

            .vehicle-id {
                color: #3498db;
                font-weight: 600;
            }

            .vehicle-brand {
                color: #2c3e50;
                font-weight: 500;
            }

            .vehicle-model {
                color: #34495e;
            }

            .vehicle-year {
                color: #7f8c8d;
                font-weight: 500;
            }


            .vehicle-color {
                color: #7f8c8d;
                font-weight: 500;
            }


            .vehicle-owner {
                color: #27ae60;
                font-weight: 500;
            }

            #color {
                padding: 12px 15px;
                border: 2px solid #e1e8ed;
                border-radius: 8px;
                font-size: 1em;
                background: white;
                appearance: none;
                transition: all 0.3s ease;
                cursor: pointer;
            }

            #color:focus {
                outline: none;
                border-color: #3498db;
                box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
            }

            #color option {
                padding: 10px;
            }

            @media (max-width: 768px) {
                .container {
                    margin: 10px;
                    border-radius: 15px;
                }

                .content {
                    padding: 20px;
                }

                .form-grid {
                    grid-template-columns: 1fr;
                }

                .header h1 {
                    font-size: 2em;
                }

                table {
                    font-size: 0.9em;
                }

                th, td {
                    padding: 12px 8px;
                }
            }

            @media (max-width: 480px) {
                body {
                    padding: 10px;
                }

                .header {
                    padding: 20px;
                }

                .header h1 {
                    font-size: 1.8em;
                }

                th, td {
                    padding: 10px 6px;
                    font-size: 0.85em;
                }
            }
        </style>
    </head>
    <body>
        
        <c:if test="${not empty mensaje}">
    <div style="background-color: #d4edda; color: #155724; padding: 10px; margin: 15px; border-radius: 8px;">
        ${mensaje}
    </div>
</c:if>

<c:if test="${not empty error}">
    <div style="background-color: #f8d7da; color: #721c24; padding: 10px; margin: 15px; border-radius: 8px;">
        ${error}
    </div>
</c:if>
        
<c:out value="${error}" />
        
        <div class="container">
            <div class="header">
                <h1>🏗️ Taller Garaje</h1>
                <p>Gestión de Vehículos</p>
            </div>

            <div class="content">
                <div class="form-section">
                    <h2>➕ Agregar Nuevo Vehículo</h2>
                    <form action="vehicles" method="post">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="brand">Marca</label>
                                <input type="text" id="brand" name="marca" placeholder="Ej: Toyota, Ford, Honda..." required />
                            </div>
                            <div class="form-group">
                                <label for="model">Modelo</label>
                                <input type="text" id="model" name="modelo" placeholder="Ej: 2019, 2020, 2025" required />
                            </div>
                            <div class="form-group">
                                <label for="color">Color</label>
                                <select id="color" name="color" required>
                                    <option value="">Seleccione un color</option>
                                    <option value="Rojo">Rojo</option>
                                    <option value="Blanco">Blanco</option>
                                    <option value="Negro">Negro</option>
                                    <option value="Azul">Azul</option>
                                    <option value="Gris">Gris</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="year">placa</label>
                                <input type="text" id="placa" name="placa" placeholder="Num Placa" required />
                            </div>
                            <div class="form-group">
                                <label for="owner">Propietario</label>
                                <input type="text" id="owner" name="propietario" placeholder="Nombre del propietario..." required />
                            </div>
                        </div>
                        <button type="submit" class="btn">🚗 Agregar Vehículo</button>
                    </form>
                </div>

                <div class="table-section">
                    <h2>📋 Lista de Vehículos</h2>
                    <div class="table-container">
                        <c:choose>
                            <c:when test="${empty vehicles}">
                                <div class="empty-state">
                                    <h3>🚙 No hay vehículos registrados</h3>
                                    <p>Agrega tu primer vehículo usando el formulario de arriba</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Marca</th>
                                            <th>Modelo</th>
                                            <th>Color</th>
                                            <th>Placa</th>
                                            <th>Propietario</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="vehicle" items="${vehicles}">
                                            <tr>
                                                <td class="vehicle-id">#${vehicle.id}</td>
                                                <td class="vehicle-brand">${vehicle.marca}</td>
                                                <td class="vehicle-model">${vehicle.modelo}</td>
                                                <td class="vehicle-color">${vehicle.color}</td>
                                                <td class="vehicle-year">${vehicle.placa}</td>
                                                <td class="vehicle-owner">${vehicle.propietario}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
