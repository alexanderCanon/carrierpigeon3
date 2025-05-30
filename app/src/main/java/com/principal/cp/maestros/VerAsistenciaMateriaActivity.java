package com.principal.cp.maestros;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerAsistenciaMateriaActivity extends AppCompatActivity {

    private TableLayout tableLayoutAsistencia;
    private int idAsignacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_asistencia_materia);

        tableLayoutAsistencia = findViewById(R.id.tableLayoutAsistencia);
        idAsignacion = getIntent().getIntExtra("id_asignacion", 0);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_materias);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_materias) {
                    startActivity(new Intent(VerAsistenciaMateriaActivity.this, MateriasAsignadasActivity.class));
                    return true;
                } else if (id == R.id.nav_actividades) {
                    startActivity(new Intent(VerAsistenciaMateriaActivity.this, GestionActividadesActivity.class));
                    return true;
                } else if (id == R.id.nav_notas) {
                    startActivity(new Intent(VerAsistenciaMateriaActivity.this, GestionNotasActivity.class));
                    return true;
                } else if (id == R.id.nav_asistencia) {
                    startActivity(new Intent(VerAsistenciaMateriaActivity.this, GestionAsistenciaActivity.class));
                    return true;
                }

                return false;
            }
        });
        cargarAsistencia();
    }

    private void cargarAsistencia() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/asistencia_por_materia.php?id_asignacion=" + idAsignacion);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray fechas = jsonObject.getJSONArray("fechas");
                JSONArray alumnos = jsonObject.getJSONArray("alumnos");

                runOnUiThread(() -> {
                    tableLayoutAsistencia.removeAllViews();

                    // Encabezado
                    TableRow header = new TableRow(this);
                    header.addView(crearCelda("Alumno", true));
                    for (int i = 0; i < fechas.length(); i++) {
                        try {
                            header.addView(crearCelda(fechas.getString(i), true));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    tableLayoutAsistencia.addView(header);

                    // Filas de asistencia
                    for (int i = 0; i < alumnos.length(); i++) {
                        try {
                            JSONObject alumno = alumnos.getJSONObject(i);
                            TableRow row = new TableRow(this);
                            row.addView(crearCelda(alumno.getString("nombre"), false));

                            JSONArray asistencias = alumno.getJSONArray("asistencias");
                            for (int j = 0; j < asistencias.length(); j++) {
                                row.addView(crearCelda(asistencias.getString(j), false));
                            }

                            tableLayoutAsistencia.addView(row);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar asistencia", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private TextView crearCelda(String texto, boolean esEncabezado) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setPadding(16, 8, 16, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(android.R.drawable.edit_text);
        if (esEncabezado) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        return textView;
    }
}
