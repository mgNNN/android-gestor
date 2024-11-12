package es.ifp.gestorpersonal;


import java.util.ArrayList;
import java.util.List;

public class Ejercicio {
    private int id;
    private List<Serie> series;

    public Ejercicio(int id) {
        this.id = id;
        this.series = new ArrayList<>();
    }

    public void addSerie(Serie serie) {
        series.add(serie);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }
}

