package director;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;

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
    }
}