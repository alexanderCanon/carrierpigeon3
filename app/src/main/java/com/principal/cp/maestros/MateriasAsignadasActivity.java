package com.principal.cp.maestros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.principal.cp.R;

public class MateriasAsignadasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MateriaAdapter adapter;
    private ArrayList<Materia> materiasList = new ArrayList<>();
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_materias_asignadas);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_materias);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_materias) {
                    return true;
                } else if (id == R.id.nav_actividades) {
                    startActivity(new Intent(MateriasAsignadasActivity.this, GestionActividadesActivity.class));
                    return true;
                } else if (id == R.id.nav_notas) {
                    startActivity(new Intent(MateriasAsignadasActivity.this, GestionNotasActivity.class));
                    return true;
                } else if (id == R.id.nav_asistencia) {
                    startActivity(new Intent(MateriasAsignadasActivity.this, GestionAsistenciaActivity.class));
                    return true;
                }

                return false;
            }
        });


        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);


        recyclerView = findViewById(R.id.recyclerMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MateriaAdapter(MateriasAsignadasActivity.this, materiasList, materia -> {
            Intent intent = new Intent(MateriasAsignadasActivity.this, AlumnosPorMateriaActivity.class);
            intent.putExtra("grado", materia.getGrado());
            intent.putExtra("seccion", materia.getSeccion());
            startActivity(intent);
        });
        FloatingActionButton btnEnviarAviso = findViewById(R.id.btnEnviarAviso);
        btnEnviarAviso.setOnClickListener(v -> {
            //Intent intent = new Intent(MateriasAsignadasActivity.this, EnviarAvisoAlumnosActivity.class);
            //startActivity(intent);
        });



        recyclerView.setAdapter(adapter);

        cargarMaterias();
    }
    private void cargarMaterias() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/maestros/api_materias/materias_asignadas_html.php?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());
                materiasList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    materiasList.add(new Materia(
                            obj.getInt("id_asignacion"),
                            obj.getString("nombre"),
                            obj.getString("grado"),
                            obj.getString("seccion")
                    ));
                }


                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar materias", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}