package mifirebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.principal.cp.LoginActivity;
import com.principal.cp.R;
import android.content.SharedPreferences;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    LoginActivity lga = new LoginActivity();
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Nuevo token generado: " + token);
        //BLOQUE AGREGADO EL 27 DE MAYO
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            int idUsuario = prefs.getInt("id_usuario", -1);
            if (idUsuario != -1) {
                lga.enviarTokenAlServidor(idUsuario, token); // Usa tu metodo si lo haces accesible desde aquí
            }
        }
    } //FIN ONNEWTOKEN

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String cuerpo = remoteMessage.getNotification().getBody();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_general")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // o tu ícono personalizado
                    .setContentTitle(titulo)
                    .setContentText(cuerpo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat manager = NotificationManagerCompat.from(this);

            // ✅ Verificar permiso antes de mostrar la notificación
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                manager.notify(1, builder.build());
            } else {
                Log.w("FCM", "❌ Permiso de notificaciones no concedido");
            }
        }
    } //FIN ONMESSAGE

//    private void sendTokenToServer(String token) {
//        new Thread(() -> {
//            try {
//                URL url = new URL("http://34.71.103.241/save_token.php"); // Cambia esto
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/json");
//                conn.setDoOutput(true);
//
//                String jsonInput = "{\"token\":\"" + token + "\"}";
//                OutputStream os = conn.getOutputStream();
//                os.write(jsonInput.getBytes("UTF-8"));
//                os.close();
//
//                int responseCode = conn.getResponseCode();
//                Log.d(TAG, "Token enviado. Respuesta: " + responseCode);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

}
