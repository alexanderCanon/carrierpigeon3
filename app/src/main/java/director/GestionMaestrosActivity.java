package director;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;

public class GestionMaestrosActivity extends AppCompatActivity {

    private WebView webViewMaestros;
    private Button btnAgregarMaestro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_maestros);

        webViewMaestros = findViewById(R.id.webviewMaestros);
        btnAgregarMaestro = findViewById(R.id.btnAgregarMaestro);

        WebSettings settings = webViewMaestros.getSettings();
        settings.setJavaScriptEnabled(true);
        webViewMaestros.setWebViewClient(new WebViewClient());
        webViewMaestros.loadUrl("http://34.71.103.241/maestros_lista_html.html");

        btnAgregarMaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionMaestrosActivity.this, RegistrarMaestroWebActivity.class);
                startActivity(intent);
            }
        });

        Button btnAsignarMateria = findViewById(R.id.btnAsignarMateria);
        btnAsignarMateria.setOnClickListener(v -> {
            Intent intent = new Intent(GestionMaestrosActivity.this, AsignarMateriaWebActivity.class);
            startActivity(intent);
        });
    }
}