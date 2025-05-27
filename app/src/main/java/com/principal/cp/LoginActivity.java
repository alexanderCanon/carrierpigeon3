package com.principal.cp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.principal.cp.maestros.MateriasAsignadasActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import director.DirectorMainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword; private Button buttonLogin; private TextView textViewForgotPass;
    private RequestQueue requestQueue;
    private static final String URL_LOGIN = "http://34.71.103.241/login_usuario.php"; // Reemplaza
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences; private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PermisosUtil.pedirPermisoNotificaciones(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_general",
                    "Canal general",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para avisos importantes");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPass = findViewById(R.id.textViewRegisterLink);
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

        textViewForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoRecuperacion();
            }
        });

    } //FIN ONCREATE

    // ✅ Aquí va el callback

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Maneja y guarda el resultado
        PermisosUtil.manejarResultadoPermisos(requestCode, grantResults, this);
        // ✅ Mostrar advertencia si lo rechazó
        if (!PermisosUtil.permisoNotificacionesConcedido(this)) {
            Toast.makeText(this, "⚠️ No recibirás avisos hasta que actives las notificaciones", Toast.LENGTH_LONG).show();
        }
    }

    private void loginUser(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    Log.d("LoginActivity", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (!jsonObject.getBoolean("error")) {
                            // Extraer datos del JSON
                            int idUsuario = jsonObject.getInt("id_usuario");
                            String tipoUsuario = jsonObject.getString("tipo_usuario");

                            // Guardar en SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("id_usuario", idUsuario);
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("tipo_usuario", tipoUsuario);
                            editor.putString("nombre", jsonObject.getString("nombre"));
                            editor.putString("apellido", jsonObject.getString("apellido"));
                            editor.putString("telefono", jsonObject.getString("telefono"));
                            editor.apply();

                            // Obtener token FCM y enviarlo al servidor
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            String token = task.getResult();
                                            Log.d("FCM_TOKEN", "Token obtenido: " + token);
                                            enviarTokenAlServidor(idUsuario, token);
                                        } else {
                                            Log.w("FCM_TOKEN", "No se pudo obtener token FCM", task.getException());
                                        }
                                    });

                            // Mensaje de confirmación
                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            // Redirigir según el tipo de usuario
                            Intent intent;
                            switch (tipoUsuario) {
                                case "admin":
                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                    break;
                                case "maestro":
                                    intent = new Intent(LoginActivity.this, MateriasAsignadasActivity.class);
                                    intent.putExtra("id_usuario", idUsuario); // Enviar también por extra si lo necesitas
                                    break;
                                case "padre":
                                    intent = new Intent(LoginActivity.this, PadreMainActivity.class);
                                    break;
                                case "director":
                                    intent = new Intent(LoginActivity.this, DirectorMainActivity.class);
                                    break;
                                default:
                                    intent = new Intent(LoginActivity.this, MainActivity.class); // Fallback
                                    break;
                            }

                            startActivity(intent);
                            finish(); // cerrar LoginActivity

                        } else {
                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Error: " + error.getMessage());
                    Toast.makeText(LoginActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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

    public void enviarTokenAlServidor(int idUsuario, String token) {
        String url = "http://34.71.103.241/actualizar_token.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Log.d("FCM_TOKEN", "Respuesta del servidor: " + response),

                error -> Log.e("FCM_TOKEN", "Error al enviar token", error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", String.valueOf(idUsuario));
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void mostrarDialogoRecuperacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar contraseña");

        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Ingresa tu correo electrónico");
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(inputEmail);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = inputEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    enviarSolicitudRecuperacion(email);
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }

    private void enviarSolicitudRecuperacion(String email) {
        String url = "http://34.71.103.241/recuperar_contrasena.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("ok")) {
                            Toast.makeText(getApplicationContext(), "Correo de recuperación enviado", Toast.LENGTH_LONG).show();
                        } else if (status.equals("not_found")) {
                            Toast.makeText(getApplicationContext(), "Este correo no está registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Error de red", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}