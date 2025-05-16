package com.principal.cp.maestros;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.principal.cp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;


public class GestionActividadesActivity extends AppCompatActivity {

    private EditText edtActividadID, edtIdMateria, edtTitulo, edtDescripcion,
            edtFechaEntrega, edtValorTotal, edtGrado, edtSeccion, edtIdMaestro;
    private Button btnRegistrarActividad, btnEditarActividad, btnEliminarActividad;
    private Spinner spinnerTipo, spinnerMateria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_actividades);

        edtFechaEntrega.setFocusable(false);
        edtFechaEntrega.setOnClickListener(v -> mostrarDateTimePicker());
        edtActividadID = findViewById(R.id.edtActividadID);
        spinnerMateria = findViewById(R.id.spinnerMateria);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        edtFechaEntrega = findViewById(R.id.edtFechaEntrega);
        edtValorTotal = findViewById(R.id.edtValorTotal);
        edtGrado = findViewById(R.id.edtGrado);
        edtSeccion = findViewById(R.id.edtSeccion);
        edtIdMaestro = findViewById(R.id.edtIdMaestro);

        Spinner spinnerTipo = findViewById(R.id.spinnerTipo);
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"tarea", "examen", "proyecto", "otro"});
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);


        btnRegistrarActividad = findViewById(R.id.btnRegistrarActividad);
        btnEditarActividad = findViewById(R.id.btnEditarActividad);
        btnEliminarActividad = findViewById(R.id.btnEliminarActividad);

        btnRegistrarActividad.setOnClickListener(v -> {
            String data = getActividadData(false);
            new ActividadTask("http://34.122.138.135/registrar_actividad.php").execute(data);
        });

        btnEditarActividad.setOnClickListener(v -> {
            String data = getActividadData(true);
            new ActividadTask("http://34.122.138.135/editar_actividad.php").execute(data);
        });

        btnEliminarActividad.setOnClickListener(v -> {
            String id = edtActividadID.getText().toString();
            String data = "id_actividad=" + id;
            new ActividadTask("http://34.122.138.135/eliminar_actividad.php").execute(data);
        });
    }

    private String getActividadData(boolean incluirID) {
        try {
            // Obtener tipo desde Spinner
            String tipo = spinnerTipo.getSelectedItem().toString();

            // Obtener materia seleccionada desde Spinner
            Materia materiaSeleccionada = (Materia) spinnerMateria.getSelectedItem();
            int idMateria = materiaSeleccionada.id_materia;

            StringBuilder data = new StringBuilder();

            if (incluirID) {
                data.append("id_actividad=").append(URLEncoder.encode(edtActividadID.getText().toString(), "UTF-8")).append("&");
            }

            data.append("id_materia=").append(URLEncoder.encode(String.valueOf(idMateria), "UTF-8")).append("&");
            data.append("titulo=").append(URLEncoder.encode(edtTitulo.getText().toString(), "UTF-8")).append("&");
            data.append("descripcion=").append(URLEncoder.encode(edtDescripcion.getText().toString(), "UTF-8")).append("&");
            data.append("tipo=").append(URLEncoder.encode(tipo, "UTF-8")).append("&");
            data.append("fecha_entrega=").append(URLEncoder.encode(edtFechaEntrega.getText().toString(), "UTF-8")).append("&");
            data.append("valor_total=").append(URLEncoder.encode(edtValorTotal.getText().toString(), "UTF-8")).append("&");
            data.append("grado=").append(URLEncoder.encode(edtGrado.getText().toString(), "UTF-8")).append("&");
            data.append("seccion=").append(URLEncoder.encode(edtSeccion.getText().toString(), "UTF-8")).append("&");
            data.append("id_usuario_maestro=").append(URLEncoder.encode(edtIdMaestro.getText().toString(), "UTF-8"));

            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



    private void mostrarDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    int finalYear = year;
                    int finalMonth = monthOfYear + 1; // Calendar is zero-based
                    int finalDay = dayOfMonth;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(GestionActividadesActivity.this,
                            (view1, hourOfDay, minute) -> {
                                String fechaSeleccionada = String.format("%04d-%02d-%02d %02d:%02d:00",
                                        finalYear, finalMonth, finalDay, hourOfDay, minute);
                                edtFechaEntrega.setText(fechaSeleccionada);
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private class ActividadTask extends AsyncTask<String, Void, String> {
        private final String urlString;

        public ActividadTask(String url) {
            this.urlString = url;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    return sb.toString();
                } else {
                    return "Error en la conexión: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            Toast.makeText(GestionActividadesActivity.this, resultado, Toast.LENGTH_LONG).show();
        }
    }
}

