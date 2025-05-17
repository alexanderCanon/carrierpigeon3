package director;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;

public class GestionAlumnosActivity extends AppCompatActivity {
    private EditText edtGrado, edtSeccion;
    private Button btnBuscar, btnAgregar;
    private WebView webViewAlumnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_alumnos2);
        edtGrado = findViewById(R.id.edtGrado);
        edtSeccion = findViewById(R.id.edtSeccion);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregarAlumno);
        webViewAlumnos = findViewById(R.id.webviewAlumnos);
        WebSettings webSettings = webViewAlumnos.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewAlumnos.setWebViewClient(new WebViewClient());

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String grado = edtGrado.getText().toString().trim();
                String seccion = edtSeccion.getText().toString().trim();

                if (!grado.isEmpty() && !seccion.isEmpty()) {
                    String url = "http://34.71.103.241/alumnos_lista_html.html?grado=" + grado + "&seccion=" + seccion;
                    webViewAlumnos.loadUrl(url);
                }
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reutiliza el formulario web que ya hicimos
                Intent intent = new Intent(GestionAlumnosActivity.this, RegistrarAlumnoWebActivity.class);
                startActivity(intent);
            }
        });


    } // Fin onCreate
}