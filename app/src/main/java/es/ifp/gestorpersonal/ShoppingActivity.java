package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
    private int selectedPosition = -1;
    private OkHttpClient client;
    private static final String BASE_URL =
            "https://gestor-personal-4898737da4af.herokuapp.com/products";

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

        client = new OkHttpClient();
        productList = findViewById(R.id.product_list);
        Button addButton = findViewById(R.id.addButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        productList.setAdapter(adapter);

        loadProducts();

        addButton.setOnClickListener(v -> startActivity(new Intent(this,
                ShoppingActivityAdd.class)));
        editButton.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                Intent intent = new Intent(this, ShoppingActivityEdit.class);
                intent.putExtra("PRODUCT_POSITION", selectedPosition);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Selecciona un producto para modificar",
                        Toast.LENGTH_SHORT).show();
            }
        });
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                Intent intent = new Intent(this, ShoppingActivityEdit.class);
                intent.putExtra("PRODUCT_POSITION", selectedPosition);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Selecciona un producto para eliminar",
                        Toast.LENGTH_SHORT).show();
            }
        });

        productList.setOnItemClickListener((parent, view, position, id)
                -> selectedPosition = position);
    }

    private void loadProducts() {
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
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        products.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            String name = product.getString("name");
                            products.add(name);
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(ShoppingActivity.this,
                                "Error de datos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ShoppingActivity.this,
                            "Error en el servidor: " + response.message(),
                            Toast.LENGTH_SHORT).show());
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
        if (item.getItemId() == R.id.menu_back) {
            finish();
        } else if (item.getItemId() == R.id.menu_close) {
            System.exit(0);
        } else if (item.getItemId() == R.id.menu_add) {
            startActivity(new Intent(this, ShoppingActivityAdd.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

