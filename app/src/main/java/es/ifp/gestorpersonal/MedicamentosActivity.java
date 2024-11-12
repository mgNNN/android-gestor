package es.ifp.gestorpersonal;


import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MedicamentosActivity extends AppCompatActivity {

    private Intent pasarPantallaMain;
    private Intent pasarPantallaAddMed;
    private Intent pasarPantallaMed;
    private Intent pasarPantallaViewMed;
    protected ListView medicamentosList;
    private ArrayList<String> meds = new ArrayList<String>();
    private ArrayList<Medicamento> medsInfo = new ArrayList<Medicamento>();
    private ArrayAdapter<String> adaptador;
    private Bundle extras;
    protected int userIDdef;
    private String medNombre;
    private String medDosis;
    private String medNumTomas;
    private String medDuracion;
    private String medHoraDosis1;

    private String contenidoItem = "";
    private OkHttpClient client;


    private static final String BASE_URL =
            "https://gestor-personal-4898737da4af.herokuapp.com/medicamentos/";
    private ActivityResultLauncher<Intent> addMedLauncher;

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
        addMedLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Recargar productos si el resultado fue OK
                        loadMeds();
                    }
                }
        );
        client = new OkHttpClient();
        medicamentosList = findViewById(R.id.lista1_med);

        // Recupera el userId de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // El valor -1 es por si no existe un userId guardado
        userIDdef=userId;

        //Toast.makeText(this, String.valueOf(userIDdef), Toast.LENGTH_SHORT).show();



        //meds = db.getAllMedicamentos();
        adaptador = new ArrayAdapter<String>(MedicamentosActivity.this, android.R.layout.simple_list_item_1, meds);
        medicamentosList.setAdapter(adaptador);

        loadMeds();
        //addButton.setOnClickListener(v -> {
         //   Intent intent = new Intent(this, ShoppingActivityAdd.class);
         //   addProductLauncher.launch(intent); // Usa el launcher en lugar de startActivityForResult
        //});
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
            //db.insertMedicamento(medNombre, medDosis, medNumTomas, medDuracion, medHoraDosis1);

            startActivity(pasarPantallaMed);
            pasarPantallaMed.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        }

        medicamentosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //contenidoItem = parent.getItemAtPosition(position).toString();
                if (position < medsInfo.size()) {
                Medicamento medicamentoItem = medsInfo.get(position);
                pasarPantallaViewMed = new Intent(MedicamentosActivity.this,MedicamentosView.class);
                pasarPantallaViewMed.putExtra("medicamento", medicamentoItem);

//                pasarPantallaViewMed.putExtra("medicamento", medsInfo.get(0));
//                pasarPantallaViewMed.putExtra("dosis", medsInfo.get(1));
//                pasarPantallaViewMed.putExtra("dosisDia", contenidoItem);
//                pasarPantallaViewMed.putExtra("duracionTratamiento", medsInfo);
//                pasarPantallaViewMed.putExtra("horaPrimeraDosis", medsInfo);
//                pasarPantallaViewMed.putExtra("id", contenidoItem);
                startActivity(pasarPantallaViewMed);
                } else {
                    Toast.makeText(MedicamentosActivity.this, "Índice no válido", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void loadMeds() {


        Request request = new Request.Builder()
                .url(BASE_URL+userIDdef)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MedicamentosActivity.this,
                        "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        meds.clear();
                        medsInfo.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject med = jsonArray.getJSONObject(i);
                            String medic = med.getString("medicamento");
                            String dosis = med.getString("dosis");
                            String dosisDia = med.getString("dosisDia");
                            String duracionTratamiento = med.getString("duracionTratamiento");
                            String horaPrimeraDosis = med.getString("horaPrimeraDosis");
                            String itemId = med.getString("id");
                            meds.add(medic);
//                            medsInfo.add(medic);
//                            medsInfo.add(dosis);
//                            medsInfo.add(dosisDia);
//                            medsInfo.add(duracionTratamiento);
//                            medsInfo.add(horaPrimeraDosis);
//                           medsInfo.add(itemId);
                            Medicamento medicamento = new Medicamento(medic, dosis, dosisDia, duracionTratamiento, horaPrimeraDosis, itemId);
                            medsInfo.add(medicamento);
                        }
                        runOnUiThread(() -> adaptador.notifyDataSetChanged());
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(MedicamentosActivity.this,
                                "Error de datos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MedicamentosActivity.this,
                            "Error en el servidor: " + response.message(),
                            Toast.LENGTH_SHORT).show());
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


