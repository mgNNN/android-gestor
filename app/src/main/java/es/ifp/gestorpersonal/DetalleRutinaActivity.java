package es.ifp.gestorpersonal;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetalleRutinaActivity extends AppCompatActivity {

    private TextView textViewNombreRutina;
    private ListView listViewSeries;
    private String nombreRutina;
    private int userId;
    private OkHttpClient client;
    private List<String> seriesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_rutina);

        textViewNombreRutina = findViewById(R.id.textViewNombreRutina);
        listViewSeries = findViewById(R.id.listViewSeries);
        client = new OkHttpClient();

        // Recuperar datos pasados
        nombreRutina = getIntent().getStringExtra("nombreRutina");
        userId = getIntent().getIntExtra("userId", -1);

        textViewNombreRutina.setText(nombreRutina);

        // Configurar ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seriesList);
        listViewSeries.setAdapter(adapter);

        // Cargar los detalles de la rutina
        cargarDetallesRutina();
    }

    private void cargarDetallesRutina() {
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/rutinas/detalle?nombre=" + nombreRutina + "&userId=" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetalleRutinaActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        seriesList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject serie = jsonArray.getJSONObject(i);
                            String peso = serie.getString("peso");
                            String repeticiones = serie.getString("repeticiones");
                            seriesList.add("Peso: " + peso + "kg, Repeticiones: " + repeticiones);
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(DetalleRutinaActivity.this, "Error al analizar la respuesta", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DetalleRutinaActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
