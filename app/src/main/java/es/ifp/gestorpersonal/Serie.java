package es.ifp.gestorpersonal;

public class Serie {
    private int series;
    private double peso;
    private int repeticiones;

    public Serie(int series, double peso, int repeticiones) {
        this.series = series;
        this.peso = peso;
        this.repeticiones = repeticiones;
    }

    // Getters y setters
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

