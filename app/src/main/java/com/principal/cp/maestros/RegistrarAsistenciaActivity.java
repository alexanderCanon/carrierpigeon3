package com.principal.cp.maestros;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RegistrarAsistenciaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabGuardar;
    private ArrayList<AsistenciaAlumno> listaAlumnos;
    private AsistenciaAlumnoAdapter adapter;
    private int idAsignacion;
    private String grado, seccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_asistencia);

        recyclerView = findViewById(R.id.recyclerAlumnosAsistencia);
        fabGuardar = findViewById(R.id.fabGuardarAsistencia);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaAlumnos = new ArrayList<>();
        adapter = new AsistenciaAlumnoAdapter(listaAlumnos, this);
        recyclerView.setAdapter(adapter);

        idAsignacion = getIntent().getIntExtra("id_asignacion", -1);
        grado = getIntent().getStringExtra("grado");
        seccion = getIntent().getStringExtra("seccion");

        cargarAlumnos();
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

        fabGuardar.setOnClickListener(v -> guardarAsistencias());
    }

    private void cargarAlumnos() {
        try {
            String gradoEncoded = URLEncoder.encode(grado, "UTF-8");
            String seccionEncoded = URLEncoder.encode(seccion, "UTF-8");
            String url = "http://34.71.103.241/listar_alumnos_por_grado_seccion.php?grado=" + gradoEncoded + "&seccion=" + seccionEncoded;
            Log.d("DEBUG_URL", "URL: " + url);

            new AsyncTask<Void, Void, JSONArray>() {
                @Override
                protected JSONArray doInBackground(Void... voids) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setRequestProperty("Accept-Charset", "UTF-8");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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
                        try {
                            listaAlumnos.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                int idAlumno = obj.getInt("id_alumno");
                                String nombre = obj.getString("nombre");
                                String apellido = obj.getString("apellido");
                                listaAlumnos.add(new AsistenciaAlumno(idAlumno, nombre + " " + apellido));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrarAsistenciaActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistrarAsistenciaActivity.this, "Error al cargar alumnos", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void guardarAsistencias() {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        for (AsistenciaAlumno alumno : listaAlumnos) {
            try {
                String data = "id_alumno=" + URLEncoder.encode(String.valueOf(alumno.getId()), "UTF-8") +
                        "&fecha=" + URLEncoder.encode(fecha, "UTF-8") +
                        "&presente=" + URLEncoder.encode(alumno.isPresente() ? "1" : "0", "UTF-8") +
                        "&observaciones=" + URLEncoder.encode(alumno.getObservaciones(), "UTF-8");

                new EnviarAsistenciaTask().execute(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, "✅ Asistencias enviadas correctamente", Toast.LENGTH_SHORT).show();
    }

    private static class EnviarAsistenciaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://34.71.103.241/registrar_asistencia.php");
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
                conn.disconnect();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error al guardar";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Aquí podrías mostrar un toast si se desea
        }
    }
}
