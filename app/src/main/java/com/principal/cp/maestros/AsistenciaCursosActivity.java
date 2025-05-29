package com.principal.cp.maestros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;
import com.principal.cp.maestros.Materia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AsistenciaCursosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Materia> listaMaterias;
    private MateriaAsistenciaAdapter adapter;
    private static final String URL_MATERIAS = "http://34.71.103.241/listar_materias_asignadas.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_cursos);

        recyclerView = findViewById(R.id.recyclerCursosAsistencia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaMaterias = new ArrayList<>();
        adapter = new MateriaAsistenciaAdapter(listaMaterias, this);
        recyclerView.setAdapter(adapter);

        cargarMateriasAsignadas();
    }

    private void cargarMateriasAsignadas() {
        SharedPreferences prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        int idMaestro = prefs.getInt("idUsuario", -1);

        if (idMaestro == -1) {
            Toast.makeText(this, "ID de maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_MATERIAS + "?id_usuario=" + idMaestro;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaMaterias.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int idAsignacion = obj.getInt("id_asignacion");
                            String nombre = obj.getString("nombre");
                            String grado = obj.getString("grado");
                            String seccion = obj.getString("seccion");

                            Materia materia = new Materia(idAsignacion, nombre, grado, seccion);
                            listaMaterias.add(materia);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error al cargar materias", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
