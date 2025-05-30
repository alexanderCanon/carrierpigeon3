package com.principal.cp.maestros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GestionNotasActivity extends AppCompatActivity {

    private Spinner spinnerActividad;
    private RecyclerView recyclerAlumnos;
    private AlumnoNotaAdapter alumnoNotaAdapter;
    private List<AlumnoNota> listaAlumnos = new ArrayList<>();
    private Switch switchMostrarTodas;
    private ActividadNota actividadSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        spinnerActividad = findViewById(R.id.spinnerActividad);
        recyclerAlumnos = findViewById(R.id.recyclerAlumnos);
        FloatingActionButton btnGuardarNotas = findViewById(R.id.btnGuardarNotas); // ✅

        switchMostrarTodas = findViewById(R.id.switchMostrarTodas);
        switchMostrarTodas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnGuardarNotas.setImageResource(R.drawable.ic_edit);
            } else {
                btnGuardarNotas.setImageResource(R.drawable.ic_save);
            }

            cargarActividadesParaNotas(isChecked);
        });

        btnGuardarNotas.setOnClickListener(v -> {
            if (switchMostrarTodas.isChecked()) {
                // Switch activado = Editar notas
                actualizarNotas();
            } else {
                // Switch apagado = Guardar nuevas notas
                guardarNotas();
            }
        });


        recyclerAlumnos.setLayoutManager(new LinearLayoutManager(this));
        cargarActividadesParaNotas(false);

        spinnerActividad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actividadSeleccionada = (ActividadNota) parent.getItemAtPosition(position);
                if (actividadSeleccionada != null) {
                    double maxNota = actividadSeleccionada.getValor_total();
                    alumnoNotaAdapter = new AlumnoNotaAdapter(listaAlumnos, maxNota);
                    recyclerAlumnos.setAdapter(alumnoNotaAdapter);

                    cargarAlumnosParaActividad(actividadSeleccionada.getId_actividad());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    }
    private void actualizarNotas() {
        ActividadNota actividad = (ActividadNota) spinnerActividad.getSelectedItem();

        if (actividad == null) {
            Toast.makeText(this, "No se ha seleccionado ninguna actividad", Toast.LENGTH_SHORT).show();
            return;
        }

        List<AlumnoNota> alumnos = alumnoNotaAdapter.getListaAlumnos();

        for (AlumnoNota alumno : alumnos) {
            new ActualizarNotaTask(alumno.getId_alumno(), actividad.getId_actividad(), alumno.nota, alumno.observaciones).execute();
        }
    }
    private class ActualizarNotaTask extends AsyncTask<Void, Void, Boolean> {
        int idAlumno, idActividad;
        String nota, observacion;

        public ActualizarNotaTask(int idAlumno, int idActividad, String nota, String observacion) {
            this.idAlumno = idAlumno;
            this.idActividad = idActividad;
            this.nota = nota;
            this.observacion = observacion;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL("http://34.71.103.241/notas_actualizar.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "id_alumno=" + idAlumno +
                        "&id_actividad=" + idActividad +
                        "&valor_obtenido=" + nota +
                        "&observaciones=" + URLEncoder.encode(observacion, "UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exito) {
            if (exito) {
                Toast.makeText(GestionNotasActivity.this, "Nota actualizada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GestionNotasActivity.this, "Error al actualizar nota", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void cargarActividadesParaNotas(boolean mostrarCalificadas) {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                int idUsuario = prefs.getInt("id_usuario", -1);

                String urlStr = mostrarCalificadas
                        ? "http://34.71.103.241/listar_actividades_con_notas.php"
                        : "http://34.71.103.241/listar_actividades_sin_notas.php";

                URL url = new URL(urlStr + "?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());
                List<ActividadNota> listaActividades = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    int id = obj.getInt("id_actividad");
                    String titulo = obj.getString("titulo");
                    String fecha = obj.getString("fecha_entrega");
                    double valor = obj.getDouble("valor_total");
                    listaActividades.add(new ActividadNota(id, titulo, fecha, valor));
                }

                runOnUiThread(() -> {
                    if (listaActividades.isEmpty()) {
                        Toast.makeText(this, "No hay actividades en este estado.", Toast.LENGTH_LONG).show();
                    }

                    ArrayAdapter<ActividadNota> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.spinner_item,
                            listaActividades
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerActividad.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar actividades", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }



    private void cargarAlumnosParaActividad(int idActividad) {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/notas_listar_alumnos.php?id_actividad=" + idActividad);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("DEBUG_ALUMNOS", "Respuesta JSON: " + result.toString());

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("alumnos");
                List<AlumnoNota> nuevaLista = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    int id = obj.getInt("id_alumno");
                    String nombre = obj.getString("nombre");
                    String apellido = obj.getString("apellido");
                    nuevaLista.add(new AlumnoNota(id, nombre, apellido));
                }

                runOnUiThread(() -> {
                    listaAlumnos.clear();
                    listaAlumnos.addAll(nuevaLista);
                    alumnoNotaAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar alumnos", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    private void guardarNotas() {
        ActividadNota actividad = (ActividadNota) spinnerActividad.getSelectedItem();
        int idActividad = actividad.id_actividad;

        for (AlumnoNota alumno : alumnoNotaAdapter.getListaAlumnos()) {
            String data = "id_alumno=" + alumno.id_alumno +
                    "&id_actividad=" + idActividad +
                    "&valor_obtenido=" + alumno.nota +
                    "&observaciones=" + alumno.observaciones;

            // URL corregida
            new EnviarNotaTask("http://34.71.103.241/notas_guardar.php").execute(data);
        }

        Toast.makeText(this, "Notas enviadas", Toast.LENGTH_SHORT).show();
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
