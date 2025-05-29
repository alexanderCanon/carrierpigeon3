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

public class VerAsistenciaAlumnoActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private int idAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_asistencia_alumno);

        tableLayout = findViewById(R.id.tableLayoutAsistencia);
        idAlumno = getIntent().getIntExtra("id_alumno", -1);

        if (idAlumno != -1) {
            cargarAsistencia();
        } else {
            Toast.makeText(this, "ID de alumno no válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarAsistencia() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/asistencia_por_alumno.php?id_alumno=" + idAlumno);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray asistenciaArray = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    TableRow header = new TableRow(this);
                    header.addView(crearCelda("Fecha", true));
                    header.addView(crearCelda("Estado", true));
                    header.addView(crearCelda("Observaciones", true));
                    tableLayout.addView(header);

                    for (int i = 0; i < asistenciaArray.length(); i++) {
                        JSONObject obj = null;
                        try {
                            obj = asistenciaArray.getJSONObject(i);
                            String fecha = obj.getString("fecha");
                            int estado = obj.getInt("estado");
                            String obs = obj.getString("observaciones");
                            String estadoTexto = estado == 1 ? "Presente" : "Ausente";

                            TableRow row = new TableRow(this);
                            row.addView(crearCelda(fecha, false));
                            row.addView(crearCelda(estadoTexto, false));
                            row.addView(crearCelda(obs, false));
                            tableLayout.addView(row);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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

    private TextView crearCelda(String texto, boolean encabezado) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setPadding(16, 12, 16, 12);
        celda.setGravity(Gravity.CENTER);
        celda.setBackgroundResource(R.drawable.cell_border);
        celda.setTextColor(Color.parseColor(encabezado ? "#2C5C60" : "#444444"));
        celda.setTextSize(encabezado ? 16 : 15);
        celda.setTypeface(null, encabezado ? Typeface.BOLD : Typeface.NORMAL);
        return celda;
    }
}
