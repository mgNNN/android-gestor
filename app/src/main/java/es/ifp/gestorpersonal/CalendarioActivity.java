package es.ifp.gestorpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalendarioActivity extends AppCompatActivity {

    protected Button boton1;
    protected Intent pasarPantalla;
    protected CalendarView calendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendario);

        // Configuración de Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boton1 = findViewById(R.id.boton1_calendario);
        calendario = findViewById(R.id.calendar_view);

        boton1.setOnClickListener(v -> {
            pasarPantalla = new Intent(CalendarioActivity.this, ModulosActivity.class);
            startActivity(pasarPantalla);
            finish(); // Finalizar la actividad
        });
    }
}
