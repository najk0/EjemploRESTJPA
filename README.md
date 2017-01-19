# EjemploREST
Este ejemplo necesita que tengas instalado en tu repositorio local la biblioteca __GeneradorDatosINE__

Una vez descargado el proyecto, ábrelo en Intellij:

1. En un terminal ve al directorio **src/main/webapp** y escribe **npm install**. Esto descargará todas las dependencias necesarias para el cliente web: jquery y angularjs.
1. En la solapa **Maven projects** haz doble click sobre **jetty:run**, se levantará el servidor jetty y los servicios REST.
 
 Los servicios desplegados se encuentran en **http://localhost:8080/rest**
 
 Al cliente web se accede en **http://localhost:8080**
 
 Si quieres crear automáticamente una serie de entradas en la agenda haz un **POST** a la dirección **http://localhost:8080/rest/people/generate/random?quantity=10** donde **quantity** indica el número de entradas que se generarán. Después, refresca el navegador.
 
 Lo más interesante de este ejemplo los son **test**