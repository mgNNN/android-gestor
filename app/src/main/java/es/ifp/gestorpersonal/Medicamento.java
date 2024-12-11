package es.ifp.gestorpersonal;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Medicamento implements Serializable {
        private String nombre;
        private String dosis;
        private String dosisDia;
        private String duracionTratamiento;
        private String horaPrimeraDosis;
        private String siguienteDosis;
        private int id;
        private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Constructor
        public Medicamento(String nombre, String dosis, String dosisDia, String duracionTratamiento, String horaPrimeraDosis, int id) {
            this.nombre = nombre;
            this.dosis = dosis;
            this.dosisDia = dosisDia;
            this.duracionTratamiento = duracionTratamiento;
            this.horaPrimeraDosis = horaPrimeraDosis;
            this.id = id;
            this.siguienteDosis = horaPrimeraDosis; // CAMBIAR ESTO PARA QUE SEA LA SIGUIENTE DOSIS DE VERDAD
        }

    public String calcularSiguienteToma() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            Date primeraDosis = sdf.parse(horaPrimeraDosis);
            calendar.setTime(primeraDosis);

            int numeroDosis = Integer.parseInt(dosisDia);

            int totalSegundosDelDia = 24 * 60 * 60;
            int intervaloSegundos = totalSegundosDelDia / numeroDosis;

            int intervaloHoras = intervaloSegundos / 3600;  // 1 hora = 3600 segundos
            int intervaloMinutos = (intervaloSegundos % 3600) / 60;  // 1 minuto = 60 segundos
            int intervaloSegundosRestantes = intervaloSegundos % 60;  // Restante en segundos

            calendar.add(Calendar.HOUR_OF_DAY, intervaloHoras);  // Sumar las horas
            calendar.add(Calendar.MINUTE, intervaloMinutos);  // Sumar los minutos
            calendar.add(Calendar.SECOND, intervaloSegundosRestantes);  // Sumar los segundos

            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error al calcular";
        }
    }
    public String calcularFinTratamiento() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(duracionTratamiento));
        return sdf.format(calendar.getTime());
    }
    public String calcularFechas() {
        try {
            Date primeraDosisDate = formatter.parse(horaPrimeraDosis);
            int dosisPorDia = Integer.parseInt(dosisDia);
            long intervalMinutes = 1440 / dosisPorDia; // 1440 minutos en un día dividido por el número de dosis por día
            long intervalMillis = TimeUnit.MINUTES.toMillis(intervalMinutes);

            Date siguienteDosisDate = new Date(primeraDosisDate.getTime() + intervalMillis);

            return formatter.format(siguienteDosisDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void tomarDosis() {
        siguienteDosis = calcularSiguienteToma();
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

