package mifirebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Nuevo token generado: " + token);

        // Aquí puedes guardar el token localmente si quieres
        // O enviarlo directamente al backend si el usuario está logueado
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Aquí puedes personalizar qué hacer si la app está en primer plano
        Log.d("FCM_MSG", "Mensaje recibido: " + remoteMessage.getNotification().getBody());
    }

    private void sendTokenToServer(String token) {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.71.103.241/save_token.php"); // Cambia esto
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInput = "{\"token\":\"" + token + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(jsonInput.getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Token enviado. Respuesta: " + responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
