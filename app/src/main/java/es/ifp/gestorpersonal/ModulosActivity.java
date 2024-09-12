package es.ifp.gestorpersonal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModulosActivity extends AppCompatActivity {

    private Button boton1;
    private TextView label1;
    protected String usuario;
    protected Intent calendarIntent;
    protected ImageButton gimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modulos);

        // Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boton1 = findViewById(R.id.boton1_modulo);
        label1 = findViewById(R.id.label1_modulos);
        gimButton = findViewById(R.id.botongim_modulos);

        // Obtener el nombre de usuario
        Intent intent = getIntent();
        usuario = intent.getStringExtra("username");

        // En ModulosActivity: usar "show_welcome" en vez de "first_time"
        SharedPreferences preferences = getSharedPreferences("modulos_preferences", MODE_PRIVATE);
        boolean showWelcome = preferences.getBoolean("show_welcome", true);

        // Mostrar el mensaje solo si show_welcome es true
        if (showWelcome) {
            label1.setText("Bienvenido " + usuario);

            // Actualizar el estado para que no se muestre el mensaje la próxima vez
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("show_welcome", false);  // Cambiar a false después de mostrar el mensaje
            editor.apply();
        } else {
            label1.setText("");  // O puedes poner otro texto si lo deseas
        }

        // Acción del botón para ir a la pantalla de Calendario
        boton1.setOnClickListener(v -> {
            calendarIntent = new Intent(ModulosActivity.this, CalendarioActivity.class);
            startActivity(calendarIntent);
            finish();  // Finaliza esta actividad para que no vuelva a la pila
        });

        gimButton.setOnClickListener(v -> {
            calendarIntent = new Intent(ModulosActivity.this, GimnasioActivity.class);
            startActivity(calendarIntent);
            finish();  // Finaliza esta actividad para que no vuelva a la pila
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_salir_login) {
            finishAffinity();
            System.exit(0);
        } else if (id == R.id.item_offsesion_modules) {
            // Limpiar las preferencias compartidas o cualquier dato de sesión
            SharedPreferences preferences = getSharedPreferences("modulos_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();  // Limpiar todas las preferencias
            editor.apply();

            // Redirigir al usuario a la pantalla de login
            Intent loginIntent = new Intent(ModulosActivity.this, LoginActivity.class); // Cambia a tu clase de login
            startActivity(loginIntent);
            finish();  // Finaliza esta actividad para que no vuelva a la pila
        }
        return super.onOptionsItemSelected(item);
    }
}
