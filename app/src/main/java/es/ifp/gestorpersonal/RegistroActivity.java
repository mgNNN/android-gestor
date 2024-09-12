package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistroActivity extends AppCompatActivity {

    protected TextView label1;
    protected TextView label2;
    protected TextView label3;
    protected EditText usuario;
    protected EditText contraseña;
    protected EditText correo;
    protected EditText telefono; // Añadido campo de teléfono
    protected Button volverButton;
    protected Button registerButton;

    private OkHttpClient client;
    private Intent pasarPantalla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        label1 = findViewById(R.id.label1_register);
        label2 = findViewById(R.id.label2_register);
        label3 = findViewById(R.id.label3_register);
        usuario = findViewById(R.id.caja1_register);
        contraseña = findViewById(R.id.caja2_register);
        correo = findViewById(R.id.caja3_register);
        telefono = findViewById(R.id.caja4_register); // Asigna el campo de teléfono
        volverButton = findViewById(R.id.boton1_register);
        registerButton = findViewById(R.id.boton2_register);

        client = new OkHttpClient(); // Inicializa el cliente HTTP

        volverButton.setOnClickListener(v -> {
            pasarPantalla = new Intent(RegistroActivity.this, LoginActivity.class);
            finish();
            startActivity(pasarPantalla);
        });

        registerButton.setOnClickListener(v -> {
            String username = usuario.getText().toString();
            String password = contraseña.getText().toString();
            String email = correo.getText().toString();
            String phone = telefono.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                register(username, password, email, phone); // Llamar al método de registro
            } else {
                Toast.makeText(RegistroActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(String username, String password, String email, String phone) {
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/register";

        // Crear el objeto JSON para enviar
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("email", email);
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegistroActivity.this, "Error en la creación del JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el cuerpo de la solicitud
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        // Crear la solicitud
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Enviar la solicitud de manera asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RegistroActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> handleRegisterResponse(responseBody));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(RegistroActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void handleRegisterResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("message")) {
                String message = json.getString("message");
                Toast.makeText(RegistroActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistroActivity.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegistroActivity.this, "Error en el análisis de la respuesta", Toast.LENGTH_SHORT).show();
        }
    }
}
