package director;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;
import com.principal.cp.LoginActivity;

public class DirectorMainActivity extends AppCompatActivity {
    Button btnAlumnos, btnPadres, btnMaestros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_director_main);
        btnAlumnos = findViewById(R.id.btnAlumnos);
        btnPadres = findViewById(R.id.btnPadres);
        btnMaestros = findViewById(R.id.btnMaestros);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Jala el nombre de quien se logueó
        TextView tvWelcome = findViewById(R.id.tvWelcome);

        // Obtener datos de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombre = preferences.getString("nombre", "Usuario"); // Valor por defecto si no encuentra nada
        String apellido = preferences.getString("apellido", "");
        // Mostrar mensaje personalizado
        tvWelcome.setText("Bienvenido, " + nombre + " " + apellido);

        // Eventos
        btnAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorMainActivity.this, GestionAlumnosActivity.class);
                startActivity(intent);
            }
        });

        btnPadres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorMainActivity.this, GestionPadresActivity.class);
                startActivity(intent);
            }
        });

        btnMaestros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorMainActivity.this, GestionMaestrosActivity.class);
                startActivity(intent);
            }
        });

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            // Aquí limpias la sesión y vuelves al LoginActivity
            //SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Borra todo
            editor.apply();

            Intent intent = new Intent(DirectorMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Cierra todas las actividades previas
            startActivity(intent);
        });
    }
}