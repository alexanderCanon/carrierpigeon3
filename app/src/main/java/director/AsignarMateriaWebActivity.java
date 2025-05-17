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

public class AsignarMateriaWebActivity extends AppCompatActivity {

    private WebView webViewAsignarMateria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asignar_materia_web);
        webViewAsignarMateria = findViewById(R.id.webviewAsignarMateria);
        WebSettings settings = webViewAsignarMateria.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webViewAsignarMateria.setWebViewClient(new WebViewClient());
        webViewAsignarMateria.loadUrl("http://34.71.103.241/director/web_director/asignar_materia_html.html");
    }
}