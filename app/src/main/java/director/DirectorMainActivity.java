package director;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.principal.cp.R;
import com.principal.cp.LoginActivity;

public class DirectorMainActivity extends AppCompatActivity {
    Button btnAlumnos, btnPadres, btnMaestros;
    private BottomNavigationView bottomNavigationView;
    private Button btnCerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_director_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        /*btnAlumnos = findViewById(R.id.btnAlumnos);
        btnPadres = findViewById(R.id.btnPadres);
        btnMaestros = findViewById(R.id.btnMaestros);*/

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        //Jala el nombre de quien se logueó
        TextView tvWelcome = findViewById(R.id.tvWelcome);

        // Obtener datos de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombre = preferences.getString("nombre", "Usuario"); // Valor por defecto si no encuentra nada
        String apellido = preferences.getString("apellido", "");
        // Mostrar mensaje personalizado
        tvWelcome.setText("Bienvenido, " + nombre + " " + apellido);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_alumnos) {
                startActivity(new Intent(this, GestionAlumnosActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            } else if (id == R.id.nav_padres) {
                startActivity(new Intent(this, GestionPadresActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            } else if (id == R.id.nav_maestros) {
                startActivity(new Intent(this, GestionMaestrosActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }

            return false;
        });

    } //fin onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // metodo 1, inflar el menu
        getMenuInflater().inflate(R.menu.menu_toolbar_director, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //metodo 2, manejar el click del icono
        if (item.getItemId() == R.id.action_logout) {
            getSharedPreferences("session", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}