package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaActivity extends AppCompatActivity {

    private TextView textViewNombreRutina;
    private ListView listViewSeries;
    private String nombreRutina;
    private ArrayList<Serie> seriesList; // Recibir como ArrayList<Serie>
    private ArrayAdapter<String> adapter;
    private List<String> seriesDetails = new ArrayList<>(); // Para mostrar los detalles en formato de texto
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
        if (seriesList != null) {
            for (Serie serie : seriesList) {
                String detalle = "Serie ID: " + serie.getSeries() +
                        ", Peso: " + serie.getPeso() + " kg" +
                        ", Repeticiones: " + serie.getRepeticiones();
                seriesDetails.add(detalle);
            }
        }

        // Configurar ListView con los detalles de las series
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seriesDetails);
        listViewSeries.setAdapter(adapter);
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
