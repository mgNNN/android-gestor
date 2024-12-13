package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MedicamentosView extends AppCompatActivity {

    private Intent pasarPantallaMain;

    protected TextView label1;
    protected TextView label2;
    protected TextView label3;
    protected TextView label4;
    protected TextView label5;
    protected TextView label6;
    protected TextView label7;

    protected EditText caja1;
    protected EditText caja2;
    protected EditText caja3;
    protected EditText caja4;
    protected EditText caja5;
    protected EditText caja6;
    protected EditText caja7;

    protected Button boton1;
    protected Button boton2;
    protected Button boton3;
    protected Button boton4;

    private int userId;
    private int userIDdef;
    private Integer medID = -1;
    private String siguienteDosis;
    private int intervalHoras;
    private int dosisTomadas=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_view);

        label1 = findViewById(R.id.label1_med_view);
        label2 = findViewById(R.id.label2_med_view);
        label3 = findViewById(R.id.label3_med_view);
        label4 = findViewById(R.id.label4_med_view);
        label5 = findViewById(R.id.label5_med_view);
        label6 = findViewById(R.id.label6_med_view);
        label7 = findViewById(R.id.label7_med_view);

        caja1 = findViewById(R.id.caja1_med_view);
        caja2 = findViewById(R.id.caja2_med_mod3);
        caja3 = findViewById(R.id.caja3_med_mod3);
        caja4 = findViewById(R.id.caja4_med_mod3);
        caja5 = findViewById(R.id.caja5_med_mod3);
        caja6 = findViewById(R.id.caja6_med_view);
        caja7 = findViewById(R.id.caja7_med_view);

        boton1 = findViewById(R.id.button1_med_view);
        boton2 = findViewById(R.id.button2_med_view);
        boton3 = findViewById(R.id.button3_med_view);
        boton4 = findViewById(R.id.button4_med_view);

        pasarPantallaMain = new Intent(MedicamentosView.this, MedicamentosActivity.class);

        // Recuperar el userId de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        deshabilitarEdicion();


        Intent intent = getIntent();
        Medicamento medicamentoItem = (Medicamento) intent.getSerializableExtra("medicamento");
        if (medicamentoItem != null) {
            caja1.setText(medicamentoItem.getNombre());
            caja2.setText(medicamentoItem.getDosis());
            caja3.setText(medicamentoItem.getDosisDia());
            caja4.setText(medicamentoItem.getDuracionTratamiento());
            caja5.setText(medicamentoItem.getHoraPrimeraDosis());
            caja6.setText(medicamentoItem.calcularSiguienteToma());
            caja7.setText(medicamentoItem.calcularFinTratamiento());
            dosisTomadas = medicamentoItem.getDosisTomadas();
            medID = medicamentoItem.getId();
            if(caja6.getText().toString().contains("No hay más tomas"))
                {
                    tratamientoFinalizado();
                }
        } else {
            Toast.makeText(this, "No se recibieron datos del medicamento", Toast.LENGTH_SHORT).show();
        }

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(pasarPantallaMain);
                finish();
            }
        });

        boton2.setOnClickListener(v -> {
            habilitarEdicion();
            boton2.setText("GUARDAR");
            boton2.setOnClickListener(z -> {
                updateMedicamento(medID, caja1.getText().toString(), caja2.getText().toString(), caja3.getText().toString(), dosisTomadas, caja4.getText().toString(), caja5.getText().toString());
            });
        });

        boton3.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MedicamentosView.this);
            builder.setMessage("¿Estás seguro de que quieres eliminar este medicamento?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", (dialog, id) -> {
                        // Si el medicamento tiene un id válido, ejecutamos el DELETE
                        if (medID != -1) {
                            deleteMedicamento(medID);
                        }
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });

        boton4.setOnClickListener(v -> {
            caja6.setText(medicamentoItem.tomarDosis());
            if(caja6.getText().toString().contains("No hay más tomas"))
            {
                tratamientoFinalizado();
                updateMedicamentoFin(medID, caja1.getText().toString(), caja2.getText().toString(), caja3.getText().toString(), dosisTomadas, caja4.getText().toString(), caja5.getText().toString());
            } else {
            updateMedicamento(medID, caja1.getText().toString(), caja2.getText().toString(), caja3.getText().toString(), dosisTomadas, caja4.getText().toString(), caja5.getText().toString());
            }
        });
    }

    private void deleteMedicamento(int medicamentoId) {
        OkHttpClient client = new OkHttpClient();

        // URL para el DELETE, reemplazando :medicamento_id con el ID real
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/medicamentos/" + medicamentoId;

        Request request = new Request.Builder()
                .url(url)
                .delete()  // Método DELETE
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MedicamentosView.this, "Error al eliminar medicamento", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MedicamentosView.this, "Medicamento eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();  // Regresar a la pantalla anterior después de la eliminación
                        startActivity(pasarPantallaMain);  // Redirigir si es necesario
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MedicamentosView.this, "Error al eliminar medicamento", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
    private void updateMedicamento(int medicamentoId, String nombre, String dosis, String dosisDia, int dosisTomadas, String duracionTratamiento, String horaPrimeraDosis) {
        JSONObject modMedicamentoJson = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        userIDdef=userId;
        try {
            modMedicamentoJson.put("user_id", userIDdef); // Asegúrate de pasar el user_id adecuado
            modMedicamentoJson.put("medicamento", caja1.getText().toString());
            modMedicamentoJson.put("dosis", caja2.getText().toString());
            modMedicamentoJson.put("dosisDia", caja3.getText().toString());
            modMedicamentoJson.put("dosisTomadas", dosisTomadas);
            modMedicamentoJson.put("duracionTratamiento", caja4.getText().toString());
            modMedicamentoJson.put("horaPrimeraDosis", caja5.getText().toString());

            Medicamento nuevoMedicamentoItem = new Medicamento(nombre, dosis, dosisDia, dosisTomadas, duracionTratamiento, horaPrimeraDosis, medicamentoId);
            nuevoMedicamentoItem.calcularSiguienteToma();
            nuevoMedicamentoItem.calcularFinTratamiento();

        } catch (JSONException e) {
            Toast.makeText(this, "Error al actualizar el medicamento", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        // Crear RequestBody
        RequestBody body = RequestBody.create(
                modMedicamentoJson.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // URL para el UPDATE, reemplazando :medicamento_id con el ID real
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/medicamentos/" + medicamentoId;

        Request request = new Request.Builder()
                .url(url)
                .put(body)  // Método DELETE
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MedicamentosView.this, "Error al modificar medicamento", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MedicamentosView.this, "Medicamento actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();  // Regresar a la pantalla anterior después de la eliminación
                            startActivity(pasarPantallaMain);  // Redirigir si es necesario
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MedicamentosView.this, "Error al modificar medicamento", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
    private void updateMedicamentoFin(int medicamentoId, String nombre, String dosis, String dosisDia, int dosisTomadas, String duracionTratamiento, String horaPrimeraDosis) {
        JSONObject modMedicamentoJson = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        userIDdef=userId;
        try {
            modMedicamentoJson.put("user_id", userIDdef); // Asegúrate de pasar el user_id adecuado
            modMedicamentoJson.put("medicamento", caja1.getText().toString());
            modMedicamentoJson.put("dosis", caja2.getText().toString());
            modMedicamentoJson.put("dosisDia", caja3.getText().toString());
            modMedicamentoJson.put("dosisTomadas", dosisTomadas);
            modMedicamentoJson.put("duracionTratamiento", caja4.getText().toString());
            modMedicamentoJson.put("horaPrimeraDosis", caja5.getText().toString());

            Medicamento nuevoMedicamentoItem = new Medicamento(nombre, dosis, dosisDia, dosisTomadas, duracionTratamiento, horaPrimeraDosis, medicamentoId);
            nuevoMedicamentoItem.calcularSiguienteToma();
            nuevoMedicamentoItem.calcularFinTratamiento();

        } catch (JSONException e) {
            Toast.makeText(this, "Error al actualizar el medicamento", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        // Crear RequestBody
        RequestBody body = RequestBody.create(
                modMedicamentoJson.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // URL para el UPDATE, reemplazando :medicamento_id con el ID real
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/medicamentos/" + medicamentoId;

        Request request = new Request.Builder()
                .url(url)
                .put(body)  // Método DELETE
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MedicamentosView.this, "Error al modificar medicamento", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MedicamentosView.this, "Final del tratamiento, por favor borre el medicamento", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MedicamentosView.this, "Error al modificar medicamento", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void tratamientoFinalizado(){
        caja1.setEnabled(false);
        caja2.setEnabled(false);
        caja3.setEnabled(false);
        caja4.setEnabled(false);
        caja5.setEnabled(false);
        caja6.setEnabled(false);
        caja7.setEnabled(false);
        boton4.setEnabled(false);
        boton2.setEnabled(false);
        boton1.setEnabled(false);
    }
    private void habilitarEdicion() {
        caja1.setEnabled(true);
        caja1.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);
        caja2.setEnabled(true);
        caja2.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);
        caja3.setEnabled(true);
        caja3.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);
        caja4.setEnabled(true);
        caja4.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);
        caja5.setEnabled(true);
        caja5.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);
        caja6.setEnabled(true);
        caja7.setEnabled(true);
        boton4.setEnabled(false);
        boton3.setEnabled(false);
    }
    private void deshabilitarEdicion() {
        caja1.setEnabled(false);
        caja2.setEnabled(false);
        caja3.setEnabled(false);
        caja4.setEnabled(false);
        caja5.setEnabled(false);
        caja6.setEnabled(false);
        caja7.setEnabled(false);
        boton4.setEnabled(true);
        boton3.setEnabled(true);
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
