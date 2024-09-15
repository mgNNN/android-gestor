package es.ifp.gestorpersonal;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MedicamentosActivity extends AppCompatActivity {

    private Intent pasarPantallaMain;
    private Intent pasarPantallaAddMed;
    private Intent pasarPantallaMed;
    private Intent pasarPantallaViewMed;
    protected ListView lista1;
    private ArrayList<String> meds = new ArrayList<String>();
    private ArrayAdapter<String> adaptador;
    private Bundle extras;

    private String medNombre;
    private String medDosis;
    private String medNumTomas;
    private String medDuracion;
    private String medHoraDosis1;

    private String contenidoItem = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicamento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        lista1 = (ListView) findViewById(R.id.lista1_med);

        meds = db.getAllMedicamentos();
        adaptador = new ArrayAdapter<String>(MedicamentosActivity.this, android.R.layout.simple_list_item_1, meds);
        lista1.setAdapter(adaptador);
        pasarPantallaMed = new Intent(MedicamentosActivity.this, MedicamentosActivity.class);
        pasarPantallaMed.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        pasarPantallaMain = new Intent(MedicamentosActivity.this, ModulosActivity.class);
        pasarPantallaAddMed = new Intent(MedicamentosActivity.this, MedicamentosAdd.class);

        extras = getIntent().getExtras();
        if (extras != null) {
            medNombre = extras.getString("MED_NOMBRE");
            medDosis = extras.getString("MED_DOSIS");
            medNumTomas = extras.getString("MED_NUM_DOSIS");
            medDuracion = extras.getString("MED_DURACION");
            medHoraDosis1 = extras.getString("MED_HORA_DOSIS");


            Toast.makeText(this, "Medicamento enviado correctamente", Toast.LENGTH_SHORT).show();
            db.insertMedicamento(medNombre, medDosis, medNumTomas, medDuracion, medHoraDosis1);

            startActivity(pasarPantallaMed);
            pasarPantallaMed.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        }

        lista1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contenidoItem = parent.getItemAtPosition(position).toString();
                Intent pasarPantallaViewMed = new Intent(MedicamentosActivity.this, MedicamentosView.class);
                pasarPantallaViewMed.putExtra("MED", contenidoItem);
                startActivity(pasarPantallaViewMed);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_med, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_volver) {
            finish();
            startActivity(pasarPantallaMain);
        } else if (item.getItemId() == R.id.menu_salir) {
            System.exit(0);
        } else if (item.getItemId() == R.id.menu_add) {
            startActivity(pasarPantallaAddMed);
        }
        return super.onOptionsItemSelected(item);
    }
}


