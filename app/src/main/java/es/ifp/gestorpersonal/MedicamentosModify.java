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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MedicamentosModify extends AppCompatActivity {

    private Intent pasarPantallaView;

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
    private Bundle extras;
    private String medNombre;
    private String medDosis;
    private String medNumTomas;
    private String medDuracion;
    private String medHoraDosis1;

    private String contenidoCaja1;
    private String contenidoCaja2;
    private String contenidoCaja3;
    private String contenidoCaja4;
    private String contenidoCaja5;

    protected Button boton1;
    protected Button boton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_modify);

        label1 = (TextView) findViewById(R.id.label1_med_mod);
        label2 = (TextView) findViewById(R.id.label2_med_mod);
        label3 = (TextView) findViewById(R.id.label3_med_mod);
        label4 = (TextView) findViewById(R.id.label4_med_mod);
        label5 = (TextView) findViewById(R.id.label5_med_mod);

        caja1 = (EditText) findViewById(R.id.caja1_med_mod);
        caja2 = (EditText) findViewById(R.id.caja2_med_mod);
        caja3 = (EditText) findViewById(R.id.caja3_med_mod);
        caja4 = (EditText) findViewById(R.id.caja4_med_mod);
        caja5 = (EditText) findViewById(R.id.caja5_med_mod);

        boton1 = (Button) findViewById(R.id.button1_med_mod);
        boton2 = (Button) findViewById(R.id.button2_med_mod);

        pasarPantallaView = new Intent(MedicamentosModify.this, MedicamentosView.class);


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
                startActivity(pasarPantallaView);
                finish();
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoCaja1 = caja1.getText().toString();
                contenidoCaja2 = caja2.getText().toString();
                contenidoCaja3 = caja3.getText().toString();
                contenidoCaja4 = caja4.getText().toString();
                contenidoCaja5 = caja5.getText().toString();


                if (!contenidoCaja1.isEmpty()) {
                    pasarPantallaView.putExtra("MED_NOMBRE", contenidoCaja1);
                } else {
                    pasarPantallaView.putExtra("MED_NOMBRE", medNombre);
                }
                if (!contenidoCaja2.isEmpty()) {
                    pasarPantallaView.putExtra("MED_DOSIS", contenidoCaja2);
                } else {
                    pasarPantallaView.putExtra("MED_DOSIS", medDosis);
                }
                if (!contenidoCaja3.isEmpty()) {
                    pasarPantallaView.putExtra("MED_NUM_DOSIS", contenidoCaja3);
                } else {
                    pasarPantallaView.putExtra("MED_NUM_DOSIS", medNumTomas);
                }
                if (!contenidoCaja4.isEmpty()) {
                    pasarPantallaView.putExtra("MED_DURACION", contenidoCaja4);
                } else {
                    pasarPantallaView.putExtra("MED_DURACION", medDuracion);
                }
                if (!contenidoCaja5.isEmpty()) {
                    pasarPantallaView.putExtra("MED_HORA_DOSIS", contenidoCaja5);
                } else {
                    pasarPantallaView.putExtra("MED_HORA_DOSIS", medHoraDosis1);
                }
                startActivity(pasarPantallaView);
                finish();

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