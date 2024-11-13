package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RutinaActivity extends AppCompatActivity {

    protected Spinner spinnerEjercicio;
    protected EditText editTextSeries;
    protected EditText editTextRepeticiones;
    protected Button buttonAddEjercicio;
    protected TextView textViewRutina;
    protected String nombreRutina;
    protected Intent pasarPantalla;
    protected LinearLayout contenedorEjercicios;
    private int userId;
    private OkHttpClient client;
    private List<String> exerciseNames = new ArrayList<>();
    private List<String> ejercicioList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private HashMap<String, Integer> exerciseMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        // Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        buttonAddEjercicio = findViewById(R.id.buttonAddEjercicio);
        textViewRutina = findViewById(R.id.textViewRutina);
        contenedorEjercicios = findViewById(R.id.contenedorEjercicios);

        client = new OkHttpClient();

        // Obtén el nombre de la rutina desde extras
        nombreRutina = getIntent().getStringExtra("nombreRutina");

        // Recuperar el userId desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        textViewRutina.setText(nombreRutina);

        // Configurar el botón para añadir ejercicios a la lista
        buttonAddEjercicio.setOnClickListener(v -> {
            fetchExerciseNames(); // Llama al método para obtener los nombres de los ejercicios
        });
    }

    private void guardarRutina() {
        // Construye los datos de la rutina
        List<Ejercicio> ejercicios = new ArrayList<>();
        for (int i = 0; i < contenedorEjercicios.getChildCount(); i++) {
            View ejercicioView = contenedorEjercicios.getChildAt(i);
            TextView nombreEjercicioView = ejercicioView.findViewById(R.id.nombreEjercicioGym);
            LinearLayout contenedorSeries = ejercicioView.findViewById(R.id.contenedorSeries);

            // Crear el objeto Ejercicio con su id
            Ejercicio ejercicio = new Ejercicio(getEjercicioId(nombreEjercicioView.getText().toString()));

            // Añadir cada serie al ejercicio
            for (int j = 0; j < contenedorSeries.getChildCount(); j++) {
                View serieView = contenedorSeries.getChildAt(j);
                EditText editTextPeso = serieView.findViewById(R.id.editTextPeso);
                EditText editTextRepeticiones = serieView.findViewById(R.id.editTextRepeticiones);

                // Establece valores predeterminados si los campos están vacíos
                double peso = 0.0;
                int repeticiones = 0;
                try {
                    peso = editTextPeso.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(editTextPeso.getText().toString());
                    repeticiones = editTextRepeticiones.getText().toString().isEmpty() ? 0 : Integer.parseInt(editTextRepeticiones.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Ingrese valores válidos en los campos de peso y repeticiones", Toast.LENGTH_SHORT).show();
                    return;
                }

                int series = j + 1; // Contador de series
                ejercicio.addSerie(new Serie(series, peso, repeticiones,nombreEjercicioView.getText().toString()));
            }
            ejercicios.add(ejercicio);
        }

        // Construir el JSON para la API
        Rutina rutina = new Rutina(nombreRutina, userId, ejercicios);
        String jsonData = new Gson().toJson(rutina);

        // Crear el cuerpo de la solicitud
        RequestBody body = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://gestor-personal-4898737da4af.herokuapp.com/rutinas")
                .post(body)
                .build();

        // Enviar la solicitud al servidor
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(RutinaActivity.this, "Error al guardar la rutina", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(RutinaActivity.this, "Rutina guardada exitosamente", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(RutinaActivity.this, "Error del servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchExerciseNames() {
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/ejercicios/nombres";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RutinaActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        exerciseNames.clear(); // Limpiamos para evitar duplicados
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ejercicio = jsonArray.getJSONObject(i);
                            int id = ejercicio.getInt("id");
                            String nombre = ejercicio.getString("nombre");

                            exerciseMap.put(nombre, id);
                            exerciseNames.add(nombre);
                        }
                        runOnUiThread(() -> showAddExerciseDialog());
                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(RutinaActivity.this, "Error en el análisis de la respuesta", Toast.LENGTH_SHORT).show()
                        );
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(RutinaActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void showAddExerciseDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_spinner_gim, null);

        Spinner spinnerEjercicioDialog = dialogView.findViewById(R.id.spinnerGim);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicioDialog.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añade el ejercicio")
                .setView(dialogView)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String ejercicioSeleccionado = spinnerEjercicioDialog.getSelectedItem().toString();

                    // Infla el layout del ejercicio y lo agrega al contenedor principal
                    View ejercicioView = inflater.inflate(R.layout.item_ejercicio, contenedorEjercicios, false);
                    TextView textViewNombreEjercicio = ejercicioView.findViewById(R.id.nombreEjercicioGym);
                    textViewNombreEjercicio.setText(ejercicioSeleccionado);

                    LinearLayout contenedorSeries = ejercicioView.findViewById(R.id.contenedorSeries);
                    Button buttonAgregarSerie = ejercicioView.findViewById(R.id.buttonAgregarSerie);

                    buttonAgregarSerie.setOnClickListener(v1 -> {
                        View serieView = inflater.inflate(R.layout.item_serie, contenedorSeries, false);
                        contenedorSeries.addView(serieView);
                    });

                    contenedorEjercicios.addView(ejercicioView);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private int getEjercicioId(String nombreEjercicio) {
        return exerciseMap.getOrDefault(nombreEjercicio, -1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rutina_gim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_salir_rutina_gim) {
            finishAffinity();
            System.exit(0);
        } else if (id == R.id.menu_volver_rutina_gim) {
            pasarPantalla = new Intent(RutinaActivity.this, GimnasioActivity.class);
            finish();
            startActivity(pasarPantalla);
        } else if (id == R.id.menu_guardar_rutina_gim) {
            guardarRutina();
            pasarPantalla = new Intent(RutinaActivity.this, GimnasioActivity.class);
            finish();
            startActivity(pasarPantalla);
        }
        return super.onOptionsItemSelected(item);
    }
}
