package director;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionPadresActivity extends AppCompatActivity {

    private EditText edtBuscarDPI;
    private Spinner spinnerFiltro;
    private Button btnBuscarPadre, btnLimpiarBusqueda;
    private RecyclerView recyclerViewPadres;
    private FloatingActionButton fabAgregarPadre;
    private PadreAdapter padreAdapter;
    private List<Padre> listaPadres = new ArrayList<>();

    private String urlBuscar = "http://34.71.103.241/buscar_padre_por_dpi.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_padres);

        edtBuscarDPI = findViewById(R.id.edtBuscarDPI);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        btnBuscarPadre = findViewById(R.id.btnBuscarPadre);
        btnLimpiarBusqueda = findViewById(R.id.btnLimpiarBusqueda);
        recyclerViewPadres = findViewById(R.id.recyclerViewPadres);
        fabAgregarPadre = findViewById(R.id.fabAgregarPadre);

        configurarSpinner();
        configurarRecyclerView();

        btnBuscarPadre.setOnClickListener(view -> buscarPadres());
        btnLimpiarBusqueda.setOnClickListener(view -> limpiarBusqueda());
        fabAgregarPadre.setOnClickListener(view -> {
            // Aquí puedes abrir una nueva activity o un dialog para agregar padre
        });

        fabAgregarPadre.setOnClickListener(view -> {
            mostrarDialogAgregarPadre();
        });

    } // fin onCreate

    private void configurarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"todos", "activo", "inactivo"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
    }

    private void configurarRecyclerView() {
        padreAdapter = new PadreAdapter(GestionPadresActivity.this, listaPadres);
        recyclerViewPadres.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPadres.setAdapter(padreAdapter);
    }

    private void buscarPadres() {
        String dpi = edtBuscarDPI.getText().toString().trim();
        String estado = spinnerFiltro.getSelectedItem().toString().toLowerCase();

        StringRequest request = new StringRequest(Request.Method.POST, urlBuscar,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaPadres.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Padre padre = new Padre(
                                    obj.getString("nombre"),
                                    obj.getString("apellido"),
                                    obj.getString("email"),
                                    obj.getString("telefono"),
                                    obj.getString("estado"),
                                    obj.getString("dpi")
                            );
                            listaPadres.add(padre);
                        }

                        padreAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("dpi", dpi);
                params.put("estado", estado);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void limpiarBusqueda() {
        edtBuscarDPI.setText("");
        spinnerFiltro.setSelection(0); // todos
        listaPadres.clear();
        padreAdapter.notifyDataSetChanged();
    }

    private void mostrarDialogAgregarPadre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nuevo padre");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_padre, null);
        builder.setView(view);

        EditText edtNombre = view.findViewById(R.id.edtNuevoNombre);
        EditText edtApellido = view.findViewById(R.id.edtNuevoApellido);
        EditText edtEmail = view.findViewById(R.id.edtNuevoEmail);
        EditText edtTelefono = view.findViewById(R.id.edtNuevoTelefono);
        EditText edtDpi = view.findViewById(R.id.edtNuevoDPI);
        EditText edtPassword = view.findViewById(R.id.edtNuevoPassword);

        builder.setPositiveButton("Registrar", (dialog, which) -> {
            String nombre = edtNombre.getText().toString().trim();
            String apellido = edtApellido.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String telefono = edtTelefono.getText().toString().trim();
            String dpi = edtDpi.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || telefono.isEmpty() || dpi.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarNuevoPadre(nombre, apellido, email, telefono, dpi, password);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void registrarNuevoPadre(String nombre, String apellido, String email, String telefono, String dpi, String password) {
        String url = "http://34.71.103.241/agregar_padre.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Padre registrado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de red al registrar", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", nombre);
                parametros.put("apellido", apellido);
                parametros.put("email", email);
                parametros.put("telefono", telefono);
                parametros.put("dpi", dpi);
                parametros.put("password", password);
                return parametros;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }



}