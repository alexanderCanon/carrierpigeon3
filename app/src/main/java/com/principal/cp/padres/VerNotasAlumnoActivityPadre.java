package com.principal.cp.padres;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class VerNotasAlumnoActivityPadre extends AppCompatActivity {

    private TableLayout tableNotas;
    private int idAlumno, idAsignacion;
    private static final String URL_BASE = "http://34.71.103.241/notas_por_alumno.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_notas_alumno_padre);

        tableNotas = findViewById(R.id.tableNotas);

        idAlumno = getIntent().getIntExtra("idAlumno", -1);
        idAsignacion = getIntent().getIntExtra("idAsignacion", -1);

        if (idAlumno != -1 && idAsignacion != -1) {
            cargarNotas();
        } else {
            Toast.makeText(this, "Error al recibir datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarNotas() {
        String url = URL_BASE + "?id_alumno=" + idAlumno + "&id_asignacion=" + idAsignacion;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String titulo = obj.getString("titulo");
                            String nota = obj.getString("nota");

                            TableRow fila = new TableRow(this);
                            fila.setBackgroundColor(getResources().getColor(android.R.color.white));

                            TextView tvActividad = new TextView(this);
                            tvActividad.setText(titulo);
                            tvActividad.setPadding(12, 10, 12, 10);
                            tvActividad.setTextColor(getResources().getColor(android.R.color.black));
                            tvActividad.setTextSize(15);
                            tvActividad.setBackgroundResource(R.drawable.cell_border);

                            TextView tvNota = new TextView(this);
                            tvNota.setText(nota);
                            tvNota.setPadding(12, 10, 12, 10);
                            tvNota.setTextColor(getResources().getColor(android.R.color.black));
                            tvNota.setTextSize(15);
                            tvNota.setBackgroundResource(R.drawable.cell_border);

                            fila.addView(tvActividad);
                            fila.addView(tvNota);
                            tableNotas.addView(fila);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar notas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
