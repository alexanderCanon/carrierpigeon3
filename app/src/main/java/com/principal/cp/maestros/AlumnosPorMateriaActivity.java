package com.principal.cp.maestros;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.principal.cp.R;

public class AlumnosPorMateriaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlumnoAdapter adapter;
    private ArrayList<Alumno> alumnoList = new ArrayList<>();
    private String grado, seccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alumnos_por_materia);
        grado = getIntent().getStringExtra("grado");
        seccion = getIntent().getStringExtra("seccion");

        recyclerView = findViewById(R.id.recyclerAlumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnoAdapter(alumnoList);
        recyclerView.setAdapter(adapter);

        cargarAlumnos();
    }

    private void cargarAlumnos() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/maestros/api_materias/alumnos_por_grado_seccion_html.php?grado=" + grado + "&seccion=" + seccion);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());
                alumnoList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    alumnoList.add(new Alumno(
                            obj.getString("nombre"),
                            obj.getString("apellido")
                    ));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar alumnos", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}