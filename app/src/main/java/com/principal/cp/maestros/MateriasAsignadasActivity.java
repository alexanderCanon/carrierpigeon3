package com.principal.cp.maestros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

        idUsuario = getIntent().getIntExtra("id_usuario", -1);

        recyclerView = findViewById(R.id.recyclerMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MateriaAdapter(materiasList, materia -> {
            Intent intent = new Intent(MateriasAsignadasActivity.this, AlumnosPorMateriaActivity.class);
            intent.putExtra("grado", materia.getGrado());
            intent.putExtra("seccion", materia.getSeccion());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        cargarMaterias();

        Button btnMenuOpciones = findViewById(R.id.btnMenuOpciones);

        btnMenuOpciones.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MateriasAsignadasActivity.this, btnMenuOpciones);
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