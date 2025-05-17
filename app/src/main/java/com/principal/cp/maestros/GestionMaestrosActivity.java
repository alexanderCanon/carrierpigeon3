package com.principal.cp.maestros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.principal.cp.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GestionMaestrosActivity extends AppCompatActivity {

    private RecyclerView recyclerMaestros;
    private MaestrosAdapter adapter;
    private ArrayList<Maestro> listaMaestros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_maestros);

        recyclerMaestros = findViewById(R.id.recyclerMaestros);
        recyclerMaestros.setLayoutManager(new LinearLayoutManager(this));

        listaMaestros = new ArrayList<>();
        adapter = new MaestrosAdapter(this, listaMaestros);
        recyclerMaestros.setAdapter(adapter);

        Button btnAgregar = findViewById(R.id.btnAgregarMaestro);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GestionMaestrosActivity.this, "Función agregar pendiente", Toast.LENGTH_SHORT).show();
            }
        });
        Button btnVolverMaestros = findViewById(R.id.btnVolverMaestros);
        btnVolverMaestros.setOnClickListener(v -> {
            Intent intent = new Intent(GestionMaestrosActivity.this, MaestroMainActivity.class);
            startActivity(intent);
            finish(); // opcional, para cerrar la activity actual
        });


        obtenerMaestros();
    }

    private void obtenerMaestros() {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/get_maestros.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONArray jsonArray = new JSONArray(sb.toString());
                listaMaestros.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    int id = obj.getInt("id_usuario");
                    String nombre = obj.getString("nombre");
                    String apellido = obj.getString("apellido");
                    String correo = obj.getString("correo");

                    listaMaestros.add(new Maestro(id, nombre, apellido, correo));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(GestionMaestrosActivity.this, "Error al obtener maestros", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
