package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GimnasioActivity extends AppCompatActivity {

    protected Intent pasarPantalla;
    protected Button boton_rutina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gimnasio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        boton_rutina = findViewById(R.id.boton1_rutina);

        boton_rutina.setOnClickListener(v -> {
            pasarPantalla = new Intent(GimnasioActivity.this, RutinaActivity.class);
            finish();
            startActivity(pasarPantalla);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_salir_gim) {
            finishAffinity();
            System.exit(0);
        } else if (id == R.id.menu_volver_gim) {
            pasarPantalla = new Intent(GimnasioActivity.this, ModulosActivity.class);
            finish();
            startActivity(pasarPantalla);
        }
        return super.onOptionsItemSelected(item);
    }
}