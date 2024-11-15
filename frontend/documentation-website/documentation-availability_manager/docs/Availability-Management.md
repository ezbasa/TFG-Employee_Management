# Aspectos Generales (BACK)
Este proyecto está desarrollado utilizando Spring Boot como framework para el backend, 
lo que facilita el desarrollo y la implementación de servicios eficientes. 
Para la gestión de datos, hemos optado por PostgreSQL como nuestra base de datos.

## Employee
Entidad encargada de instanciar la información necesaria para los empleados

### Modelado de base de datos
Datos necesarios para la creación de un empleado (Employee)

| **Propiedad**      | **Tipo** | **Descripción**                        |
|--------------------|----------|----------------------------------------|
| anumber            | String   | Identificador del registro             |
| name               | String   | Nombre                                 |
| team               | String   | Equipo donde se encuentra registrado   |
| location           | String   | Oficina asignada                       |
| holiday            | int      | Vacaciones disponibles                 |
| calendarItems      | List     | Relacion con la entidad CalendarItems. |


## CalendarITem
Entidad encargada de instanciar la información necesaria para los eventos de los empleados

### Modelado de base de datos
Datos necesarios para la creación de un evento (CalendarItem)

| **Propiedad** | **Tipo** | **Descripción**                          |
|---------------|----------|------------------------------------------|
| id            | Long     | Identificador del registro               |
| itemType      | String   | Tipo de ausencia                         |
| description   | String   | Resumen de ausencia                      |
| startDate     | Instant  | Fecha inicio                             |
| endDate       | Instant  | Fecha fin                                |
| itemActive    | boolean  | Muestra si el registro se encutra activo |
| employee      | boolean  | Relación con la entidad Employee         |



## Autenticación y autorización propia
La versión actual no contempla autenticación ni autorización, pero es necesaria una gestión por perfiles.

## Base de datos
Se utiliza una base de datos PostgreSQL, que se caracteriza por ser un sistema de gestión de bases de
datos relacional. La configuración de la conexión se realiza mediante las siguientes propiedades en el
archivo de configuración de Spring Boot:
### URL de conexión: 
La base de datos se conecta a través de la URL jdbc:postgresql://localhost:1521/postgres, donde
localhost es la dirección del servidor de la base de datos, 1521 es el puerto utilizado para las
conexiones, y postgres es el nombre de la base de datos a la que se accede.
### Credenciales:
Las credenciales de acceso a la base de datos son las siguientes:
<h6>nombre de ususario:</h6>postgres
<h6>contraseña:</h6> admin

Estas credenciales permiten la autenticación y autorización del acceso a la base de datos.

### Controlador:
El controlador JDBC utilizado para PostgreSQL está especificado como org.postgresql.Driver, que
permite la interacción entre la aplicación Spring Boot y la base de datos.

## Configuración Adicional
Para optimizar la gestión de la base de datos y el uso de Hibernate, se han añadido las siguientes
configuraciones adicionales:
### Dialect:
Se establece el dialecto de Hibernate a org.hibernate.dialect.PostgreSQLDialect, lo que permite a
Hibernate traducir correctamente las operaciones de la aplicación a las instrucciones SQL
específicas para PostgreSQL.
### Modo de creación de esquemas:
Se configura spring.jpa.hibernate.ddl-auto=update, lo que indica a Hibernate que actualice el
esquema de la base de datos según los cambios realizados en las entidades del modelo de datos.
Esto facilita el desarrollo al permitir que las modificaciones en el código se reflejen
automáticamente en la base de datos sin necesidad de realizar cambios manuales.


# Aspectos Generales (FRONT)
