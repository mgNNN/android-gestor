package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MedicamentosView extends AppCompatActivity {

    private Intent pasarPantallaMain;
    private Intent pasarPantallaMod;

    protected TextView label1;
    protected TextView label2;
    protected TextView label3;
    protected TextView label4;
    protected TextView label5;

    protected EditText caja1;
    protected EditText caja2;
    protected EditText caja3;
    protected EditText caja4;
    protected EditText caja5;

    protected Button boton1;
    protected Button boton2;
    protected Button boton3;


    private Bundle extras;
    private String medNombre;
    private String medDosis;
    private String medNumTomas;
    private String medDuracion;
    private String medHoraDosis1;
    private String itemId;


    private String med;
    private String[] partes;
    private Integer medID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_view);

        label1 = (TextView) findViewById(R.id.label1_med_view);
        label2 = (TextView) findViewById(R.id.label2_med_view);
        label3 = (TextView) findViewById(R.id.label3_med_view);
        label4 = (TextView) findViewById(R.id.label4_med_view);
        label5 = (TextView) findViewById(R.id.label5_med_view);

        caja1 = (EditText) findViewById(R.id.caja1_med_view);
        caja2 = (EditText) findViewById(R.id.caja2_med_mod3);
        caja3 = (EditText) findViewById(R.id.caja3_med_mod3);
        caja4 = (EditText) findViewById(R.id.caja4_med_mod3);
        caja5 = (EditText) findViewById(R.id.caja5_med_mod3);

        boton1 = (Button) findViewById(R.id.button1_med_view);
        boton2 = (Button) findViewById(R.id.button2_med_view);
        boton3 = (Button) findViewById(R.id.button3_med_view);

        pasarPantallaMain = new Intent(MedicamentosView.this, ModulosActivity.class);
        pasarPantallaMod = new Intent(MedicamentosView.this, MedicamentosModify.class);

        Intent intent = getIntent();
        Medicamento medicamentoItem = (Medicamento) intent.getSerializableExtra("medicamento");
        if (medicamentoItem != null) {
            caja1.setText(medicamentoItem.getNombre());
            caja2.setText(medicamentoItem.getDosis());
            caja3.setText(medicamentoItem.getDosisDia());
            caja4.setText(medicamentoItem.getDuracionTratamiento());
            caja5.setText(medicamentoItem.getHoraPrimeraDosis());
        } else {
            Toast.makeText(this, "No se recibieron datos del medicamento", Toast.LENGTH_SHORT).show();
        }

///// ahora solo estamos pasando los datos del primer medicamento creado.
// Pendiente obtener la posicion y obtener los datos de ese elemento del array list
//        ArrayList<String> medsInfo = getIntent().getStringArrayListExtra("medsInfo");
//        if (medsInfo != null) {
//            String medNombre = medsInfo.get(0);
//            String medDosis = medsInfo.get(1);
//            String medNumTomas = medsInfo.get(2);
//            String medDuracion = medsInfo.get(3);
//            String medHoraDosis1 = medsInfo.get(4);
//
//
//            caja1.setText(medNombre);
//            caja2.setText(medDosis);
//            caja3.setText(medNumTomas);
//            caja4.setText(medDuracion);
//            caja5.setText(medHoraDosis1);
//        }
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(pasarPantallaMain);
                finish();
            }
        });
        boton2.setOnClickListener(v -> {
            pasarPantallaMod.putExtra("MED_NOMBRE", medicamentoItem.getNombre());
            pasarPantallaMod.putExtra("MED_DOSIS", medicamentoItem.getDosis());
            pasarPantallaMod.putExtra("MED_NUM_DOSIS", medicamentoItem.getDosisDia());
            pasarPantallaMod.putExtra("MED_DURACION", medicamentoItem.getDuracionTratamiento());
            pasarPantallaMod.putExtra("MED_HORA_DOSIS", medicamentoItem.getHoraPrimeraDosis());
            startActivity(pasarPantallaMod);
            finish();
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partes = med.split(".-");
                medID = Integer.parseInt(partes[0]);
                if(medID!=-1) {
                    //db.deleteMedicamento(medID);
                }
                finish();
                startActivity(pasarPantallaMain);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_med_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_salir) {
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }
}


