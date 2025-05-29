package com.principal.cp.maestros;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.principal.cp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

public class GestionActividadesActivity extends AppCompatActivity {

    private EditText edtActividadID, edtTitulo, edtDescripcion, edtFechaEntrega, edtValorTotal;
    private Button btnRegistrarActividad, btnEditarActividad, btnEliminarActividad;
    private Spinner spinnerTipo, spinnerMateria;
    private List<Materia> listaMaterias = new ArrayList<>();

    private int idUsuario;
    private int idAsignacionSeleccionada = -1;
    private String gradoSeleccionado = "", seccionSeleccionada = "";
    private ScrollView formularioScroll;
    private FloatingActionButton fabAgregarActividad;
    private RecyclerView recyclerActividades;
    private ActividadAdapter actividadAdapter;
    private List<Actividad> listaActividades = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_actividades);
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        FloatingActionButton btnFiltro = findViewById(R.id.btnFiltro);
        btnFiltro.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.menu_filtro_actividades, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.filtro_fecha) {
                    mostrarDatePicker();
                    return true;
                } else if (id == R.id.filtro_calificadas) {
                    cargarActividadesFiltradas("1"); // estado = 1
                    return true;
                } else if (id == R.id.filtro_pendientes) {
                    cargarActividadesFiltradas("0"); // estado = 0
                    return true;
                } else {
                    return false;
                }
            });

            popup.show();
        });



        spinnerMateria = findViewById(R.id.spinnerMateria);
        cargarMaterias();

        edtActividadID = findViewById(R.id.edtActividadID);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        edtFechaEntrega = findViewById(R.id.edtFechaEntrega);
        edtValorTotal = findViewById(R.id.edtValorTotal);

        edtFechaEntrega.setFocusable(false);
        edtFechaEntrega.setOnClickListener(v -> mostrarDateTimePicker());

        formularioScroll = findViewById(R.id.formularioScroll);
        fabAgregarActividad = findViewById(R.id.fabAgregarActividad);

        recyclerActividades = findViewById(R.id.recyclerActividades);
        recyclerActividades.setLayoutManager(new LinearLayoutManager(this));
        listaActividades = new ArrayList<>();
        actividadAdapter = new ActividadAdapter(listaActividades);
        recyclerActividades.setAdapter(actividadAdapter);

        actividadAdapter = new ActividadAdapter(listaActividades);
        recyclerActividades.setAdapter(actividadAdapter);

        actividadAdapter.setOnActividadClickListener(new ActividadAdapter.OnActividadClickListener() {

            @Override
            public void onEditarClick(Actividad actividad) {
                mostrarFormularioEditar(actividad);
            }
            @Override
            public void onEliminarClick(Actividad actividad) {
                new MaterialAlertDialogBuilder(GestionActividadesActivity.this, R.style.CustomAlertDialog)
                        .setTitle("¿Eliminar actividad?")
                        .setMessage("¿Deseas eliminar la actividad \"" + actividad.titulo + "\"?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            String data = "id_actividad=" + actividad.id_actividad;
                            new ActividadTask("http://34.71.103.241/eliminar_actividad.php") {
                                @Override
                                protected void onPostExecute(String resultado) {
                                    super.onPostExecute(resultado);
                                    cargarActividadesEnCurso(); // recargar la lista
                                }
                            }.execute(data);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        fabAgregarActividad.setOnClickListener(v -> {
            if (formularioScroll.getVisibility() == View.GONE) {
                formularioScroll.setVisibility(View.VISIBLE);
                recyclerActividades.setVisibility(View.GONE);
                mostrarFormularioAgregar();
            } else {
                formularioScroll.setVisibility(View.GONE);
                recyclerActividades.setVisibility(View.VISIBLE);
                cargarActividadesEnCurso();
            }
        });

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tarea", "Examen", "Proyecto", "Otro"});
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        spinnerMateria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Materia materiaSeleccionada = (Materia) parent.getItemAtPosition(position);
                idAsignacionSeleccionada = materiaSeleccionada.id_asignacion;
                gradoSeleccionado = materiaSeleccionada.getGrado();
                seccionSeleccionada = materiaSeleccionada.getSeccion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegistrarActividad = findViewById(R.id.btnRegistrarActividad);
        btnEditarActividad = findViewById(R.id.btnEditarActividad);
        btnEliminarActividad = findViewById(R.id.btnEliminarActividad);

        btnRegistrarActividad.setOnClickListener(v -> {
            String data = getActividadData(false);
            new ActividadTask("http://34.71.103.241/registrar_actividad.php").execute(data);
        });

        btnEditarActividad.setOnClickListener(v -> {
            String data = getActividadData(true);
            new ActividadTask("http://34.122.138.135/editar_actividad.php").execute(data);
        });

        btnEliminarActividad.setOnClickListener(v -> {
            String id = edtActividadID.getText().toString();
            String data = "id_actividad=" + id;
            new ActividadTask("http://34.122.138.135/eliminar_actividad.php").execute(data);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_actividades);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_materias) {
                Intent intent = new Intent(GestionActividadesActivity.this, MateriasAsignadasActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_actividades) {
                return true;
            } else if (id == R.id.nav_notas) {
                startActivity(new Intent(GestionActividadesActivity.this, GestionNotasActivity.class));
                return true;
            } else if (id == R.id.nav_asistencia) {
                startActivity(new Intent(GestionActividadesActivity.this, GestionAsistenciaActivity.class));
                return true;
            }
            return false;
        });
        cargarActividadesEnCurso();
    }
    private void cargarActividadesFiltradas(String estado) {
        String url = "http://34.71.103.241/listar_actividades_por_estado.php?estado=" + estado;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaActividades.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id_actividad = obj.getInt("id_actividad");
                            int id_asignacion = obj.getInt("id_asignacion");
                            String titulo = obj.getString("titulo");
                            String descripcion = obj.getString("descripcion");
                            String tipo = obj.getString("tipo");
                            String fecha_entrega = obj.getString("fecha_entrega");

                            Actividad actividad = new Actividad(
                                    id_actividad,
                                    id_asignacion,
                                    titulo,
                                    descripcion,
                                    tipo,
                                    fecha_entrega
                            );
                            listaActividades.add(actividad);
                        }

                        actividadAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                GestionActividadesActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Formatear la fecha seleccionada
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);

                    // Llamar a método para cargar actividades por fecha
                    cargarActividadesPorFecha(fechaSeleccionada);
                },
                year, month, day
        );
        datePickerDialog.show();
    }


    private void cargarActividadesPorFecha(String fecha) {
        String url = "http://34.71.103.241/listar_actividades_por_fecha.php?fecha=" + fecha;

        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return new JSONArray(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray response) {
                if (response != null) {
                    listaActividades.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int idActividad = obj.getInt("id_actividad");
                            int idAsignacion = obj.getInt("id_asignacion");
                            String titulo = obj.getString("titulo");
                            String descripcion = obj.getString("descripcion");
                            String tipo = obj.getString("tipo");
                            String fechaEntrega = obj.getString("fecha_entrega");

                            Actividad actividad = new Actividad(idActividad, idAsignacion, titulo, descripcion, tipo, fechaEntrega);
                            listaActividades.add(actividad);
                        }
                        actividadAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(GestionActividadesActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GestionActividadesActivity.this, "No hay actividades para esa fecha", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void mostrarFormularioAgregar() {
        edtActividadID.setText("");
        edtTitulo.setText("");
        edtDescripcion.setText("");
        edtFechaEntrega.setText("");
        edtValorTotal.setText("");

        edtTitulo.setEnabled(true);
        edtDescripcion.setEnabled(true);
        edtFechaEntrega.setEnabled(true);
        edtValorTotal.setEnabled(true);
        spinnerTipo.setEnabled(true);
        spinnerMateria.setEnabled(true);

        btnRegistrarActividad.setVisibility(View.VISIBLE);
        btnEditarActividad.setVisibility(View.GONE);
        btnEliminarActividad.setVisibility(View.GONE);

        edtTitulo.requestFocus();
    }

    private void mostrarFormularioEditar(Actividad actividad) {
        formularioScroll.setVisibility(View.VISIBLE);
        recyclerActividades.setVisibility(View.GONE);

        edtActividadID.setText(String.valueOf(actividad.id_actividad));
        edtTitulo.setText(actividad.titulo);
        edtDescripcion.setText(actividad.descripcion);
        edtFechaEntrega.setText(actividad.fecha_entrega);
        edtValorTotal.setText("");

        // Spinner tipo
        for (int i = 0; i < spinnerTipo.getCount(); i++) {
            if (spinnerTipo.getItemAtPosition(i).toString().equalsIgnoreCase(actividad.tipo)) {
                spinnerTipo.setSelection(i);
                break;
            }
        }

        // Activar/desactivar botones
        btnRegistrarActividad.setVisibility(View.GONE);
        btnEditarActividad.setVisibility(View.VISIBLE);
        btnEliminarActividad.setVisibility(View.GONE);

        spinnerMateria.setEnabled(false);
    }



    private String getActividadData(boolean incluirID) {
        try {
            String tipo = spinnerTipo.getSelectedItem().toString();
            Log.d("FECHA_DEBUG", "Fecha: " + edtFechaEntrega.getText().toString());

            StringBuilder data = new StringBuilder();

            if (incluirID) {
                data.append("id_actividad=").append(URLEncoder.encode(edtActividadID.getText().toString(), "UTF-8")).append("&");
            }

            data.append("id_asignacion=").append(idAsignacionSeleccionada).append("&");
            data.append("titulo=").append(URLEncoder.encode(edtTitulo.getText().toString(), "UTF-8")).append("&");
            data.append("descripcion=").append(URLEncoder.encode(edtDescripcion.getText().toString(), "UTF-8")).append("&");
            data.append("tipo=").append(URLEncoder.encode(tipo, "UTF-8")).append("&");
            data.append("fecha_entrega=").append(URLEncoder.encode(edtFechaEntrega.getText().toString(), "UTF-8")).append("&");
            data.append("valor_total=").append(URLEncoder.encode(edtValorTotal.getText().toString(), "UTF-8")).append("&");
            data.append("grado=").append(URLEncoder.encode(gradoSeleccionado, "UTF-8")).append("&");
            data.append("seccion=").append(URLEncoder.encode(seccionSeleccionada, "UTF-8")).append("&");
            data.append("id_usuario_maestro=").append(idUsuario);

            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private void cargarActividadesEnCurso() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/listar_actividades_en_progreso.php?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray actividadesJSON = new JSONArray(result.toString());
                listaActividades.clear();

                for (int i = 0; i < actividadesJSON.length(); i++) {
                    JSONObject obj = actividadesJSON.getJSONObject(i);
                    Actividad actividad = new Actividad(
                            obj.getInt("id_actividad"),
                            obj.getInt("id_asignacion"),
                            obj.getString("titulo"),
                            obj.getString("descripcion"),
                            obj.getString("tipo"),
                            obj.getString("fecha_entrega")
                    );
                    listaActividades.add(actividad);
                }

                runOnUiThread(() -> actividadAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ACTIVIDAD_ERROR", "Error cargando actividades", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar actividades", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }



    private void cargarMaterias() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/maestros/api_materias/materias_asignadas_html.php?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray arr = new JSONArray(result.toString());
                listaMaterias.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    int id_asignacion = item.getInt("id_asignacion");
                    String nombre = item.getString("nombre");
                    String grado = item.getString("grado");
                    String seccion = item.getString("seccion");
                    listaMaterias.add(new Materia(id_asignacion, nombre, grado, seccion));
                }

                runOnUiThread(() -> {
                    ArrayAdapter<Materia> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaMaterias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMateria.setAdapter(adapter);
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar materias", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void mostrarDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    int finalYear = year;
                    int finalMonth = monthOfYear + 1;
                    int finalDay = dayOfMonth;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(GestionActividadesActivity.this,
                            (view1, hourOfDay, minute) -> {
                                String fechaHora = String.format("%04d-%02d-%02d %02d:%02d:00",
                                        finalYear, finalMonth, finalDay, hourOfDay, minute);
                                edtFechaEntrega.setText(fechaHora);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private class ActividadTask extends AsyncTask<String, Void, String> {
        private final String urlString;

        public ActividadTask(String url) {
            this.urlString = url;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                Log.d("DATA_ENVIADA", params[0]); // Ver qué se está mandando

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    return sb.toString();
                } else {
                    return "Error en la conexión: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            Toast.makeText(GestionActividadesActivity.this, resultado, Toast.LENGTH_LONG).show();

            if (resultado.contains("Actividad registrada")) {
                formularioScroll.setVisibility(View.GONE);
                recyclerActividades.setVisibility(View.VISIBLE);
                cargarActividadesEnCurso(); // ✅ ahora sí, después del éxito
            }
        }

    }

}
