package es.ifp.gestorpersonal;

import java.io.Serializable;

public class Serie implements Serializable {

    private int id; // Agregado el campo id
    private int series;
    private double peso;
    private int repeticiones;
    private String nombreEjercicio;

    // Constructor actualizado para aceptar el ID
    public Serie(int id, int series, double peso, int repeticiones, String nombreEjercicio) {
        this.id = id; // Establece el ID
        this.series = series;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.nombreEjercicio = nombreEjercicio;
    }

    // Getters y setters para el campo id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters y setters para los otros campos
    public String getNombreEjercicio() {
        return nombreEjercicio;
    }

    public void setNombreEjercicio(String nombreEjercicio) {
        this.nombreEjercicio = nombreEjercicio;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}
