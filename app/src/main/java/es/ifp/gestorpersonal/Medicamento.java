package es.ifp.gestorpersonal;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Medicamento implements Serializable {
    private String nombre;
    private String dosis;
    private String dosisDia;
    private String duracionTratamiento;
    private String horaPrimeraDosis;
    private String siguienteDosis;
    private int dosisTomadas;
    private int id;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // Constructor
    public Medicamento(String nombre, String dosis, String dosisDia, int dosisTomadas, String duracionTratamiento, String horaPrimeraDosis, int id) {
        this.nombre = nombre;
        this.dosis = dosis;
        this.dosisDia = dosisDia;
        this.duracionTratamiento = duracionTratamiento;
        this.horaPrimeraDosis = horaPrimeraDosis;
        this.id = id;
        this.dosisTomadas = dosisTomadas;
    }

    public String calcularSiguienteToma() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();  // Usamos la fecha y hora actuales como referencia
        int numeroDosis = Integer.parseInt(dosisDia);
        int duracion = Integer.parseInt(duracionTratamiento);
        int numeroDosisTotal = numeroDosis * duracion; // Número de dosis al día
        if (horaPrimeraDosis != null && !horaPrimeraDosis.isEmpty()) {
            try {
                Date primeraDosis = sdf.parse(horaPrimeraDosis);
                calendar.setTime(primeraDosis);  // Establecemos el calendario con la hora de la primera dosis

                int totalSegundosDelDia = 24 * 60 * 60;  // 24 horas * 60 minutos * 60 segundos
                int intervaloSegundos = totalSegundosDelDia / numeroDosis;  // Intervalo en segundos entre dosis

                if (dosisTomadas >= numeroDosisTotal) {
                    calendar.add(Calendar.SECOND, intervaloSegundos * dosisTomadas);
                    return "El tratamiento ha finalizado";
                }else if(dosisTomadas > 0) {
                    calendar.add(Calendar.SECOND, intervaloSegundos * dosisTomadas);
                }
               // if (dosisTomadas > 0) {
                //    calendar.add(Calendar.SECOND, intervaloSegundos * dosisTomadas);
               // } else if (dosisTomadas >= numeroDosis) {
               //     return "El tratamiento ha finalizado";
               // }
                return sdf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return "Error en la hora de la primera dosis";
            }
        } else {
            return "Hora de la primera dosis no definida";
        }


    }

    public String calcularFinTratamiento() {
        try {
            // Inicializamos el calendario con la fecha y hora actuales
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            // Parseamos la hora de la primera dosis
            Date primeraDosis = sdf.parse(horaPrimeraDosis);

            // Establecemos la fecha actual pero con la hora de la primera dosis
            calendar.set(Calendar.HOUR_OF_DAY, primeraDosis.getHours());
            calendar.set(Calendar.MINUTE, primeraDosis.getMinutes());
            calendar.set(Calendar.SECOND, primeraDosis.getSeconds());

            // Número de dosis por día y duración en días
            int numeroDosis = Integer.parseInt(dosisDia);  // Dosis por día
            int duracion = Integer.parseInt(duracionTratamiento);  // Duración del tratamiento en días

            // Calculamos el intervalo de tiempo entre cada dosis (en segundos)
            int totalSegundosDelDia = 24 * 60 * 60;  // 24 horas * 60 minutos * 60 segundos
            int intervaloSegundos = totalSegundosDelDia / numeroDosis;  // Intervalo en segundos entre dosis

            // Convertimos el intervalo en horas, minutos y segundos
            int intervaloHoras = intervaloSegundos / 3600;
            int intervaloMinutos = (intervaloSegundos % 3600) / 60;
            int intervaloSegundosRestantes = intervaloSegundos % 60;

            // Iteramos sobre los días del tratamiento
            for (int dia = 1; dia <= duracion; dia++) {  // Comenzamos en 1 para incluir el primer día
                // Añadimos las dosis del día
                for (int dosis = 1; dosis <= numeroDosis; dosis++) {  // Iteramos sobre el número de dosis por día
                    if (dia == duracion && dosis == numeroDosis) {
                        break;  // Si es el último día y la última dosis, no sumamos más
                    }
                    calendar.add(Calendar.HOUR_OF_DAY, intervaloHoras);  // Sumamos las horas
                    calendar.add(Calendar.MINUTE, intervaloMinutos);  // Sumamos los minutos
                    calendar.add(Calendar.SECOND, intervaloSegundosRestantes);  // Sumamos los segundos
                }
            }

            return sdfFechaHora.format(calendar.getTime());  // Devolvemos la fecha y hora final del tratamiento

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al calcular";  // En caso de error
        }
    }


    public String tomarDosis() {
        int numeroDosis = Integer.parseInt(dosisDia);
        int duracion = Integer.parseInt(duracionTratamiento);
        int totalDosisPermitidas = duracion*numeroDosis;

        if (dosisTomadas+1 >= totalDosisPermitidas) {
            return "El tratamiento ha finalizado";
        }
        try {
            // Inicializamos el calendario con la hora de la primera dosis
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            Date primeraDosis = sdf.parse(horaPrimeraDosis);  // Parseamos la hora de la primera dosis
            calendar.setTime(primeraDosis);  // Establecemos la hora inicial en el calendario

            // Calculamos el intervalo entre dosis (en segundos)
            int totalSegundosDelDia = 24 * 60 * 60;  // 24 horas * 60 minutos * 60 segundos
            int intervaloSegundos = totalSegundosDelDia / numeroDosis;  // Intervalo en segundos entre dosis

            int horaSiguienteDosis = intervaloSegundos * dosisTomadas;
            // Convertimos el intervalo en horas, minutos y segundos
            int intervaloHoras = intervaloSegundos / 3600;
            int intervaloMinutos = (intervaloSegundos % 3600) / 60;
            int intervaloSegundosRestantes = intervaloSegundos % 60;
            boolean tratamientoFinalizado=false;
            // Iteramos sobre los días del tratamiento


            calendar.add(Calendar.HOUR_OF_DAY, intervaloHoras*(dosisTomadas+1));
            calendar.add(Calendar.MINUTE, intervaloMinutos*(dosisTomadas+1));
            calendar.add(Calendar.SECOND, intervaloSegundosRestantes*(dosisTomadas+1));
            SimpleDateFormat sdfFechaHora = new SimpleDateFormat("HH:mm:ss");
            siguienteDosis = sdfFechaHora.format(calendar.getTime());
            dosisTomadas++;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return siguienteDosis;
    }



    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getDosisDia() {
        return dosisDia;
    }

    public void setDosisDia(String dosisDia) {
        this.dosisDia = dosisDia;
    }

    public int getDosisTomadas() {
        return dosisTomadas;
    }
    public void setDosisTomadas(int dosisTomadas) {
        this.dosisTomadas = dosisTomadas;
    }

    public String getDuracionTratamiento() {
        return duracionTratamiento;
    }

    public void setDuracionTratamiento(String duracionTratamiento) {
        this.duracionTratamiento = duracionTratamiento;
    }

    public String getHoraPrimeraDosis() {
        return horaPrimeraDosis;
    }

    public void setHoraPrimeraDosis(String horaPrimeraDosis) {
        this.horaPrimeraDosis = horaPrimeraDosis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

