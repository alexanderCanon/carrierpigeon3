package director;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionMaestrosActivity extends AppCompatActivity {

    private EditText edtBuscarDPI;
    private Spinner spinnerEstado;
    private ImageButton btnBuscar, btnLimpiar, btnAgregarMaestro, btnAsignarMateria, btnEnviarAviso;
    private RecyclerView recyclerMaestros;
    private MaestroAdapter maestroAdapter;
    private ArrayList<Maestro> listaMaestros;
    private RequestQueue requestQueue;
    private final String URL_BUSCAR = "http://34.71.103.241/buscar_maestros.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_maestros);
        edtBuscarDPI = findViewById(R.id.edtBuscarDPI);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregarMaestro = findViewById(R.id.btnAgregarMaestro);
        btnAsignarMateria = findViewById(R.id.btnAsignarMateria);
        btnEnviarAviso = findViewById(R.id.btnEnviarAviso);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        recyclerMaestros = findViewById(R.id.recyclerMaestros);

        listaMaestros = new ArrayList<>();
        maestroAdapter = new MaestroAdapter(this, listaMaestros);
        recyclerMaestros.setLayoutManager(new LinearLayoutManager(this));
        recyclerMaestros.setAdapter(maestroAdapter);

        requestQueue = Volley.newRequestQueue(this);

        configurarSpinnerEstado();

        btnBuscar.setOnClickListener(v -> buscarMaestros());
        btnLimpiar.setOnClickListener(v -> limpiarFiltros());
        btnAgregarMaestro.setOnClickListener(v -> mostrarDialogoAgregarMaestro());
        btnAsignarMateria.setOnClickListener(v -> mostrarDialogoAsignarMateria());
        btnEnviarAviso.setOnClickListener(v -> {
            Intent intent = new Intent(GestionMaestrosActivity.this, EnviarAvisoActivity.class);
            startActivity(intent);
        });
    } // Fin onCreate

    private void configurarSpinnerEstado() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"todos", "activo", "inactivo"});
        spinnerEstado.setAdapter(adapter);
    }

    private void buscarMaestros() {
        String dpi = edtBuscarDPI.getText().toString().trim();
        String estado = spinnerEstado.getSelectedItem().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_BUSCAR,
                response -> {
                    try {
                        listaMaestros.clear();
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() == 0) {
                            Toast.makeText(this, "No se encontró maestro", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                Maestro m = new Maestro(
                                        obj.getInt("id_usuario"),
                                        obj.getString("nombre"),
                                        obj.getString("apellido"),
                                        obj.getString("email"),
                                        obj.getString("telefono"),
                                        obj.getString("dpi"),
                                        obj.getString("estado")
                                );
                                listaMaestros.add(m);
                            }
                        }
                        maestroAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("dpi", dpi);
                if (!estado.equals("todos")) {
                    parametros.put("estado", estado);
                }
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    } //fin buscarMaestros

    private void limpiarFiltros() {
        edtBuscarDPI.setText("");
        spinnerEstado.setSelection(0);
        listaMaestros.clear();
        maestroAdapter.notifyDataSetChanged();
    }

    private void mostrarDialogoAgregarMaestro() {
        View vista = getLayoutInflater().inflate(R.layout.dialog_agregar_maestro, null);

        EditText edtNombre = vista.findViewById(R.id.edtNombre);
        EditText edtApellido = vista.findViewById(R.id.edtApellido);
        EditText edtEmail = vista.findViewById(R.id.edtEmail);
        EditText edtTelefono = vista.findViewById(R.id.edtTelefono);
        EditText edtDPI = vista.findViewById(R.id.edtDPI);
        EditText edtPassword = vista.findViewById(R.id.edtPassword);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Maestro")
                .setView(vista)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = edtNombre.getText().toString().trim();
                    String apellido = edtApellido.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String telefono = edtTelefono.getText().toString().trim();
                    String dpi = edtDPI.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                            telefono.isEmpty() || dpi.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dpi.length() != 13) {
                        Toast.makeText(this, "El DPI debe tener 13 dígitos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    registrarMaestro(nombre, apellido, email, telefono, dpi, password);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    } // fin mostrarDialogoAgregarMaestro

    private void registrarMaestro(String nombre, String apellido, String email, String telefono, String dpi, String password) {
        String url = "http://34.71.103.241/insertar_maestro.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String mensaje = obj.getString("message");
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                        if (success) {
                            buscarMaestros(); // recargar lista después de insertar
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("email", email);
                params.put("telefono", telefono);
                params.put("dpi", dpi);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    } // fin registrarMaestro

    private void mostrarDialogoAsignarMateria() {
        View vista = getLayoutInflater().inflate(R.layout.dialog_asignar_materia, null);

        Spinner spinnerMaestro = vista.findViewById(R.id.spinnerMaestro);
        Spinner spinnerMateria = vista.findViewById(R.id.spinnerMateria);
        EditText edtGrado = vista.findViewById(R.id.edtGrado);
        EditText edtSeccion = vista.findViewById(R.id.edtSeccion);

        List<MaestroSpinnerItem> listaMaestros = new ArrayList<>();
        List<MateriaSpinnerItem> listaMaterias = new ArrayList<>();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Asignar Materia")
                .setView(vista)
                .setPositiveButton("Asignar", (d, which) -> {
                    MaestroSpinnerItem maestro = (MaestroSpinnerItem) spinnerMaestro.getSelectedItem();
                    MateriaSpinnerItem materia = (MateriaSpinnerItem) spinnerMateria.getSelectedItem();
                    String grado = edtGrado.getText().toString().trim();
                    String seccion = edtSeccion.getText().toString().trim();

                    if (maestro.id == -1 || materia.id == -1 || grado.isEmpty() || seccion.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    asignarMateria(maestro.id, materia.id, grado, seccion);
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        // Cargar datos
        cargarMaestros(spinnerMaestro, listaMaestros);
        cargarMaterias(spinnerMateria, listaMaterias);
    }

    private void cargarMaestros(Spinner spinner, List<MaestroSpinnerItem> lista) {
        String url = "http://34.71.103.241/obtener_maestros.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    lista.clear();
                    lista.add(new MaestroSpinnerItem(-1, "Selecciona un maestro")); // <-- hint
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id_usuario");
                            String nombre = obj.getString("nombre");
                            String apellido = obj.getString("apellido");
                            lista.add(new MaestroSpinnerItem(id, nombre + " " + apellido));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<MaestroSpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista);
                    spinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Error al cargar maestros", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    } // fin cargarMaestros

    private void cargarMaterias(Spinner spinner, List<MateriaSpinnerItem> lista) {
        String url = "http://34.71.103.241/obtener_materias.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    lista.clear();
                    lista.add(new MateriaSpinnerItem(-1, "Selecciona una materia")); // <-- hint
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id_materia");
                            String nombre = obj.getString("nombre");
                            lista.add(new MateriaSpinnerItem(id, nombre));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<MateriaSpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista);
                    spinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Error al cargar materias", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    } // fin cargarMaterias

    private void asignarMateria(int idUsuario, int idMateria, String grado, String seccion) {
        String url = "http://34.71.103.241/asignar_materia.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String mensaje = obj.getString("message");
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", String.valueOf(idUsuario));
                params.put("id_materia", String.valueOf(idMateria));
                params.put("grado", grado);
                params.put("seccion", seccion);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    } // fin asignarMateria
}