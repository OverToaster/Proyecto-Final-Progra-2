package Utilidades;

/**
 * Centraliza los nombres de los archivos de texto usados como persistencia
 * por toda la capa AccesoDatos. Si se necesita mover los datos a otra
 * carpeta o renombrar un archivo, este es el único lugar que hay que tocar.
 */
public enum NombresArchivos {

    ID_CONTROL("id_control.txt"),
    USUARIOS("usuarios.txt"),
    EMPLEADOS("empleados.txt"),
    NOMINAS("nominas.txt"),
    DETALLE_NOMINA("detalle_nomina.txt");

    private final String nombreArchivo;

    NombresArchivos(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return this.nombreArchivo;
    }
}
