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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
    private ArrayList<Integer> productIds = new ArrayList<>(); // Lista de IDs de productos
    private ArrayAdapter<String> adapter;
    private OkHttpClient client;
    private static final String BASE_URL = "https://gestor-personal-4898737da4af.herokuapp.com/products/";
    private ActivityResultLauncher<Intent> addProductLauncher;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

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
            return;
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        productList.setAdapter(adapter);

        loadProducts();  // Cargar los productos

        productList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = products.get(position);
            int productId = productIds.get(position); // Obtener el ID del producto

            // Verificar que el ID es válido
            Log.d("ShoppingActivity", "Producto seleccionado: " + selectedProduct + " con ID: " + productId);

            // Crea un Intent para ir a ShoppingActivityEdit
            Intent intent = new Intent(ShoppingActivity.this, ShoppingActivityEdit.class);
            intent.putExtra("PRODUCT_NAME", selectedProduct);
            intent.putExtra("PRODUCT_ID", productId);  // Asegúrate de que el ID se pasa correctamente
            startActivity(intent);  // Inicia la actividad
        });
    }

    // Método para cargar los productos desde la API
    public void loadProducts() {
        // Construir la URL con el userId para obtener productos específicos del usuario
        String url = BASE_URL + userId;

        Request request = new Request.Builder()
                .url(url)
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
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        products.clear();  // Limpiar lista actual
                        productIds.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            String name = product.getString("name");
                            int productId = product.getInt("id");
                            products.add(name);
                            productIds.add(productId);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_back) {
            startActivity(new Intent(this, ModulosActivity.class));
        } else if (id == R.id.menu_close) {
            System.exit(0);
        } else if (id == R.id.menu_add) {
            Intent intent = new Intent(this, ShoppingActivityAdd.class);
            addProductLauncher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
