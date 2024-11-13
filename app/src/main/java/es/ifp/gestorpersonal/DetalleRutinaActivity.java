package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetalleRutinaActivity extends AppCompatActivity {

    private TextView textViewNombreRutina;
    private ListView listViewSeries;
    private String nombreRutina;
    private ArrayList<Serie> seriesList;
    private ArrayAdapter<String> adapter;
    private List<String> seriesDetails = new ArrayList<>();
    protected Intent pasarPantalla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_rutina);

        textViewNombreRutina = findViewById(R.id.textViewNombreRutina);
        listViewSeries = findViewById(R.id.listViewSeries);

        // Recuperar el nombre de la rutina y la lista de series
        nombreRutina = getIntent().getStringExtra("nombreRutina");
        seriesList = (ArrayList<Serie>) getIntent().getSerializableExtra("seriesList");

        // Configurar TextView con el nombre de la rutina
        textViewNombreRutina.setText(nombreRutina);

        // Preparar los detalles de cada serie para mostrar en el ListView
        if (seriesList != null && !seriesList.isEmpty()) {
            String lastExerciseName = "";

            for (Serie serie : seriesList) {
                String currentExerciseName = serie.getNombreEjercicio();

                // Si el ejercicio es diferente al anterior, agrega una línea en blanco para separar
                if (!lastExerciseName.equals(currentExerciseName) && !lastExerciseName.isEmpty()) {
                    seriesDetails.add(""); // Añadir línea en blanco
                }

                String detalle = currentExerciseName + ", Peso: " + serie.getPeso() + " kg" +
                        ", Repeticiones: " + serie.getRepeticiones();
                seriesDetails.add(detalle);

                lastExerciseName = currentExerciseName;
            }
        }

        // Configurar ListView con los detalles de las series
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seriesDetails);
        listViewSeries.setAdapter(adapter);

        listViewSeries.setOnItemClickListener((parent, view, position, id) -> {
            // Comprobar que el elemento seleccionado no es una línea en blanco
            if (!seriesDetails.get(position).isEmpty()) {
                // Mostrar diálogo para editar peso y repeticiones
                showEditDialog(position);
            }
        });
    }

    private void showEditDialog(int position) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_series, null);
        EditText editTextPeso = dialogView.findViewById(R.id.editTextPesoModificar);
        EditText editTextRepeticiones = dialogView.findViewById(R.id.editTextRepeticionesModificar);

        Serie selectedSerie = seriesList.get(position);

        // Rellenar con valores actuales
        editTextPeso.setText(String.valueOf(selectedSerie.getPeso()));
        editTextRepeticiones.setText(String.valueOf(selectedSerie.getRepeticiones()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modificar Serie")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        double newPeso = Double.parseDouble(editTextPeso.getText().toString());
                        int newRepeticiones = Integer.parseInt(editTextRepeticiones.getText().toString());

                        // Actualizar los valores en el objeto Serie
                        selectedSerie.setPeso(newPeso);
                        selectedSerie.setRepeticiones(newRepeticiones);

                        // Actualizar los detalles en la lista de visualización
                        seriesDetails.set(position, selectedSerie.getNombreEjercicio() + ", Peso: " + newPeso + " kg, Repeticiones: " + newRepeticiones);
                        adapter.notifyDataSetChanged();

                        // Llamar al método para enviar la solicitud PUT al backend
                        actualizarSerieEnBaseDeDatos(selectedSerie.getId(), newPeso, newRepeticiones);

                        Toast.makeText(this, "Serie actualizada", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Por favor, ingrese valores válidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void actualizarSerieEnBaseDeDatos(int serieId, double nuevoPeso, int nuevasRepeticiones) {
        OkHttpClient client = new OkHttpClient();

        // Crea el cuerpo de la solicitud PUT con los datos de la serie
        String json = "{ \"peso\": " + nuevoPeso + ", \"repeticiones\": " + nuevasRepeticiones + " }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        // Enviar la solicitud PUT
        Request request = new Request.Builder()
                .url("https://gestor-personal-4898737da4af.herokuapp.com/series/" + serieId)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(DetalleRutinaActivity.this, "Error al actualizar la serie", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleRutinaActivity.this, "Serie actualizada correctamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleRutinaActivity.this, "Error en la actualización de la serie", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
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
            pasarPantalla = new Intent(DetalleRutinaActivity.this, GimnasioActivity.class);
            startActivity(pasarPantalla);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
