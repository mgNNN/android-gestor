package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ShoppingActivityAdd extends AppCompatActivity {

    private EditText productNameInput;
    private OkHttpClient client;
    private static final String BASE_URL =
            "https://gestor-personal-4898737da4af.herokuapp.com/products/";
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.get("application/json; charset=utf-8");
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_add);

        productNameInput = findViewById(R.id.editTextItem);
        Button addProductButton = findViewById(R.id.saveButton);
        Button backButton = findViewById(R.id.backButton);

        client = new OkHttpClient();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        addProductButton.setOnClickListener(v -> addProductToBackend());
        backButton.setOnClickListener(v -> finish());
    }

    private void addProductToBackend() {
        String productName = productNameInput.getText().toString();

        if (productName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre para el producto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("name", productName);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en la creación del JSON",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder().url(BASE_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ShoppingActivityAdd.this,
                        "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ShoppingActivityAdd.this,
                                "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Indica que el producto se añadió
                        finish(); // Cierra ShoppingActivityAdd
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ShoppingActivityAdd.this,
                            "Error en el servidor: " + response.message(),
                            Toast.LENGTH_SHORT).show());
                }
            }

        });
    }
}

