package director;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnviarAvisoActivity extends AppCompatActivity {

    private EditText edtGrado, edtSeccion;
    private Button btnBuscar, btnSiguiente;
    private RecyclerView recyclerMaestros;
    private SeleccionarMaestroAdapter adaptador;
    private List<MaestroSeleccionable> listaMaestros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enviar_aviso);

        edtGrado = findViewById(R.id.edtGrado);
        edtSeccion = findViewById(R.id.edtSeccion);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        recyclerMaestros = findViewById(R.id.recyclerMaestros);

        listaMaestros = new ArrayList<>();
        adaptador = new SeleccionarMaestroAdapter(this, listaMaestros);
        recyclerMaestros.setLayoutManager(new LinearLayoutManager(this));
        recyclerMaestros.setAdapter(adaptador);

        btnBuscar.setOnClickListener(v -> {
            String grado = edtGrado.getText().toString().trim();
            String seccion = edtSeccion.getText().toString().trim();
            if (!grado.isEmpty() && !seccion.isEmpty()) {
                buscarMaestros(grado, seccion);
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            List<MaestroSeleccionable> seleccionados = adaptador.getSeleccionados();
            if (!seleccionados.isEmpty()) {
                List<Integer> ids = new ArrayList<>();
                for (MaestroSeleccionable m : seleccionados) {
                    ids.add(m.getId());
                }
                mostrarDialogoMensaje(ids);
            }
        });
    } //FIN ONCREATE

    private void buscarMaestros(String grado, String seccion) {
        String url = "http://34.71.103.241/buscar_maestros_por_grado.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaMaestros.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id_usuario");
                            String nombreCompleto = obj.getString("nombre") + " " + obj.getString("apellido");
                            listaMaestros.add(new MaestroSeleccionable(id, nombreCompleto));
                        }

                        adaptador.notifyDataSetChanged();

                        if (listaMaestros.isEmpty()) {
                            Toast.makeText(this, "No se encontraron maestros para ese grado y sección", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de red al buscar maestros", Toast.LENGTH_SHORT).show();
                }
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
    } //FIN buscarMaestros

    private void mostrarDialogoMensaje(List<Integer> idsSeleccionados) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar Aviso");

        View vista = getLayoutInflater().inflate(R.layout.dialog_enviar_aviso, null);
        EditText edtAsunto = vista.findViewById(R.id.edtAsunto);
        EditText edtMensaje = vista.findViewById(R.id.edtMensaje);

        builder.setView(vista);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String asunto = edtAsunto.getText().toString().trim();
            String mensaje = edtMensaje.getText().toString().trim();

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(EnviarAvisoActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarAvisoDirector(idsSeleccionados, asunto, mensaje);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    } //FIN mostrarDialogoMensaje

    private void enviarAvisoDirector(List<Integer> idsSeleccionados, String asunto, String mensaje) {
        String url = "http://34.71.103.241/enviar_avisos_director.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "✅ Aviso enviado correctamente", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Error al enviar aviso", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("asunto", asunto);
                params.put("mensaje", mensaje);
                params.put("ids", new com.google.gson.Gson().toJson(idsSeleccionados)); // Enviamos array como JSON string
                return params;
            }
        };

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        queue.add(request);
    } //FIN enviarAvisoDirector

}
