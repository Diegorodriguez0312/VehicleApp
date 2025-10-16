Proyecto #2
Gestión de Vehículos — Aplicación CRUD con Jakarta EE, Servlets + EJB Stateless + JDBC y vistas JSP.
Repositorio: En GitHub  | 3 integrantes

Índice:
1. Resumen
2. Arquitectura y componentes
3. Convenciones de nombres
4. Reglas de negocio (resumen)
5. Pre-requisitos
6. Configuración DB y DataSource
7. Cómo compilar y desplegar (NetBeans + GlassFish)
8. Flujo Git recomendado (branches y MR)

#  Proyecto: Gestión de Vehículos — Aplicación CRUD con Jakarta EE

**Repositorio:** Proyecto académico colaborativo (3 integrantes)  
**Tecnologías:** Jakarta EE 10 (Servlets + EJB Stateless + JDBC), JSP, MySQL, GlassFish 7.0.11  
**Integrantes:**  
- Wilson Fernando Gelves Salazar  
- Diego alexander rodriguez jaimes  
- ivan david sanchez garcia 

---

## 1. Resumen

Aplicación web para **registrar, listar, actualizar y eliminar vehículos** en un taller mecánico universitario.  
Implementa un CRUD completo con conexión a base de datos MySQL, arquitectura multicapa (Controller, DAO, Facade, Servlet) y vistas dinámicas en JSP.

---

## 2. Arquitectura y Componentes

Arquitectura basada en **capas lógicas** bajo el modelo MVC distribuido:

- **Controladores (`com.garaje.controller`)**  
  Gestionan la comunicación entre las vistas y la capa de negocio.

- **Servicios / EJB (`com.garaje.facade`)**  
  Contienen la lógica de negocio en *beans* Stateless.  
  Ejemplo: `VehiculoFacade.java`

- **Modelo (`com.garaje.model`)**  
  Define las entidades del sistema (ej. `Vehiculo.java`).

- **Persistencia (`com.garaje.persistence`)**  
  Gestiona operaciones JDBC directas con la base de datos.  
  Ejemplo: `VehiculoDAO.java`

- **Servlets (`com.garaje.servlet`)**  
  Controlan el flujo web, reenvían peticiones a JSP.  
  Ejemplo: `VehicleServlet.java`

- **Vistas JSP (`/WEB-INF/vehicles.jsp`)**  
  Interfaz web para la interacción con el usuario.

---

## 3. Convenciones de Nombres

- Paquetes: `com.garaje.[capa]`  
- Clases: PascalCase (`VehiculoDAO`, `VehiculoFacade`)  
- Métodos: camelCase (`registrarVehiculo`, `buscarPorPlaca`)  
- JSP: nombres en minúsculas (`vehicles.jsp`)  
- Variables: nombres descriptivos en minúsculas (`vehiculo`, `listaVehiculos`)  

---

## 4. Reglas de Negocio (Resumen)

1. No se permite agregar un vehículo con **placa duplicada**.  
2. Propietario no puede estar vacío ni tener menos de 5 caracteres.  
3. Marca, modelo y placa deben tener al menos 3 caracteres.  
4. El color solo acepta valores predefinidos: *Rojo, Blanco, Negro, Azul, Gris*.  
5. No se aceptan vehículos con antigüedad mayor a 20 años.  
6. Las placas son **únicas** en toda la base.  
7. No se puede eliminar un vehículo si el propietario es “Administrador”.  
8. Solo se puede actualizar un vehículo existente.  
9. Validar campos para evitar SQL Injection (simulado).  
10. Si la marca es “Ferrari”, se genera una notificación simulada.

---

## 5. Pre-requisitos

- **JDK:** 21 o superior  
- **Servidor de aplicaciones:** GlassFish 7.0.11  
- **IDE recomendado:** Apache NetBeans 20+  
- **Base de datos:** MySQL 8.0  
- **Driver JDBC:** `mysql-connector-j-8.x.jar`  

---

## 6. Configuración de la Base de Datos y DataSource

**Base de datos:** `garage`  

``sql
CREATE DATABASE garage;
USE garage;

CREATE TABLE vehiculo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  placa VARCHAR(10) UNIQUE NOT NULL,
  marca VARCHAR(50) NOT NULL,
  modelo INT NOT NULL,
  color VARCHAR(20),
  propietario VARCHAR(100) NOT NULL
);

## 7. Configuración del DataSource en GlassFish:

Nombre JNDI: jdbc/garage

Driver: com.mysql.cj.jdbc.Driver
URL: jdbc:mysql://localhost:3306/garage
Usuario: root
Contraseña:“root123”

 Cómo Compilar y Desplegar (NetBeans + GlassFish)
Clonar el repositorio:
git clone https://github.com/Diegorodriguez0312/VehicleApp.git
Abrir el proyecto en NetBeans.
•	Configurar el servidor GlassFish 7.0.11.
•	Configurar el DataSource jdbc/garage en GlassFish.
•	Limpiar y compilar el proyecto (Clean and Build).
•	Desplegar (Run Project).
•	Acceder a:
•	http://localhost:8080/Taller-Garaje/

8.  Flujo Git Recomendado (Branches y Merge Requests)

Flujo de trabajo basado en Git Feature Branch Workflow:
•	main: Rama principal estable.
•	develop: Rama de integración.
•	feature/: Desarrollo de funcionalidades nuevas.
•	Ejemplo: feature/registro-vehiculo
•	fix/: Corrección de errores.
•	Ejemplo: fix/validacion-placa
merge requests (MR):
•	Un MR por funcionalidad.
•	Revisión por al menos otro integrante antes de hacer merge a develop.



Reglas básicas:
•	Commits pequeños y descriptivos.
•	No subir código directamente a main.

📌 Autoría y uso académico:
Wilson Fernando Gelves Salazar |wgelves@uts.edu.co
Diego alexander rodriguez jaimes  | diegoalexanderrodriguez@uts.edu.co
Ivan david sanchez gracia.  | idavidsanchez@uts.edu.co

