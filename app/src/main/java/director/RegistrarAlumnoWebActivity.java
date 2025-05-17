package director;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.principal.cp.R;

public class RegistrarAlumnoWebActivity extends AppCompatActivity {
    private WebView webViewFormulario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_alumno_web);
        webViewFormulario = findViewById(R.id.webviewFormulario);
        WebSettings webSettings = webViewFormulario.getSettings();
        webSettings.setJavaScriptEnabled(true); // Permite que funcione el JS del formulario

        webViewFormulario.setWebViewClient(new WebViewClient()); // Para no abrir navegador externo

        // Cargar la URL de tu formulario web
        webViewFormulario.loadUrl("http://34.71.103.241/registrar_alumno_html.html");
    } // Fin onCreate
}