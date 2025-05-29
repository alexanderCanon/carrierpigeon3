package com.principal.cp.maestros;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

public class VerNotasAlumnoActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private int idAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_notas_alumno);

        tableLayout = findViewById(R.id.tableLayoutNotas);
        idAlumno = getIntent().getIntExtra("id_alumno", -1);

        if (idAlumno != -1) {
            cargarNotas();
        } else {
            Toast.makeText(this, "ID de alumno no válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarNotas() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/notas_por_alumno.php?id_alumno=" + idAlumno);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray notasArray = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    // Encabezado
                    TableRow headerRow = new TableRow(this);

                    TextView th1 = crearCelda("Actividad", true);
                    TextView th2 = crearCelda("Nota", true);
                    headerRow.addView(th1);
                    headerRow.addView(th2);

                    tableLayout.addView(headerRow);

                    // Celdas de datos
                    for (int i = 0; i < notasArray.length(); i++) {
                        try {
                            JSONObject obj = null;
                            obj = notasArray.getJSONObject(i);
                            String titulo = obj.getString("titulo");
                            String nota = obj.getString("nota");

                            TableRow row = new TableRow(this);
                            row.addView(crearCelda(titulo, false));
                            row.addView(crearCelda(nota, false));
                            tableLayout.addView(row);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar notas", Toast.LENGTH_SHORT).show()
                );
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
