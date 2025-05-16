package com.principal.cp.maestros;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class GestionAlumnosActivity extends AppCompatActivity {

    private EditText edtAlumnoID, edtNombreAlumno, edtApellidoAlumno, edtTelefonoAlumno;
    private EditText edtEmailAlumno, edtGradoAlumno, edtSeccionAlumno;
    private Button btnRegistrarAlumno, btnActualizarAlumno, btnEliminarAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_alumnos);

        edtAlumnoID = findViewById(R.id.edtAlumnoID);
        edtNombreAlumno = findViewById(R.id.edtNombreAlumno);
        edtApellidoAlumno = findViewById(R.id.edtApellidoAlumno);
        edtTelefonoAlumno = findViewById(R.id.edtTelefonoAlumno);
        edtEmailAlumno = findViewById(R.id.edtEmailAlumno);
        edtGradoAlumno = findViewById(R.id.edtGradoAlumno);
        edtSeccionAlumno = findViewById(R.id.edtSeccionAlumno);

        btnRegistrarAlumno = findViewById(R.id.btnRegistrarAlumno);
        btnActualizarAlumno = findViewById(R.id.btnActualizarAlumno);
        btnEliminarAlumno = findViewById(R.id.btnEliminarAlumno);

        btnRegistrarAlumno.setOnClickListener(v -> {
            String nombre = edtNombreAlumno.getText().toString();
            String apellido = edtApellidoAlumno.getText().toString();
            String telefono = edtTelefonoAlumno.getText().toString();
            String email = edtEmailAlumno.getText().toString();
            String grado = edtGradoAlumno.getText().toString();
            String seccion = edtSeccionAlumno.getText().toString();

            String data = "nombre=" + nombre + "&apellido=" + apellido + "&telefono=" + telefono +
                    "&email=" + email + "&grado=" + grado + "&seccion=" + seccion;

            new EnviarDatosAlumnoTask("http://34.122.138.135/registrar_alumno.php").execute(data);
        });

        btnActualizarAlumno.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String nombre = edtNombreAlumno.getText().toString();
            String apellido = edtApellidoAlumno.getText().toString();
            String telefono = edtTelefonoAlumno.getText().toString();
            String email = edtEmailAlumno.getText().toString();
            String grado = edtGradoAlumno.getText().toString();
            String seccion = edtSeccionAlumno.getText().toString();

            String data = "id=" + alumnoID + "&nombre=" + nombre + "&apellido=" + apellido +
                    "&telefono=" + telefono + "&email=" + email + "&grado=" + grado + "&seccion=" + seccion;

            new EnviarDatosAlumnoTask("http://34.122.138.135/actualizar_alumno.php").execute(data);
        });

        btnEliminarAlumno.setOnClickListener(v -> {
            String alumnoID = edtAlumnoID.getText().toString();
            String data = "id=" + alumnoID;
            new EnviarDatosAlumnoTask("http://34.122.138.135/eliminar_alumno.php").execute(data);
        });
    }

    private class EnviarDatosAlumnoTask extends AsyncTask<String, Void, String> {
        private final String url;

        public EnviarDatosAlumnoTask(String url) {
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
            Toast.makeText(GestionAlumnosActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
