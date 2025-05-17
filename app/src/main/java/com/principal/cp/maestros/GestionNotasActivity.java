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

public class GestionNotasActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtActividadID, edtNota;
    private Button btnRegistrarNota, btnActualizarNota, btnEliminarNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtActividadID = findViewById(R.id.edtActividadID);
        edtNota = findViewById(R.id.edtNota);
        btnRegistrarNota = findViewById(R.id.btnRegistrarNota);
        btnActualizarNota = findViewById(R.id.btnActualizarNota);
        btnEliminarNota = findViewById(R.id.btnEliminarNota);

        // Registrar una nueva nota
        btnRegistrarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String actividad = edtActividadID.getText().toString();
            String nota = edtNota.getText().toString();

            registrarNota(alumnoID, actividad, nota);
        });

        // Actualizar una nota existente
        btnActualizarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String actividad = edtActividadID.getText().toString();
            String nota = edtNota.getText().toString();

            actualizarNota(alumnoID, actividad, nota);
        });

        // Eliminar una nota
        btnEliminarNota.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String actividad = edtActividadID.getText().toString();

            eliminarNota(alumnoID, actividad);
        });
        Button btnVolverMaestros = findViewById(R.id.btnVolverMaestros);
        btnVolverMaestros.setOnClickListener(v -> {
            Intent intent = new Intent(GestionNotasActivity.this, MaestroMainActivity.class);
            startActivity(intent);
            finish(); // opcional, para cerrar la activity actual
        });

    }

    private void registrarNota(String alumnoID, String actividadID, String nota) {
        String observaciones = ""; // podrías añadir un campo EditText si quieres
        String data = "id_alumno=" + alumnoID +
                "&id_actividad=" + actividadID +
                "&valor_obtenido=" + nota +
                "&observaciones=" + observaciones;
        new EnviarNotaTask("http://34.122.138.135/registrar_nota.php").execute(data);
    }

    private void actualizarNota(String alumnoID, String actividadID, String nota) {
        String observaciones = "";
        String data = "id_alumno=" + alumnoID +
                "&id_actividad=" + actividadID +
                "&valor_obtenido=" + nota +
                "&observaciones=" + observaciones;
        new EnviarNotaTask("http://34.122.138.135/actualizar_nota.php").execute(data);
    }

    private void eliminarNota(String alumnoID, String actividadID) {
        String data = "id_alumno=" + alumnoID + "&id_actividad=" + actividadID;
        new EnviarNotaTask("http://34.122.138.135/eliminar_nota.php").execute(data);
    }

    private class EnviarNotaTask extends AsyncTask<String, Void, String> {
        private final String url;

        public EnviarNotaTask(String url) {
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
            Toast.makeText(GestionNotasActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

}