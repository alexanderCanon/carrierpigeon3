package com.principal.cp.maestros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

public class GestionNotasActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtMateria, edtNota;
    private Button btnRegistrarNota, btnActualizarNota, btnEliminarNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtMateria = findViewById(R.id.edtMateria);
        edtNota = findViewById(R.id.edtNota);
        btnRegistrarNota = findViewById(R.id.btnRegistrarNota);
        btnActualizarNota = findViewById(R.id.btnActualizarNota);
        btnEliminarNota = findViewById(R.id.btnEliminarNota);

        // Registrar una nueva nota
        btnRegistrarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String materia = edtMateria.getText().toString();
            String nota = edtNota.getText().toString();

            registrarNota(alumnoID, materia, nota);
        });

        // Actualizar una nota existente
        btnActualizarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String materia = edtMateria.getText().toString();
            String nota = edtNota.getText().toString();

            actualizarNota(alumnoID, materia, nota);
        });

        // Eliminar una nota
        btnEliminarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String materia = edtMateria.getText().toString();

            eliminarNota(alumnoID, materia);
        });
    }

    private void registrarNota(String alumnoID, String materia, String nota) {
        // Aquí iría la lógica para enviar los datos al servidor PHP
    }

    private void actualizarNota(String alumnoID, String materia, String nota) {
        // Aquí iría la lógica para actualizar la nota en la base de datos
    }

    private void eliminarNota(String alumnoID, String materia) {
        // Aquí iría la lógica para eliminar la nota en la base de datos
    }
}
