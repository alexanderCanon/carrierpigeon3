package com.principal.cp.maestros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

public class GestionAsistenciaActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtFecha, edtAsistencia;
    private Button btnRegistrarAsistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_asistencia);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtFecha = findViewById(R.id.edtFecha);
        edtAsistencia = findViewById(R.id.edtAsistencia);
        btnRegistrarAsistencia = findViewById(R.id.btnRegistrarAsistencia);

        btnRegistrarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para registrar la asistencia (enviar datos a PHP)
                String alumnoID = edtAlumnoID.getText().toString();
                String fecha = edtFecha.getText().toString();
                String asistencia = edtAsistencia.getText().toString();

                // Llamar a una función para enviar estos datos al servidor PHP
                registrarAsistencia(alumnoID, fecha, asistencia);
            }
        });
    }

    private void registrarAsistencia(String alumnoID, String fecha, String asistencia) {
        // Aquí podrías usar una API o un AsyncTask para hacer una solicitud HTTP POST
        // a un script PHP para registrar la asistencia en la base de datos.
    }
}
