package com.sena.notbateria.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.sena.notbateria.R;

/**
 * Helper para crear y gestionar las notificaciones locales.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "notbateria_channel";
    private static final String CHANNEL_NAME = "Notificaciones de batería";
    private static final int NOTIFICATION_ID = 1001;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        crearCanalNotificacion();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            canal.setDescription("Avisos cuando la batería alcanza el umbral configurado");
            notificationManager.createNotificationChannel(canal);
        }
    }

    /**
     * Muestra la notificación de batería casi llena.
     */
    public void mostrarNotificacionBateria(int porcentaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(context.getString(R.string.notif_titulo))
                .setContentText(context.getString(R.string.notif_mensaje, porcentaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
