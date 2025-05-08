package com.principal.cp;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MaestroMainActivity extends AppCompatActivity {

    private Button buttonLogout;
    private SharedPreferences sharedPreferences; //nos permite guardar datos en el dispositivo para la sesión activa
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_main); // Reemplaza con tu layout
        //aquí para abajo es el bloque del boton para cerrar sesión
        buttonLogout = findViewById(R.id.buttonLogout);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isLoggedIn", false);
                editor.remove("user_id");
                editor.remove("tipo_usuario");
                editor.remove("nombre");
                editor.remove("apellido");
                editor.remove("telefono");
                editor.apply();

                Intent intent = new Intent(MaestroMainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }); //aquí termina el bloque del boton para cerrar sesión
    }
}