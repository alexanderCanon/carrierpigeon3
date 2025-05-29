package com.principal.cp.maestros;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.principal.cp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerNotasMateriaActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private int idAsignacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_notas_materia);

        tableLayout = findViewById(R.id.tableLayoutNotasMateria);
        idAsignacion = getIntent().getIntExtra("id_asignacion", -1);

        if (idAsignacion != -1) {
            cargarNotas();
        } else {
            Toast.makeText(this, "ID de asignación no válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarNotas() {
        Log.d("DEBUG_ASIGNACION", "ID Asignación: " + idAsignacion);
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/notas_por_materia.php?id_asignacion=" + idAsignacion);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray actividades = jsonObject.getJSONArray("actividades");
                JSONArray alumnos = jsonObject.getJSONArray("alumnos");

                runOnUiThread(() -> {
                    // Limpia por si se recarga
                    tableLayout.removeAllViews();

                    // Fila de encabezados
                    TableRow header = new TableRow(this);
                    header.addView(crearCelda("Alumno", true));
                    for (int i = 0; i < actividades.length(); i++) {
                        try {
                            header.addView(crearCelda(actividades.getString(i), true));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    tableLayout.addView(header);

                    // Filas por alumno
                    for (int i = 0; i < alumnos.length(); i++) {
                        try {
                            JSONObject alumno = alumnos.getJSONObject(i);
                            TableRow row = new TableRow(this);
                            row.addView(crearCelda(alumno.getString("nombre"), false));

                            JSONArray notas = alumno.getJSONArray("notas");
                            Log.d("DEBUG_ALUMNO", "Alumno: " + alumno.getString("nombre") + " - Notas: " + notas.toString());
                            for (int j = 0; j < notas.length(); j++) {
                                row.addView(crearCelda(notas.getString(j), false));
                            }

                            tableLayout.addView(row);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar notas", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private TextView crearCelda(String texto, boolean esEncabezado) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setPadding(16, 12, 16, 12);
        celda.setGravity(Gravity.CENTER);
        celda.setBackgroundResource(R.drawable.cell_border);
        celda.setTextColor(Color.parseColor(esEncabezado ? "#2C5C60" : "#444444"));
        celda.setTextSize(esEncabezado ? 16 : 15);
        celda.setTypeface(null, esEncabezado ? Typeface.BOLD : Typeface.NORMAL);
        return celda;
    }
}