package com.principal.cp;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class PermisosUtil {
    public static final int CODIGO_PERMISO_NOTIFICACIONES = 1001;
    // Metodo para pedir el permiso si es Android 13+
    public static void pedirPermisoNotificaciones(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        CODIGO_PERMISO_NOTIFICACIONES);
            }
        }
    }
    // Metodo para manejar la respuesta
    public static void manejarResultadoPermisos(int requestCode, @NonNull int[] grantResults, Context context) {
        if (requestCode == CODIGO_PERMISO_NOTIFICACIONES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISOS", "✅ Permiso de notificaciones concedido");
                guardarResultadoPermiso(context, true);
            } else {
                Log.w("PERMISOS", "❌ Permiso de notificaciones denegado");
                guardarResultadoPermiso(context, false);
            }
        }
    } //FIN METODO MANEJAR RESULTADO PERMISOS
    public static void guardarResultadoPermiso(Context context, boolean concedido) {
        SharedPreferences prefs = context.getSharedPreferences("permisos", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("permiso_notificaciones", concedido).apply();
    }

    public static boolean permisoNotificacionesConcedido(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("permisos", Context.MODE_PRIVATE);
        return prefs.getBoolean("permiso_notificaciones", false);
    }
}

