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

public class GestionPadresActivity extends AppCompatActivity {

    private WebView webViewPadres;
    private Button btnAgregarPadre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_padres);
        webViewPadres = findViewById(R.id.webviewPadres);
        btnAgregarPadre = findViewById(R.id.btnAgregarPadre);

        WebSettings settings = webViewPadres.getSettings();
        settings.setJavaScriptEnabled(true);
        webViewPadres.setWebViewClient(new WebViewClient());
        webViewPadres.loadUrl("http://34.71.103.241/padres_lista_html.html");

        btnAgregarPadre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionPadresActivity.this, RegistrarPadreWebActivity.class);
                startActivity(intent);
            }
        });
    }
}