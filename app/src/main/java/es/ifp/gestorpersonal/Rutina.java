package es.ifp.gestorpersonal;

import java.util.List;

public class Rutina {
    private String nombre;
    private int user_id;
    private List<Ejercicio> ejercicios;

    public Rutina(String nombre, int user_id, List<Ejercicio> ejercicios) {
        this.nombre = nombre;
        this.user_id = user_id;
        this.ejercicios = ejercicios;
    }

    // Getters y setters (opcionalmente, puedes generarlos si los necesitas)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }
}

