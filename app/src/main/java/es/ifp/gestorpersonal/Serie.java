package es.ifp.gestorpersonal;

import java.io.Serializable;

public class Serie implements Serializable {
    private int series;
    private double peso;
    private int repeticiones;
    private String nombreEjercicio;

    public Serie(int series, double peso, int repeticiones, String nombreEjercicio) {
        this.series = series;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.nombreEjercicio = nombreEjercicio;
    }

    // Getters y setters
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

