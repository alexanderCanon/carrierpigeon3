package com.principal.cp.maestros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GestionActividadesActivity extends AppCompatActivity {

    private EditText edtActividadID, edtIdMateria, edtTitulo, edtDescripcion,
            edtFechaEntrega, edtValorTotal, edtGrado, edtSeccion, edtIdMaestro;
    private Button btnRegistrarActividad, btnEditarActividad, btnEliminarActividad;
    private Spinner spinnerTipo, spinnerMateria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_actividades); // ✅ PRIMERO

        // ✅ Luego inicializas todo
        edtActividadID = findViewById(R.id.edtActividadID);
        spinnerMateria = findViewById(R.id.spinnerMateria);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        edtFechaEntrega = findViewById(R.id.edtFechaEntrega);
        edtFechaEntrega.setFocusable(false);
        edtFechaEntrega.setOnClickListener(v -> mostrarDateTimePicker());
        edtValorTotal = findViewById(R.id.edtValorTotal);
        edtGrado = findViewById(R.id.edtGrado);
        edtSeccion = findViewById(R.id.edtSeccion);

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

        Button btnMenuOpciones = findViewById(R.id.btnMenuOpciones);

        btnMenuOpciones.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(GestionActividadesActivity.this, btnMenuOpciones);
            popup.getMenuInflater().inflate(R.menu.menu_maestro, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_volver_materias) {
                    startActivity(new Intent(this, MateriasAsignadasActivity.class));
                    return true;
                } else if (itemId == R.id.menu_actividades) {
                    startActivity(new Intent(this, GestionActividadesActivity.class));
                    return true;
                } else if (itemId == R.id.menu_asistencia) {
                    startActivity(new Intent(this, GestionAsistenciaActivity.class));
                    return true;
                } else if (itemId == R.id.menu_notas) {
                    startActivity(new Intent(this, GestionNotasActivity.class));
                    return true;
                } else {
                    return false;
                }
            });

            popup.show();
        });

        RecyclerView recyclerActividades = findViewById(R.id.recyclerActividades);
        recyclerActividades.setLayoutManager(new LinearLayoutManager(this));
        cargarActividadesEnProgreso(recyclerActividades);


    }

    private String getActividadData(boolean incluirID) {
        try {
            String tipo = spinnerTipo.getSelectedItem().toString();
            Materia materiaSeleccionada = (Materia) spinnerMateria.getSelectedItem();
            int idMateria = materiaSeleccionada.id_materia;

            // ✅ Obtener el ID del maestro desde SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int idMaestro = prefs.getInt("user_id", -1);

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
            data.append("id_usuario_maestro=").append(URLEncoder.encode(String.valueOf(idMaestro), "UTF-8"));


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

    private void cargarActividadesEnProgreso(RecyclerView recyclerView) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int idMaestro = prefs.getInt("user_id", -1);

        String url = "http://34.122.138.135/listar_actividades_en_progreso.php?id_usuario_maestro=" + idMaestro;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL link = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) link.openConnection();
                    conn.setRequestMethod("GET");

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
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String json) {
                if (json != null) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        if (!obj.getBoolean("error")) {
                            JSONArray arr = obj.getJSONArray("actividades");
                            List<Actividad> actividades = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject item = arr.getJSONObject(i);
                                Actividad a = new Actividad();
                                a.id_actividad = item.getInt("id_actividad");
                                a.titulo = item.getString("titulo");
                                a.fecha_entrega = item.getString("fecha_entrega");
                                a.tipo = item.getString("tipo");
                                actividades.add(a);
                            }
                            recyclerView.setAdapter(new ActividadAdapter(actividades));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
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

