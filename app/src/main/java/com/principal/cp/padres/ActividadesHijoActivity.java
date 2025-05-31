package com.principal.cp.padres;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.principal.cp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActividadesHijoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActividadHijoAdapter adapter;
    private ArrayList<ActividadHijo> listaActividades;
    private int idPadre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades_hijo);

        recyclerView = findViewById(R.id.recyclerActividadesHijo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idPadre = prefs.getInt("id_usuario", -1);

        if (idPadre != -1) {
            cargarActividades();
        } else {
            Toast.makeText(this, "No se encontró ID del padre", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_actividades);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_materias) {
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_actividades) {
                return true;
            }
            return false;
        });

        FloatingActionButton btnFiltro = findViewById(R.id.btnFiltroPadre);
        btnFiltro.setOnClickListener(v -> {
            String[] opciones = {"En curso", "Vencidas", "Con nota", "Sin nota"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filtrar por:")
                    .setItems(opciones, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                filtrarPorEstado("En curso");
                                break;
                            case 1:
                                filtrarPorEstado("Vencida");
                                break;
                            case 2:
                                filtrarPorNota(true);
                                break;
                            case 3:
                                filtrarPorNota(false);
                                break;
                        }
                    })
                    .show();
        });


    }

    private void filtrarPorEstado(String estado) {
        ArrayList<ActividadHijo> filtradas = new ArrayList<>();
        for (ActividadHijo act : listaActividades) {
            if (act.getEstado().equalsIgnoreCase(estado)) {
                filtradas.add(act);
            }
        }
        adapter.actualizarLista(filtradas);
    }

    private void filtrarPorNota(boolean conNota) {
        ArrayList<ActividadHijo> filtradas = new ArrayList<>();
        for (ActividadHijo act : listaActividades) {
            boolean tieneNota = !act.getNota().equals("Sin nota") && !act.getNota().trim().isEmpty();
            if ((conNota && tieneNota) || (!conNota && !tieneNota)) {
                filtradas.add(act);
            }
        }
        adapter.actualizarLista(filtradas);
    }


    private void cargarActividades() {
        listaActividades = new ArrayList<>();
        String url = "http://34.71.103.241/actividades_por_hijo.php?id_usuario_padre=" + idPadre;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            ActividadHijo actividad = new ActividadHijo();
                            actividad.setTitulo(obj.getString("titulo"));
                            actividad.setDescripcion(obj.getString("descripcion"));
                            actividad.setFechaEntrega(obj.getString("fechaEntrega"));
                            actividad.setEstado(obj.getString("estado"));
                            actividad.setNota(obj.getString("nota"));
                            listaActividades.add(actividad);
                        }

                        adapter = new ActividadHijoAdapter(listaActividades);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
