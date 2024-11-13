package es.ifp.gestorpersonal;
import java.io.Serializable;

    public class Medicamento implements Serializable {
        private String nombre;
        private String dosis;
        private String dosisDia;
        private String duracionTratamiento;
        private String horaPrimeraDosis;
        private int id;

        // Constructor
        public Medicamento(String nombre, String dosis, String dosisDia, String duracionTratamiento, String horaPrimeraDosis, int id) {
            this.nombre = nombre;
            this.dosis = dosis;
            this.dosisDia = dosisDia;
            this.duracionTratamiento = duracionTratamiento;
            this.horaPrimeraDosis = horaPrimeraDosis;
            this.id = id;
        }

        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getDosis() { return dosis; }
        public void setDosis(String dosis) { this.dosis = dosis; }

        public String getDosisDia() { return dosisDia; }
        public void setDosisDia(String dosisDia) { this.dosisDia = dosisDia; }

        public String getDuracionTratamiento() { return duracionTratamiento; }
        public void setDuracionTratamiento(String duracionTratamiento) { this.duracionTratamiento = duracionTratamiento; }

        public String getHoraPrimeraDosis() { return horaPrimeraDosis; }
        public void setHoraPrimeraDosis(String horaPrimeraDosis) { this.horaPrimeraDosis = horaPrimeraDosis; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
    }

