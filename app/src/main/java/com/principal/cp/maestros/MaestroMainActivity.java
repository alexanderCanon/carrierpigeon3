package com.principal.cp.maestros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

public class MaestroMainActivity extends AppCompatActivity {

    private Button btnAlumnos, btnNotas, btnAsistencia, btnActividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_main);

        // Referencias a los botones
        btnAlumnos = findViewById(R.id.btnAlumnos);
        btnNotas = findViewById(R.id.btnNotas);
        btnAsistencia = findViewById(R.id.btnAsistencia);
        btnActividades = findViewById(R.id.btnActividades);

        // Evento de click para cada botón
        btnAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroMainActivity.this, GestionAlumnosActivity.class);
                startActivity(intent);
            }
        });

        btnNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroMainActivity.this, GestionNotasActivity.class);
                startActivity(intent);
            }
        });

        btnAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroMainActivity.this, GestionAsistenciaActivity.class);
                startActivity(intent);
            }
        });

        btnActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroMainActivity.this, GestionActividadesActivity.class);
                startActivity(intent);
            }
        });
    }
}
