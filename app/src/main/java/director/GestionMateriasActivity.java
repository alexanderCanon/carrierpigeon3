package director;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.principal.cp.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GestionMateriasActivity extends AppCompatActivity {


    private RecyclerView recyclerMaterias;
    private FloatingActionButton btnAgregarMateria;
    private List<AgregarMateria> listaMaterias = new ArrayList<>();
    private AgregarMateriaAdapter materiaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_materias);

        recyclerMaterias = findViewById(R.id.recyclerMaterias);
        btnAgregarMateria = findViewById(R.id.btnAgregarMateria);

        recyclerMaterias.setLayoutManager(new LinearLayoutManager(this));
        materiaAdapter = new AgregarMateriaAdapter(listaMaterias);
        recyclerMaterias.setAdapter(materiaAdapter);

        btnAgregarMateria.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarMateriaActivity.class);
            startActivity(intent);
        });
        cargarMaterias();


    } //fin onCreate

    private void cargarMaterias() {
        String url = "http://34.71.103.241/listar_materias.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaMaterias.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaMaterias.add(new AgregarMateria(
                                    obj.getInt("id_materia"),
                                    obj.getString("nombre"),
                                    obj.getString("descripcion")
                            ));
                        }
                        materiaAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar materias", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    } //fin cargarMaterias


}