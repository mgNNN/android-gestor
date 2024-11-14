package es.ifp.gestorpersonal;


import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    // Declaración de OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    private int userId;
    private int userIDdef;
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
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // El valor -1 es por si no existe un userId guardado
        userIDdef=userId;
        boton1.setOnClickListener(v -> {
            contenidoCaja1 = caja1.getText().toString();  // Nombre del medicamento
            contenidoCaja2 = caja2.getText().toString();  // Dosis
            contenidoCaja3 = caja3.getText().toString();  // Dosis por día
            contenidoCaja4 = caja4.getText().toString();  // Duración del tratamiento
            contenidoCaja5 = caja5.getText().toString();  // Hora primera dosis

            if (contenidoCaja1.isEmpty() || contenidoCaja2.isEmpty() || contenidoCaja3.isEmpty() ||
                    contenidoCaja4.isEmpty() || contenidoCaja5.isEmpty()) {
                Toast.makeText(MedicamentosAdd.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                guardarMedicamento();
            }
        });
    }
    private void guardarMedicamento() {
        try {
            // Crear JSON con los datos del medicamento
            JSONObject medicamentoJson = new JSONObject();
            medicamentoJson.put("user_id", userIDdef); // Asegúrate de pasar el user_id adecuado
            medicamentoJson.put("medicamento", contenidoCaja1);
            medicamentoJson.put("dosis", Double.parseDouble(contenidoCaja2));
            medicamentoJson.put("dosisDia", Integer.parseInt(contenidoCaja3));
            medicamentoJson.put("duracionTratamiento", Float.parseFloat(contenidoCaja4));
            medicamentoJson.put("horaPrimeraDosis", contenidoCaja5);

            // Crear RequestBody
            RequestBody body = RequestBody.create(
                    medicamentoJson.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            // Crear Request
            Request request = new Request.Builder()
                    .url("https://gestor-personal-4898737da4af.herokuapp.com/medicamentos") // Asegúrate de que esta URL sea correcta
                    .post(body)
                    .build();

            // Ejecutar la solicitud en segundo plano
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(MedicamentosAdd.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(MedicamentosAdd.this, "Medicamento guardado correctamente", Toast.LENGTH_SHORT).show();
                            startActivity(pasarPantallaMed);
                            finish();
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(MedicamentosAdd.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear el medicamento", Toast.LENGTH_SHORT).show();
        }
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

