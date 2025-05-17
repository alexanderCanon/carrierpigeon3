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

public class RegistrarPadreWebActivity extends AppCompatActivity {

    private WebView webViewPadre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_padre_web);

        webViewPadre = findViewById(R.id.webviewPadre);
        WebSettings settings = webViewPadre.getSettings();
        settings.setJavaScriptEnabled(true);
        webViewPadre.setWebViewClient(new WebViewClient());
        webViewPadre.loadUrl("http://34.71.103.241/registrar_padre_html.html");
    }
}