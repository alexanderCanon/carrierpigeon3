package com.principal.cp;

import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    //ESTA ACTIVITY CULERA ES PARA REGISTRAR USUARIOS, SOLO LA VA A USAR EL ADMIN PERO PENDIENTE POR AHORA
    private EditText editTextNombre, editTextApellido, editTextEmail, editTextPassword, editTextTelefono;
    private Spinner spinnerTipoUsuario;
    private Button buttonRegister;
    private RequestQueue requestQueue;
    private static final String URL_REGISTER = "http://34.173.185.235/registrar_usuario.php"; // Reemplaza

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuarioRegistro);
        editTextNombre = findViewById(R.id.editTextNombreRegistro);
        editTextApellido = findViewById(R.id.editTextApellidoRegistro);
        editTextEmail = findViewById(R.id.editTextEmailRegistro);
        editTextPassword = findViewById(R.id.editTextPasswordRegistro);
        editTextTelefono = findViewById(R.id.editTextTelefonoRegistro);
        buttonRegister = findViewById(R.id.buttonRegister);
        requestQueue = Volley.newRequestQueue(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();
        final String nombre = editTextNombre.getText().toString().trim();
        final String apellido = editTextApellido.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String telefono = editTextTelefono.getText().toString().trim();

        // Validaciones básicas (puedes agregar más)
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || tipoUsuario.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, complete los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RegisterActivity", "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                finish(); // Volver a la pantalla de login
                            } else {
                                Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RegisterActivity", "Error: " + error.getMessage());
                        Toast.makeText(RegisterActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tipo_usuario", tipoUsuario);
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("email", email);
                params.put("password", password);
                params.put("telefono", telefono);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}