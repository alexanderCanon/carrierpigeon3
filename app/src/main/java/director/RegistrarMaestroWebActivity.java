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

public class RegistrarMaestroWebActivity extends AppCompatActivity {

    private WebView webViewMaestro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_maestro_web);

        webViewMaestro = findViewById(R.id.webviewMaestro);
        WebSettings settings = webViewMaestro.getSettings();
        settings.setJavaScriptEnabled(true);
        webViewMaestro.setWebViewClient(new WebViewClient());
        webViewMaestro.loadUrl("http://34.71.103.241/registrar_maestro_html.html");
    }
}