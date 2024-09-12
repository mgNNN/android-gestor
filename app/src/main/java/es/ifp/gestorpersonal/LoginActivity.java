package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class LoginActivity extends AppCompatActivity {

    protected EditText usuario;
    protected EditText contraseña;
    protected Button loginButton;
    protected Button registerButton;
    private OkHttpClient client;
    private Intent pasarPantalla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = findViewById(R.id.caja1_main);
        contraseña = findViewById(R.id.caja2_main);
        loginButton = findViewById(R.id.boton1_main);
        registerButton = findViewById(R.id.boton2_main);

        client = new OkHttpClient();

        loginButton.setOnClickListener(v -> {
            String username = usuario.getText().toString();
            String password = contraseña.getText().toString();
            login(username, password);
        });

        registerButton.setOnClickListener(v -> {
            pasarPantalla = new Intent(LoginActivity.this, RegistroActivity.class);
            finish();
            startActivity(pasarPantalla);
            Toast.makeText(LoginActivity.this, "Pantalla de registro", Toast.LENGTH_SHORT).show();
        });

    }

    private void login(String username, String password) {
        // URL de la app en Heroku
        String url = "https://gestor-personal-4898737da4af.herokuapp.com/login";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error en la creación del JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> handleResponse(responseBody));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void handleResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("token")) {
                String token = json.getString("token");

                // Mostrar mensaje de login exitoso
                Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                // En LoginActivity: guardar "show_welcome" como true después del login exitoso.
                SharedPreferences preferences = getSharedPreferences("modulos_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("show_welcome", true);  // Se asegura de que el mensaje de bienvenida se muestre en ModulosActivity
                editor.apply();


                // Navegar a la nueva pantalla (ModulosActivity)
                Intent intent = new Intent(LoginActivity.this, ModulosActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", usuario.getText().toString());
                finish();
                startActivity(intent);

            } else if (json.has("error")) {
                String message = json.getString("error");
                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error en el análisis de la respuesta", Toast.LENGTH_SHORT).show();
        }
    }
}
