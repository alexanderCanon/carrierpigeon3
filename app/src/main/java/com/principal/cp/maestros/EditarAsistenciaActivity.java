package com.principal.cp.maestros;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditarAsistenciaActivity extends AppCompatActivity {

    private EditText edtFechaSeleccionada;
    private Button btnCargarAsistencia;
    private RecyclerView recyclerView;
    private FloatingActionButton fabGuardarCambios;
    private ArrayList<AsistenciaAlumno> listaAlumnos;
    private AsistenciaAlumnoAdapter adapter;

    private String grado, seccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_asistencia);

        edtFechaSeleccionada = findViewById(R.id.edtFechaSeleccionada);

        edtFechaSeleccionada.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditarAsistenciaActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        edtFechaSeleccionada.setText(fecha);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        btnCargarAsistencia = findViewById(R.id.btnCargarAsistencia);
        recyclerView = findViewById(R.id.recyclerEditarAsistencia);
        fabGuardarCambios = findViewById(R.id.fabGuardarCambios);

        grado = getIntent().getStringExtra("grado");
        seccion = getIntent().getStringExtra("seccion");

        listaAlumnos = new ArrayList<>();
        adapter = new AsistenciaAlumnoAdapter(listaAlumnos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCargarAsistencia.setOnClickListener(v -> {
            String fecha = edtFechaSeleccionada.getText().toString();
            if (!fecha.isEmpty()) {
                cargarAsistencia(fecha);
            } else {
                Toast.makeText(this, "Ingrese una fecha válida", Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_notas);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_materias) {
                startActivity(new Intent(this, MateriasAsignadasActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (itemId == R.id.nav_actividades) {
                startActivity(new Intent(this, GestionActividadesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (itemId == R.id.nav_notas) {
                return true; // ya estás aquí
            }

            if (itemId == R.id.nav_asistencia) {
                startActivity(new Intent(this, GestionAsistenciaActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        fabGuardarCambios.setOnClickListener(v -> guardarCambiosAsistencia());
    }

    private void cargarAsistencia(String fecha) {
        String url = "http://34.71.103.241/obtener_asistencia_por_fecha.php?grado=" + grado + "&seccion=" + seccion + "&fecha=" + fecha;
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return new JSONArray(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray response) {
                if (response != null) {
                    if (response.length() == 0) {
                        listaAlumnos.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(EditarAsistenciaActivity.this, "No se encontró asistencia para esta fecha.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        listaAlumnos.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int idAlumno = obj.getInt("id_alumno");
                            String nombre = obj.getString("nombre");
                            String apellido = obj.getString("apellido");
                            boolean presente = obj.getInt("presente") == 1;
                            String observaciones = obj.getString("observaciones");

                            AsistenciaAlumno alumno = new AsistenciaAlumno(idAlumno, nombre + " " + apellido);
                            alumno.setPresente(presente);
                            alumno.setObservaciones(observaciones);
                            listaAlumnos.add(alumno);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(EditarAsistenciaActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditarAsistenciaActivity.this, "Error al cargar asistencia", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void guardarCambiosAsistencia() {
        String fecha = edtFechaSeleccionada.getText().toString();
        for (AsistenciaAlumno alumno : listaAlumnos) {
            String data = "id_alumno=" + alumno.getId()
                    + "&fecha=" + fecha
                    + "&presente=" + (alumno.isPresente() ? "1" : "0")
                    + "&observaciones=" + alumno.getObservaciones();
            new ActualizarAsistenciaTask().execute(data);
        }
        Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
    }

    private static class ActualizarAsistenciaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://34.71.103.241/actualizar_asistencia.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(params[0].getBytes());
                os.flush();
                os.close();

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
                return "Error al actualizar";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ACTUALIZAR_ASISTENCIA", result);
        }
    }
}
