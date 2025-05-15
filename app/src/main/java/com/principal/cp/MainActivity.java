package com.principal.cp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    //esta clase se uso para fines de prueba de conexion con la base de datos, no es relevante
    private Button buttonLogout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerViewAnuncios;
    private AnuncioAdapter anuncioAdapter;
    private List<AnuncioPrueba> listaAnuncios;

    private static final String URL_ANUNCIOS = "http://34.71.103.241/obtener_anuncios_prueba.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogout = findViewById(R.id.buttonLogout);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar la sesión en SharedPreferences
                editor.putBoolean("isLoggedIn", false); //para cerrar la sesion activa
                editor.remove("user_id"); //elimina los datos obtenidos por JSON
                editor.remove("tipo_usuario");
                editor.remove("nombre");
                editor.remove("apellido");
                editor.remove("telefono"); //aquí borra todo esto para que sea un cierre de sesion correcto
                editor.apply();

                // Redirigir a la LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class); //nos regresa al Login
                startActivity(intent);
                finish(); // Cierra la MainActivity
            }
        });

        recyclerViewAnuncios = findViewById(R.id.recyclerViewAnuncios);
        recyclerViewAnuncios.setLayoutManager(new LinearLayoutManager(this));

        listaAnuncios = new ArrayList<>();
        anuncioAdapter = new AnuncioAdapter(listaAnuncios);
        recyclerViewAnuncios.setAdapter(anuncioAdapter);

        new ObtenerAnunciosTask().execute(URL_ANUNCIOS);

    }
    private class ObtenerAnunciosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                Log.e("Error HTTP", e.getMessage(), e);
                return null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Log.d("Respuesta JSON", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String titulo = jsonObject.getString("titulo");
                        String contenido = jsonObject.getString("contenido");
                        String fechaPublicacion = jsonObject.getString("fecha_publicacion");
                        listaAnuncios.add(new AnuncioPrueba(id, titulo, contenido, fechaPublicacion));
                    }
                    anuncioAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Error JSON", "Error al parsear JSON", e);
                    Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error al obtener los anuncios", Toast.LENGTH_SHORT).show();
            }
        }
    }

}