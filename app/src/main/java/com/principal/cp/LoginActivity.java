package com.principal.cp;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.maestros.MaestroMainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegisterLink;
    private RequestQueue requestQueue;
    private static final String URL_LOGIN = "http://34.71.103.241/login_usuario.php"; // Reemplaza
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Verificar si ya hay una sesión activa
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToMainActivity();
            finish();
        }
        Log.d("LoginActivity", "isLoggedIn al onCreate: " + sharedPreferences.getBoolean("isLoggedIn", false));

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //FIREBASEMESSAGING PART
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Error al obtener el token", task.getException());
                        return;
                    }

                    // Token obtenido
                    String token = task.getResult();
                    Log.d(TAG, "Token FCM: " + token);

                    // Enviar el token al servidor
                    new Thread(() -> {
                        try {
                            URL url = new URL("http://34.71.103.241/save_token.php"); // CAMBIA esto por tu URL real
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            String jsonInput = "{\"token\":\"" + token + "\"}";
                            OutputStream os = conn.getOutputStream();
                            os.write(jsonInput.getBytes("UTF-8"));
                            os.close();

                            int responseCode = conn.getResponseCode();
                            Log.d(TAG, "Respuesta del servidor: " + responseCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
    }

    private void loginUser(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginActivity", "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                // Guardar información del usuario en SharedPreferences
                                editor.putBoolean("isLoggedIn", true); //para definir que ya hay una sesion activa
                                editor.putInt("user_id", jsonObject.getInt("id_usuario"));
                                String tipoUsuario = jsonObject.getString("tipo_usuario");
                                editor.putString("tipo_usuario", jsonObject.getString("tipo_usuario"));
                                editor.putString("nombre", jsonObject.getString("nombre"));
                                editor.putString("apellido", jsonObject.getString("apellido"));
                                editor.putString("telefono", jsonObject.getString("telefono"));
                                editor.apply();

                                Intent intent;
                                switch (tipoUsuario) {
                                    case "admin":
                                        intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                        break;
                                    case "maestro":
                                        intent = new Intent(LoginActivity.this, MaestroMainActivity.class);
                                        break;
                                    case "padre":
                                        intent = new Intent(LoginActivity.this, PadreMainActivity.class);
                                        break;
                                    default:
                                        intent = new Intent(LoginActivity.this, MainActivity.class); // Fallback
                                        break;
                                }
                                startActivity(intent);
                                finish(); // Cerrar la actividad de login
                            }
                             else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LoginActivity", "Error: " + error.getMessage());
                        Toast.makeText(LoginActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


}