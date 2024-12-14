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

public class ShoppingActivityEdit extends AppCompatActivity {

    private EditText productNameInput;
    private Button updateButton, deleteButton, backButton;
    private OkHttpClient client;
    private int productId;
    private static final String BASE_URL =
            "https://gestor-personal-4898737da4af.herokuapp.com/products/";
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.get("application/json; charset=utf-8");


    private static final String ERROR_RED = "Error de red: ";
    private static final String ERROR_JSON = "Error al crear la información del producto";
    private static final String PRODUCTO_EDITADO = "Producto editado correctamente";
    private static final String PRODUCTO_ELIMINADO = "Producto eliminado correctamente";
    private static final String ERROR_SERVIDOR = "Error en el servidor: ";
    private static final String ERROR_PRODUCTO_NO_ENCONTRADO = "Producto no encontrado";
    private static final String ERROR_ID_INVALIDO = "ID de producto inválido";
    private static final String ERROR_NOMBRE_VACIO = "El nombre del producto no puede estar vacío";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_edit);

        productNameInput = findViewById(R.id.editTextItem);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        client = new OkHttpClient();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        if (!initializeProductDetails()) {
            return;
        }

        updateButton.setOnClickListener(v -> editProduct());
        deleteButton.setOnClickListener(v -> deleteProduct());
        backButton.setOnClickListener(v -> finish());
    }
     private boolean initializeProductDetails() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("PRODUCT_ID")) {
            mostrarMensaje(ERROR_PRODUCTO_NO_ENCONTRADO);
            finish();
            return false;
        }

        productId = intent.getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            mostrarMensaje(ERROR_ID_INVALIDO);
            finish();
            return false;
        }

        String productName = intent.getStringExtra("PRODUCT_NAME");
        productNameInput.setText(productName);
        return true;
    }

    private void editProduct() {
        String productName = productNameInput.getText().toString().trim();
        if (productName.isEmpty()) {
            mostrarMensaje(ERROR_NOMBRE_VACIO);
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("name", productName);
        } catch (JSONException e) {
            mostrarMensaje(ERROR_JSON);
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(BASE_URL + productId)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mostrarMensaje(ERROR_RED + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    mostrarMensaje(PRODUCTO_EDITADO);
                    finish();
                } else {
                    mostrarMensaje(ERROR_SERVIDOR + response.message());
                }
            }
        });
    }

    private void deleteProduct() {
        Request request = new Request.Builder()
                .url(BASE_URL + productId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mostrarMensaje(ERROR_RED + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    mostrarMensaje(PRODUCTO_ELIMINADO);
                    finish();
                } else {
                    mostrarMensaje(ERROR_SERVIDOR + response.message());
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        runOnUiThread(() -> Toast.makeText(ShoppingActivityEdit.this, mensaje,
                Toast.LENGTH_SHORT).show());
    }
}


