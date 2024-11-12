package es.ifp.gestorpersonal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    private List<String> rutinasList;
    private ArrayAdapter<String> adapter;
    private OkHttpClient client;

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

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);  // Recibe el userId pasado desde LoginActivity

        // Inicializar OkHttpClient
        client = new OkHttpClient();

        // Configurar el adaptador para el ListView
        rutinasList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rutinasList);
        listViewRutinas.setAdapter(adapter);

        // Cargar rutinas al iniciar la actividad
        cargarRutinas();
        listViewRutinas.setOnItemClickListener((parent, view, position, id) -> {
            if (!rutinasList.isEmpty() && position < rutinasList.size()) {
                String selectedRutina = rutinasList.get(position);
                // Continúa con la lógica para abrir la nueva actividad, usando el `selectedRutina`
                Intent intent2 = new Intent(GimnasioActivity.this, DetalleRutinaActivity.class);
                intent2.putExtra("nombreRutina", selectedRutina);
                intent2.putExtra("userId", userId);
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
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Obtener el texto del EditText
                            String nombreRutina = editTextRoutineName.getText().toString();
                            pasarPantalla = new Intent(GimnasioActivity.this, RutinaActivity.class);
                            pasarPantalla.putExtra("nombreRutina", nombreRutina);
                            pasarPantalla.putExtra("userId", userId);
                            startActivity(pasarPantalla);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

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
                    parseRutinas(responseBody); // Llamar a parseRutinas para procesar la respuesta JSON
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
                int serieId = obj.getInt("serie_id");
                double peso = obj.getDouble("peso");
                int repeticiones = obj.getInt("repeticiones");

                // Crear el objeto Serie
                Serie serie = new Serie(serieId, peso, repeticiones);

                // Verificar si la rutina ya existe en el mapa
                Rutina rutina;
                if (!rutinaMap.containsKey(rutinaId)) {
                    rutina = new Rutina(nombreRutina, userId, new ArrayList<>());
                    rutinaMap.put(rutinaId, rutina);
                } else {
                    rutina = rutinaMap.get(rutinaId);
                }

                // Agregar la serie a la rutina en el mapa
                Ejercicio ejercicio = new Ejercicio(serieId); // Asumiendo un ejercicio por serie para este ejemplo
                ejercicio.addSerie(serie);
                rutina.getEjercicios().add(ejercicio);
            }

            // Convertir el mapa de rutinas en una lista
            List<Rutina> rutinas = new ArrayList<>(rutinaMap.values());

            // Mostrar las rutinas en el ListView
            runOnUiThread(() -> mostrarRutinas(rutinas));

        } catch (JSONException e) {
            runOnUiThread(() ->
                    Toast.makeText(GimnasioActivity.this, "Error en el análisis de la respuesta", Toast.LENGTH_SHORT).show()
            );
            e.printStackTrace();
        }
    }
    private void mostrarRutinas(List<Rutina> rutinas) {
        RutinaAdapter rutinaAdapter = new RutinaAdapter(this, rutinas);
        listViewRutinas.setAdapter(rutinaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar las rutinas al volver a esta actividad
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
            finish();
            startActivity(pasarPantalla);
        }
        return super.onOptionsItemSelected(item);
    }
}
