package com.principal.cp.maestros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnviarAvisoAlumnosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlumnoAvisoAdapter adapter;
    private List<AlumnoAviso> listaAlumnos = new ArrayList<>();
    private Button btnSiguiente;
    private String grado, seccion;
    private int idMaestro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enviar_aviso_alumnos);

        grado = getIntent().getStringExtra("grado");
        seccion = getIntent().getStringExtra("seccion");

        recyclerView = findViewById(R.id.recyclerAlumnos);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnoAvisoAdapter(listaAlumnos);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idMaestro = prefs.getInt("id_usuario", -1);

        cargarAlumnos();

        btnSiguiente.setOnClickListener(v -> {
            List<Integer> ids = adapter.getIdsSeleccionados();
            if (ids.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un alumno", Toast.LENGTH_SHORT).show();
                return;
            }

            mostrarDialogoAviso(ids);
        });

    }//FIN ONCREATE

    private void cargarAlumnos() {
        String url = "http://34.71.103.241/listar_alumnos_por_maestro.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            int id = obj.getInt("id_alumno");
                            String nombre = obj.getString("nombre");
                            String apellido = obj.getString("apellido");
                            listaAlumnos.add(new AlumnoAviso(id, nombre, apellido));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando alumnos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grado", grado);
                params.put("seccion", seccion);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void mostrarDialogoAviso(List<Integer> idsSeleccionados) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Redactar aviso");

        View view = getLayoutInflater().inflate(R.layout.dialog_enviar_aviso_padres, null);
        EditText edtAsunto = view.findViewById(R.id.edtAsunto);
        EditText edtMensaje = view.findViewById(R.id.edtMensaje);

        builder.setView(view);
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String asunto = edtAsunto.getText().toString().trim();
            String mensaje = edtMensaje.getText().toString().trim();

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Asunto y mensaje son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarAviso(idsSeleccionados, asunto, mensaje);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void enviarAviso(List<Integer> ids, String asunto, String mensaje) {
        String url = "http://34.71.103.241/enviar_avisos_maestro.php";

        JSONArray jsonIds = new JSONArray();
        for (int id : ids) jsonIds.put(id);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Aviso enviado", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error al enviar aviso", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ids", jsonIds.toString());
                params.put("asunto", asunto);
                params.put("mensaje", mensaje);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
