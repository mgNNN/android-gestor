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

public class MedicamentosAdd extends AppCompatActivity {

    protected TextView label1;
    protected TextView label2;
    protected EditText caja1;
    protected EditText caja2;
    protected EditText caja3;
    protected EditText caja4;
    protected EditText caja5;
    protected EditText caja6;
    protected EditText caja7;
    protected Button boton1;
    private Intent pasarPantallaMain;
    private Intent pasarPantallaAddMed;
    private Intent pasarPantallaMed;


    private String contenidoCaja1;
    private String contenidoCaja2;
    private String contenidoCaja3;
    private String contenidoCaja4;
    private String contenidoCaja5;
    private String medID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_add);
        label1 = (TextView) findViewById(R.id.label1_med_add);
        label2 = (TextView) findViewById(R.id.label2_med_add);
        caja1 = (EditText) findViewById(R.id.caja1_med_add);
        caja2 = (EditText) findViewById(R.id.caja2_med_add);
        caja3 = (EditText) findViewById(R.id.caja3_med_add);
        caja4 = (EditText) findViewById(R.id.caja4_med_add);
        caja5 = (EditText) findViewById(R.id.caja5_med_add);
        caja6 = (EditText) findViewById(R.id.caja6_med_add);
        caja7 = (EditText) findViewById(R.id.caja7_med_add);
        boton1 = (Button) findViewById(R.id.button1_med_add);

        pasarPantallaMain = new Intent(MedicamentosAdd.this, ModulosActivity.class);
        pasarPantallaMed = new Intent(MedicamentosAdd.this, MedicamentosActivity.class);
        pasarPantallaAddMed = new Intent(MedicamentosAdd.this, MedicamentosAdd.class);
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoCaja1 = caja1.getText().toString();
                contenidoCaja2 = caja2.getText().toString();
                contenidoCaja3 = caja3.getText().toString();
                contenidoCaja4 = caja4.getText().toString();
                contenidoCaja5 = caja5.getText().toString();

                if (contenidoCaja1.isEmpty()) {
                    Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if (contenidoCaja2.isEmpty()) {
                    Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if (contenidoCaja3.isEmpty()) {
                    Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if (contenidoCaja4.isEmpty()) {
                    Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if (contenidoCaja5.isEmpty()) {
                    Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MedicamentosAdd.this, "Medicamento guardado correctamente", Toast.LENGTH_SHORT).show();

                    pasarPantallaMed.putExtra("MED_NOMBRE", contenidoCaja1);
                    pasarPantallaMed.putExtra("MED_DOSIS", contenidoCaja2);
                    pasarPantallaMed.putExtra("MED_NUM_DOSIS", contenidoCaja3);
                    pasarPantallaMed.putExtra("MED_DURACION", contenidoCaja4);
                    pasarPantallaMed.putExtra("MED_HORA_DOSIS", contenidoCaja5);


                    startActivity(pasarPantallaMed);
                    finish();
                }
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

