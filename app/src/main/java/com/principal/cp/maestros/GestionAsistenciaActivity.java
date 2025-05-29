package com.principal.cp.maestros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GestionAsistenciaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Materia> listaMaterias;
    private MateriaAsistenciaAdapter adapter;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_cursos);
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        recyclerView = findViewById(R.id.recyclerCursosAsistencia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaMaterias = new ArrayList<>();
        adapter = new MateriaAsistenciaAdapter(listaMaterias, this);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_asistencia);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_materias) {
                Intent intent = new Intent(this, MateriasAsignadasActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_actividades) {
                startActivity(new Intent(this, GestionActividadesActivity.class));
                return true;
            } else if (id == R.id.nav_notas) {
                startActivity(new Intent(this, GestionNotasActivity.class));
                return true;
            } else if (id == R.id.nav_asistencia) {
                return true;
            }
            return false;
        });


        cargarMateriasAsignadas();
    }

    private void cargarMateriasAsignadas() {
        SharedPreferences prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        int idMaestro = prefs.getInt("id_usuario", -1);

        if (idMaestro == -1) {
            Toast.makeText(this, "ID de maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://34.71.103.241/maestros/api_materias/materias_asignadas_html.php?id_usuario=" + idMaestro;

        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                try {
                    URL endpoint = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    Log.d("RESPUESTA", result.toString());
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
                        listaMaterias.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String nombre = obj.getString("nombre");
                            String grado = obj.getString("grado");
                            String seccion = obj.getString("seccion");

                            listaMaterias.add(new Materia(nombre, grado, seccion));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(GestionAsistenciaActivity.this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
