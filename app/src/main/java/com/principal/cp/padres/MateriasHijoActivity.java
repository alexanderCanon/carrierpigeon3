package com.principal.cp.padres;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MateriasHijoActivity extends AppCompatActivity {

    private RecyclerView recyclerMaterias;
    private List<MateriaHijo> listaMaterias = new ArrayList<>();
    private int idAlumno;

    private static final String URL_MATERIAS_HIJO = "http://34.71.103.241/materias_por_hijo.php?id_usuario_padre=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias_hijo);

        recyclerMaterias = findViewById(R.id.recyclerMateriasHijo);
        recyclerMaterias.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idPadre = prefs.getInt("id_usuario", -1);

        if (idPadre != -1) {
            cargarMateriasDeHijo(idPadre);
        } else {
            Toast.makeText(this, "Error al obtener sesión de usuario", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_materias);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_actividades) {
                Intent intent = new Intent(this, ActividadesHijoActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_materias) {
                return true;
            }
            return false;
        });


    }

    private void cargarMateriasDeHijo(int idPadre) {
        String url = URL_MATERIAS_HIJO + idPadre;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject primerElemento = response.getJSONObject(0);
                            idAlumno = primerElemento.getInt("id_alumno"); // Se repite para cada materia

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject materiaJSON = response.getJSONObject(i);

                                int idAsignacion = materiaJSON.getInt("id_asignacion");
                                String nombreMateria = materiaJSON.getString("nombre");
                                String grado = materiaJSON.getString("grado");
                                String seccion = materiaJSON.getString("seccion");

                                listaMaterias.add(new MateriaHijo(idAsignacion, nombreMateria, grado, seccion));
                            }

                            MateriaHijoAdapter adapter = new MateriaHijoAdapter(this, listaMaterias, idAlumno);
                            recyclerMaterias.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No se encontraron materias asignadas al hijo", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        Log.e("MateriasHijo", "Error: ", e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                    Log.e("MateriasHijo", "Volley error: ", error);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
