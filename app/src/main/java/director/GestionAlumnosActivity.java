package director;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionAlumnosActivity extends AppCompatActivity {
    private EditText edtGrado, edtSeccion;
    private Button btnBuscar, btnAgregarAlumno;
    private WebView webViewAlumnos;

    private List<Alumno> listaAlumnos;
    private AlumnoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_alumnos2);
        edtGrado = findViewById(R.id.edtGrado);
        edtSeccion = findViewById(R.id.edtSeccion);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregarAlumno = findViewById(R.id.btnAgregarAlumno);


//        btnBuscar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String grado = edtGrado.getText().toString().trim();
//                String seccion = edtSeccion.getText().toString().trim();
//
//                if (!grado.isEmpty() && !seccion.isEmpty()) {
//                    String url = "http://34.71.103.241/alumnos_lista_html.html?grado=" + grado + "&seccion=" + seccion;
//                    webViewAlumnos.loadUrl(url);
//                }
//            }
//        });

//        btnAgregar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Reutiliza el formulario web que ya hicimos
//                Intent intent = new Intent(GestionAlumnosActivity.this, RegistrarAlumnoWebActivity.class);
//                startActivity(intent);
//            }
//        });

        btnAgregarAlumno.setOnClickListener(v -> mostrarDialogoAgregar());


        // Configurar el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAlumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaAlumnos = new ArrayList<>();
        adapter = new AlumnoAdapter(listaAlumnos, this, new AlumnoAdapter.OnItemClickListener() {
            @Override
            public void onEditarClick(Alumno alumno) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestionAlumnosActivity.this);
                builder.setTitle("Editar Alumno");

                View view = LayoutInflater.from(GestionAlumnosActivity.this).inflate(R.layout.dialog_editar_alumno, null);
                builder.setView(view);

                EditText editNombre = view.findViewById(R.id.editNombre);
                EditText editApellido = view.findViewById(R.id.editApellido);
                EditText editGrado = view.findViewById(R.id.editGrado);
                EditText editSeccion = view.findViewById(R.id.editSeccion);

                // Separar nombre y apellido
                String[] nombrePartes = alumno.getNombre().split(" ", 2);
                editNombre.setText(nombrePartes[0]);
                if (nombrePartes.length > 1) {
                    editApellido.setText(nombrePartes[1]);
                }

                editGrado.setText(alumno.getGrado());
                editSeccion.setText(alumno.getSeccion());

                builder.setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = editNombre.getText().toString().trim();
                    String nuevoApellido = editApellido.getText().toString().trim();
                    String nuevoGrado = editGrado.getText().toString().trim();
                    String nuevaSeccion = editSeccion.getText().toString().trim();

                    actualizarAlumnoEnServidor(alumno.getId(), nuevoNombre, nuevoApellido, nuevoGrado, nuevaSeccion);
                });

                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }

            @Override
            public void onEliminarClick(Alumno alumno) {
                new AlertDialog.Builder(GestionAlumnosActivity.this)
                        .setTitle("Eliminar Alumno")
                        .setMessage("¿Estás seguro de que deseas eliminar a " + alumno.getNombre() + "?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            eliminarAlumno(alumno.getId());
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        // Lógica cuando se hace clic en "Buscar"
        btnBuscar.setOnClickListener(v -> {
            // Aquí haces la petición al backend y llenas `listaAlumnos`, luego:
            // listaAlumnos.clear(); listaAlumnos.addAll(nuevosDatos); adapter.notifyDataSetChanged();
            String grado = edtGrado.getText().toString().trim();
            String seccion = edtSeccion.getText().toString().trim();

            if (grado.isEmpty() || seccion.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese grado y sección", Toast.LENGTH_SHORT).show();
            } else {
                buscarAlumnos(grado, seccion);
            }
        });

    } // Fin onCreate

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GestionAlumnosActivity.this);
        builder.setTitle("Agregar Nuevo Alumno");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_alumno, null);
        builder.setView(view);

        EditText nombre = view.findViewById(R.id.editNombre);
        EditText apellido = view.findViewById(R.id.editApellido);
        EditText fecha = view.findViewById(R.id.editFecha);
        fecha.setFocusable(false);
        fecha.setOnClickListener(v1 -> {
            Calendar calendar = Calendar.getInstance(); //Mucho ojo con esto
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this, (view1, year1, month1, dayOfMonth) -> {
                String fechaSeleccionada = year1 + "-" + String.format("%02d", month1 + 1) + "-" + String.format("%02d", dayOfMonth);
                fecha.setText(fechaSeleccionada);
            }, year, month, day);

            datePicker.show();
        });
        EditText direccion = view.findViewById(R.id.editDireccion);
        EditText telefono = view.findViewById(R.id.editTelefono);
        EditText email = view.findViewById(R.id.editEmail);
        EditText grado = view.findViewById(R.id.editGrado);
        EditText seccion = view.findViewById(R.id.editSeccion);
        EditText padreId = view.findViewById(R.id.editPadreID);

        builder.setPositiveButton("Guardar", null); // Se asignará luego manualmente

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            Button btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnGuardar.setOnClickListener(v12 -> {
                // Validar campos obligatorios
                String valNombre = nombre.getText().toString().trim();
                String valApellido = apellido.getText().toString().trim();
                String valFecha = fecha.getText().toString().trim();
                String valGrado = grado.getText().toString().trim();
                String valSeccion = seccion.getText().toString().trim();
                String valPadreID = padreId.getText().toString().trim();

                if (valNombre.isEmpty() || valApellido.isEmpty() || valFecha.isEmpty()
                        || valGrado.isEmpty() || valSeccion.isEmpty() || valPadreID.isEmpty()) {
                    Toast.makeText(GestionAlumnosActivity.this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    registrarAlumnoEnServidor(
                            valNombre,
                            valApellido,
                            valFecha,
                            direccion.getText().toString().trim(),
                            telefono.getText().toString().trim(),
                            email.getText().toString().trim(),
                            valGrado,
                            valSeccion,
                            valPadreID
                    );
                    dialog.dismiss(); // solo cerrar si fue exitoso
                }
            });
        });
        builder.setNegativeButton("Cancelar", null);
        dialog.show(); // este debe ir después de todo
    }

    private void registrarAlumnoEnServidor(String nombre, String apellido, String fecha, String direccion,
                                           String telefono, String email, String grado, String seccion, String idPadre) {
        String url = "http://34.71.103.241/registrar_alumno_html.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show();
                    btnBuscar.performClick(); // actualiza la lista
                },
                error -> {
                    Toast.makeText(this, "Error al registrar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("fecha_nacimiento", fecha);
                params.put("direccion", direccion);
                params.put("telefono", telefono);
                params.put("email", email);
                params.put("grado", grado);
                params.put("seccion", seccion);
                params.put("id_usuario_padre", idPadre);
                return params;
            }
        };

        queue.add(request);
    }

    private void buscarAlumnos(String grado, String seccion) {
        String url = "http://34.71.103.241/listar_alumnos_html.php?grado=" + grado + "&seccion=" + seccion;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaAlumnos.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id_alumno");
                            String nombre = obj.getString("nombre") + " " + obj.getString("apellido");
                            String gradoR = obj.getString("grado");
                            String seccionR = obj.getString("seccion");

                            Alumno alumno = new Alumno(id, nombre, gradoR, seccionR);
                            listaAlumnos.add(alumno);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    } // Fin buscarAlumnos

    private void actualizarAlumnoEnServidor(int id, String nombre, String apellido, String grado, String seccion) {
        String url = "http://34.71.103.241/modificar_alumno_html.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show();
                    // Refresca lista
                    btnBuscar.performClick(); // simula clic en "Buscar"
                },
                error -> {
                    Toast.makeText(this, "Error al actualizar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_alumno", String.valueOf(id));
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("grado", grado);
                params.put("seccion", seccion);
                return params;
            }
        };

        queue.add(request);
    } // Fin actualizarAlumnoEnServidor

    private void eliminarAlumno(int id) {
        String url = "http://34.71.103.241/eliminar_alumno_html.php?id=" + id;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show();
                    btnBuscar.performClick(); // refrescar la lista
                },
                error -> {
                    Toast.makeText(this, "Error al eliminar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}