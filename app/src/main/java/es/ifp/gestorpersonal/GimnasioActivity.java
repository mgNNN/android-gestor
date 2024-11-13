package es.ifp.gestorpersonal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GimnasioActivity extends AppCompatActivity {

    protected Intent pasarPantalla;
    protected Button boton_rutina;
    protected EditText popupNombre;
    protected int userId;
    private ListView listViewRutinas;
    private ArrayAdapter<String> nombreAdapter;
    private OkHttpClient client;
    private List<Rutina> rutinasList; // Lista para almacenar objetos completos de Rutina

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gimnasio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boton_rutina = findViewById(R.id.boton1_rutina);
        listViewRutinas = findViewById(R.id.listViewRutinas);

        // Recuperar el userId de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        // Inicializar OkHttpClient
        client = new OkHttpClient();

        // Inicializar la lista de rutinas y el adaptador para mostrar nombres en el ListView
        rutinasList = new ArrayList<>();
        nombreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewRutinas.setAdapter(nombreAdapter);

        // Cargar rutinas al iniciar la actividad
        cargarRutinas();

        listViewRutinas.setOnItemClickListener((parent, view, position, id) -> {
            if (!rutinasList.isEmpty() && position < rutinasList.size()) {
                Rutina selectedRutina = rutinasList.get(position);

                Intent intent2 = new Intent(GimnasioActivity.this, DetalleRutinaActivity.class);
                intent2.putExtra("nombreRutina", selectedRutina.getNombre());

                // Extraer todas las series de todos los ejercicios en la rutina seleccionada
                ArrayList<Serie> seriesList = new ArrayList<>();
                for (Ejercicio ejercicio : selectedRutina.getEjercicios()) {
                    seriesList.addAll(ejercicio.getSeries()); // Añade todas las series de cada ejercicio
                }
                intent2.putExtra("seriesList", seriesList);

                startActivity(intent2);
            } else {
                Toast.makeText(GimnasioActivity.this, "No hay rutinas disponibles", Toast.LENGTH_SHORT).show();
            }
        });



        boton_rutina.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_text_gim, null);
            EditText editTextRoutineName = dialogView.findViewById(R.id.editTextPopUp);

            // Crear el AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Crear nueva rutina")
                    .setView(dialogView)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        String nombreRutina = editTextRoutineName.getText().toString();
                        pasarPantalla = new Intent(GimnasioActivity.this, RutinaActivity.class);
                        pasarPantalla.putExtra("nombreRutina", nombreRutina); // Solo pasar el nombre de la rutina
                        startActivity(pasarPantalla);
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            // Mostrar el cuadro de diálogo
            builder.create().show();
        });
    }

    private void cargarRutinas() {
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/rutinas?userId=" + userId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(GimnasioActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    parseRutinas(responseBody);
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(GimnasioActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void parseRutinas(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            HashMap<Integer, Rutina> rutinaMap = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int rutinaId = obj.getInt("rutina_id");
                String nombreRutina = obj.getString("nombre_rutina");
                String nombreEjercicio = obj.getString("nombre_ejercicio"); // Obtén el nombre del ejercicio
                double peso = obj.getDouble("peso");
                int series = obj.getInt("series");
                int repeticiones = obj.getInt("repeticiones");

                Serie serie = new Serie(series, peso, repeticiones, nombreEjercicio); // Pasa nombreEjercicio

                Rutina rutina;
                if (!rutinaMap.containsKey(rutinaId)) {
                    rutina = new Rutina(nombreRutina, userId, new ArrayList<>());
                    rutinaMap.put(rutinaId, rutina);
                } else {
                    rutina = rutinaMap.get(rutinaId);
                }

                Ejercicio ejercicio = new Ejercicio(series);
                ejercicio.addSerie(serie);
                rutina.getEjercicios().add(ejercicio);
            }

            List<Rutina> rutinas = new ArrayList<>(rutinaMap.values());
            runOnUiThread(() -> mostrarRutinas(rutinas));

        } catch (JSONException e) {
            runOnUiThread(() ->
                    Toast.makeText(GimnasioActivity.this, "Error en el análisis de la respuesta", Toast.LENGTH_SHORT).show()
            );
            e.printStackTrace();
        }
    }


    private void mostrarRutinas(List<Rutina> rutinas) {
        rutinasList.clear();
        rutinasList.addAll(rutinas); // Almacena los objetos completos de tipo Rutina

        // Crear una lista de nombres para el adaptador de nombres
        List<String> nombresRutinas = new ArrayList<>();
        for (Rutina rutina : rutinas) {
            nombresRutinas.add(rutina.getNombre());
        }

        // Actualizar el adaptador de nombres
        nombreAdapter.clear();
        nombreAdapter.addAll(nombresRutinas);
        nombreAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRutinas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_salir_rutina_gim) {
            finishAffinity();
            System.exit(0);
        } else if (id == R.id.menu_volver_rutina_gim) {
            pasarPantalla = new Intent(GimnasioActivity.this, ModulosActivity.class);
            startActivity(pasarPantalla);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
