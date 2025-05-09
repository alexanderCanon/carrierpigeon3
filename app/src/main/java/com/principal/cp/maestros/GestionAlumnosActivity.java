package com.principal.cp.maestros;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

public class GestionAlumnosActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtNombreAlumno, edtApellidoAlumno, edtTelefonoAlumno;
    private Button btnRegistrarAlumno, btnActualizarAlumno, btnEliminarAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_alumnos);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtNombreAlumno = findViewById(R.id.edtNombreAlumno);
        edtApellidoAlumno = findViewById(R.id.edtApellidoAlumno);
        edtTelefonoAlumno = findViewById(R.id.edtTelefonoAlumno);
        btnRegistrarAlumno = findViewById(R.id.btnRegistrarAlumno);
        btnActualizarAlumno = findViewById(R.id.btnActualizarAlumno);
        btnEliminarAlumno = findViewById(R.id.btnEliminarAlumno);

        // Registrar un nuevo alumno
        btnRegistrarAlumno.setOnClickListener(v -> {
            String nombre = edtNombreAlumno.getText().toString();
            String apellido = edtApellidoAlumno.getText().toString();
            String telefono = edtTelefonoAlumno.getText().toString();

            registrarAlumno(nombre, apellido, telefono);
        });

        // Actualizar información de un alumno
        btnActualizarAlumno.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String nombre = edtNombreAlumno.getText().toString();
            String apellido = edtApellidoAlumno.getText().toString();
            String telefono = edtTelefonoAlumno.getText().toString();

            actualizarAlumno(alumnoID, nombre, apellido, telefono);
        });

        // Eliminar un alumno
        btnEliminarAlumno.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            eliminarAlumno(alumnoID);
        });
    }

    private void registrarAlumno(String nombre, String apellido, String telefono) {
        // Aquí iría la lógica para enviar los datos al servidor PHP (utilizando AsyncTask, Retrofit o alguna otra librería)
        Toast.makeText(this, "Alumno registrado: " + nombre, Toast.LENGTH_SHORT).show();
    }

    private void actualizarAlumno(String alumnoID, String nombre, String apellido, String telefono) {
        // Aquí iría la lógica para actualizar el alumno en la base de datos a través de PHP
        Toast.makeText(this, "Alumno actualizado: " + nombre, Toast.LENGTH_SHORT).show();
    }

    private void eliminarAlumno(String alumnoID) {
        // Aquí iría la lógica para eliminar el alumno en la base de datos a través de PHP
        Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show();
    }
}
