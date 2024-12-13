package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ShoppingActivity extends AppCompatActivity {

    private ListView productList;
    private ArrayList<String> products = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OkHttpClient client;
    private static final String BASE_URL =
            "https://gestor-personal-4898737da4af.herokuapp.com/products";
    private ActivityResultLauncher<Intent> addProductLauncher;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Registro de actividad para agregar un producto
        addProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadProducts(); // Recargar productos si el resultado fue OK
                    }
                }
        );

        client = new OkHttpClient();
        productList = findViewById(R.id.product_list);

        // Recuperar el userId de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        productList.setAdapter(adapter);

        loadProducts();

        productList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = products.get(position);

            // Crea un Intent para ir a ShoppingActivityEdit
            Intent intent = new Intent(ShoppingActivity.this, ShoppingActivityEdit.class);
            intent.putExtra("product_name", selectedProduct);  // Pasa el nombre del producto
            intent.putExtra("product_position", position);  // Pasa la posición del producto
            startActivity(intent);  // Inicia la actividad
        });
    }

    // Método para cargar los productos desde la API
    public void loadProducts() {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ShoppingActivity.this,
                        "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ShoppingActivity", "Response Body: " + responseBody); // Para debug
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        products.clear();  // Limpiar lista actual
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            String name = product.getString("name");
                            products.add(name); // Añadir nombre del producto a la lista
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());  // Actualizar la UI con los nuevos productos
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(ShoppingActivity.this,
                                "Error de datos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ShoppingActivity.this,
                            "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_back) {
            startActivity(new Intent(this, ModulosActivity.class));
        } else if (id == R.id.menu_close) {
            System.exit(0);
        } else if (id == R.id.menu_add) {
            startActivity(new Intent(this, ShoppingActivityAdd.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
