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

    private void mostrarDialogoAgregar() { //agrega alumnos
        AlertDialog.Builder builder = new AlertDialog.Builder(GestionAlumnosActivity.this);
        builder.setTitle("Agregar Nuevo Alumno");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_alumno, null);
        builder.setView(view);

        // Campos de alumno
        EditText nombre = view.findViewById(R.id.editNombre);
        EditText apellido = view.findViewById(R.id.editApellido);
        EditText fecha = view.findViewById(R.id.editFecha);
        EditText direccion = view.findViewById(R.id.editDireccion);
        EditText telefono = view.findViewById(R.id.editTelefono);
        EditText email = view.findViewById(R.id.editEmail);
        EditText grado = view.findViewById(R.id.editGrado);
        EditText seccion = view.findViewById(R.id.editSeccion);
        Spinner spinnerPadres = view.findViewById(R.id.spinnerPadres);

        // Activar DatePicker en campo de fecha
        fecha.setFocusable(false);
        fecha.setOnClickListener(v1 -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this, (view1, year1, month1, dayOfMonth) -> {
                String fechaSeleccionada = year1 + "-" + String.format("%02d", month1 + 1) + "-" + String.format("%02d", dayOfMonth);
                fecha.setText(fechaSeleccionada);
            }, year, month, day);

            datePicker.show();
        });

        // Cargar padres en Spinner
        final List<Padre> listaPadres = new ArrayList<>();
        listaPadres.add(new Padre(-1, "Seleccione un padre...")); // Opción inicial

        final ArrayAdapter<Padre> adapterPadres = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listaPadres);
        adapterPadres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPadres.setAdapter(adapterPadres);

        // Petición a servidor
        String urlPadres = "http://34.71.103.241/listar_padres_activos.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlPadres, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject padreObj = response.getJSONObject(i);
                            int id = padreObj.getInt("id_usuario");
                            String nombrePadre = padreObj.getString("nombre") + " " + padreObj.getString("apellido");
                            listaPadres.add(new Padre(id, nombrePadre));
                        }
                        listaPadres.add(new Padre(-99, "➕ Registrar nuevo padre")); // Opción al final
                        adapterPadres.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error procesando padres", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error cargando padres", Toast.LENGTH_LONG).show()
        );
        queue.add(request);

        // Si el usuario elige "Registrar nuevo padre"
        spinnerPadres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Padre seleccionado = (Padre) parent.getItemAtPosition(position);
                if (seleccionado.getId() == -99) {
                    mostrarDialogoAgregarPadre(listaPadres, adapterPadres, spinnerPadres); // Aún por implementar
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setPositiveButton("Guardar", null);
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            Button btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnGuardar.setOnClickListener(v -> {
                // Validación
                String valNombre = nombre.getText().toString().trim();
                String valApellido = apellido.getText().toString().trim();
                String valFecha = fecha.getText().toString().trim();
                String valGrado = grado.getText().toString().trim();
                String valSeccion = seccion.getText().toString().trim();
                Padre padreSeleccionado = (Padre) spinnerPadres.getSelectedItem();

                if (valNombre.isEmpty() || valApellido.isEmpty() || valFecha.isEmpty()
                        || valGrado.isEmpty() || valSeccion.isEmpty() || padreSeleccionado == null || padreSeleccionado.getId() <= 0) {
                    Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                registrarAlumnoEnServidor(
                        valNombre,
                        valApellido,
                        valFecha,
                        direccion.getText().toString().trim(),
                        telefono.getText().toString().trim(),
                        email.getText().toString().trim(),
                        valGrado,
                        valSeccion,
                        String.valueOf(padreSeleccionado.getId())
                );
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void mostrarDialogoAgregarPadre(List<Padre> listaPadres, ArrayAdapter<Padre> adapterPadres, Spinner spinnerPadres) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrar Nuevo Padre");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_padre, null);
        builder.setView(view);

        EditText nombre = view.findViewById(R.id.edtNuevoNombre);
        EditText apellido = view.findViewById(R.id.edtNuevoApellido);
        EditText email = view.findViewById(R.id.edtNuevoEmail);
        EditText telefono = view.findViewById(R.id.edtNuevoTelefono);
        EditText dpi = view.findViewById(R.id.edtNuevoDPI);
        EditText password = view.findViewById(R.id.edtNuevoPassword);

        builder.setPositiveButton("Registrar", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d1 -> {
            Button btnRegistrar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnRegistrar.setOnClickListener(v -> {
                String valNombre = nombre.getText().toString().trim();
                String valApellido = apellido.getText().toString().trim();
                String valEmail = email.getText().toString().trim();
                String valTelefono = telefono.getText().toString().trim();
                String valDpi = dpi.getText().toString().trim();
                String valPassword = password.getText().toString().trim();

                if (valNombre.isEmpty() || valEmail.isEmpty() || valPassword.isEmpty()) {
                    Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                registrarPadreEnServidor(valNombre, valApellido, valEmail, valTelefono, valDpi, valPassword,
                        listaPadres, adapterPadres, spinnerPadres);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void registrarPadreEnServidor(String nombre, String apellido, String email, String telefono, String dpi, String password,
                                          List<Padre> listaPadres, ArrayAdapter<Padre> adapterPadres, Spinner spinnerPadres) {
        String url = "http://34.71.103.241/agregar_padre.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            int nuevoId = obj.getInt("id");
                            String nombreCompleto = obj.getString("nombre") + " " + obj.getString("apellido");

                            Padre nuevoPadre = new Padre(nuevoId, nombreCompleto);
                            // Insertar antes de la opción "➕ Registrar nuevo padre"
                            listaPadres.add(listaPadres.size() - 1, nuevoPadre);
                            adapterPadres.notifyDataSetChanged();
                            spinnerPadres.setSelection(listaPadres.indexOf(nuevoPadre));

                            Toast.makeText(this, "✅ Padre registrado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "❌ Error: " + obj.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de red al registrar padre: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

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

        queue.add(request);
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