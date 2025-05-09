package com.principal.cp.maestros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

public class GestionActividadesActivity extends AppCompatActivity {

    private EditText edtTituloActividad, edtDescripcionActividad, edtFechaActividad;
    private Button btnRegistrarActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_actividades);

        edtTituloActividad = findViewById(R.id.edtTituloActividad);
        edtDescripcionActividad = findViewById(R.id.edtDescripcionActividad);
        edtFechaActividad = findViewById(R.id.edtFechaActividad);
        btnRegistrarActividad = findViewById(R.id.btnRegistrarActividad);

        btnRegistrarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para registrar la actividad (enviar datos a PHP)
                String titulo = edtTituloActividad.getText().toString();
                String descripcion = edtDescripcionActividad.getText().toString();
                String fecha = edtFechaActividad.getText().toString();

                // Llamar a una función para enviar estos datos al servidor PHP
                registrarActividad(titulo, descripcion, fecha);
            }
        });
    }

    private void registrarActividad(String titulo, String descripcion, String fecha) {
        // Aquí podrías usar una API o un AsyncTask para hacer una solicitud HTTP POST
        // a un script PHP para registrar la actividad en la base de datos.
    }
}
