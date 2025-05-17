package com.principal.cp.maestros;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GestionAsistenciaActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtFecha, edtAsistencia, edtObservaciones;
    private Button btnRegistrarAsistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_asistencia);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtFecha = findViewById(R.id.edtFecha);
        edtAsistencia = findViewById(R.id.edtAsistencia);
        edtObservaciones = findViewById(R.id.edtObservaciones);
        btnRegistrarAsistencia = findViewById(R.id.btnRegistrarAsistencia);

        btnRegistrarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alumnoID = edtAlumnoID.getText().toString();
                String fecha = edtFecha.getText().toString();
                String asistencia = edtAsistencia.getText().toString(); // "1" o "0"
                String observaciones = edtObservaciones.getText().toString();

                registrarAsistencia(alumnoID, fecha, asistencia, observaciones);
            }
        });

        Button btnEditarAsistencia = findViewById(R.id.btnEditarAsistencia);
        btnEditarAsistencia.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String fecha = edtFecha.getText().toString();
            String asistencia = edtAsistencia.getText().toString();
            String observaciones = edtObservaciones.getText().toString();

            editarAsistencia(alumnoID, fecha, asistencia, observaciones);
        });

        Button btnVolverMaestros = findViewById(R.id.btnVolverMaestros);
        btnVolverMaestros.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAsistenciaActivity.this, MaestroMainActivity.class);
            startActivity(intent);
            finish(); // opcional, para cerrar la activity actual
        });


    }

    private void registrarAsistencia(String alumnoID, String fecha, String asistencia, String observaciones) {
        String data = "id_alumno=" + alumnoID +
                "&fecha=" + fecha +
                "&presente=" + asistencia +
                "&observaciones=" + observaciones;
        new EnviarAsistenciaTask("http://34.122.138.135/registrar_asistencia.php").execute(data);
    }

    private void editarAsistencia(String alumnoID, String fecha, String asistencia, String observaciones) {
        String data = "id_alumno=" + alumnoID +
                "&fecha=" + fecha +
                "&presente=" + asistencia +
                "&observaciones=" + observaciones;
        new EnviarAsistenciaTask("http://34.122.138.135/editar_asistencia.php").execute(data);
    }

    private class EnviarAsistenciaTask extends AsyncTask<String, Void, String> {
        private final String url;

        public EnviarAsistenciaTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(params[0]);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(GestionAsistenciaActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
