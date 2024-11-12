package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MedicamentosView extends AppCompatActivity {

    private Intent pasarPantallaMain;
    private Intent pasarPantallaMod;

    protected TextView label1;
    protected TextView label2;
    protected TextView label3;
    protected TextView label4;
    protected TextView label5;

    protected Button boton1;
    protected Button boton2;
    protected Button boton3;


    private Bundle extras;
    private String medNombre;
    private String medDosis;
    private String medNumTomas;
    private String medDuracion;
    private String medHoraDosis1;


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
        boton1 = (Button) findViewById(R.id.button1_med_view);
        boton2 = (Button) findViewById(R.id.button2_med_view);
        boton3 = (Button) findViewById(R.id.button3_med_view);

        med = getIntent().getStringExtra("MED");

        pasarPantallaMain = new Intent(MedicamentosView.this, ModulosActivity.class);
        pasarPantallaMod = new Intent(MedicamentosView.this, MedicamentosModify.class);

        extras = getIntent().getExtras();
        if (extras != null) {
            medNombre = extras.getString("MED_NOMBRE");
            medDosis = extras.getString("MED_DOSIS");
            medNumTomas = extras.getString("MED_NUM_DOSIS");
            medDuracion = extras.getString("MED_DURACION");
            medHoraDosis1 = extras.getString("MED_HORA_DOSIS");

            // pendiente hacer getNombre/dosis etc, para ubicar el cursor en la posicion que hemos seleccionado


            label1.setText(medNombre);
            label2.setText(medDosis);
            label3.setText(medNumTomas);
            label4.setText(medDuracion);
            label5.setText(medHoraDosis1);

        }
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(pasarPantallaMain);
                finish();
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarPantallaMod.putExtra("MED_NOMBRE", medNombre);
                pasarPantallaMod.putExtra("MED_DOSIS", medDosis);
                pasarPantallaMod.putExtra("MED_NUM_DOSIS", medNumTomas);
                pasarPantallaMod.putExtra("MED_DURACION", medDuracion);
                pasarPantallaMod.putExtra("MED_HORA_DOSIS", medHoraDosis1);
                startActivity(pasarPantallaMod);
                finish();

            }
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


