package com.sena.notbateria.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.sena.notbateria.R;

/**
 * Helper para crear y gestionar las notificaciones locales.
 * Usa dos canales separados:
 * - Uno silencioso para el foreground service (persistente)
 * - Uno con sonido para la alerta de umbral
 */
public class NotificationHelper {

    // Canal silencioso para el servicio en primer plano
    private static final String CHANNEL_ID_FOREGROUND = "notbateria_foreground";
    private static final String CHANNEL_NAME_FOREGROUND = "Servicio en segundo plano";

    // Canal con sonido para la alerta de umbral
    private static final String CHANNEL_ID_UMBRAL = "notbateria_umbral";
    private static final String CHANNEL_NAME_UMBRAL = "Alertas de batería";

    private static final int NOTIFICATION_ID_UMBRAL = 1001;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        crearCanales();
    }

    /**
     * Crea los dos canales de notificación con importancias distintas.
     */
    private void crearCanales() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal silencioso para el servicio en primer plano
            NotificationChannel canalForeground = new NotificationChannel(
                    CHANNEL_ID_FOREGROUND,
                    CHANNEL_NAME_FOREGROUND,
                    NotificationManager.IMPORTANCE_MIN);
            canalForeground.setDescription("Notificación persistente del servicio en segundo plano");
            canalForeground.setSound(null, null);
            canalForeground.enableVibration(false);
            canalForeground.setShowBadge(false);
            notificationManager.createNotificationChannel(canalForeground);

            // Canal con sonido para la alerta de umbral
            NotificationChannel canalUmbral = new NotificationChannel(
                    CHANNEL_ID_UMBRAL,
                    CHANNEL_NAME_UMBRAL,
                    NotificationManager.IMPORTANCE_HIGH);
            canalUmbral.setDescription("Alerta cuando la batería alcanza el umbral configurado");
            notificationManager.createNotificationChannel(canalUmbral);
        }
    }

    /**
     * Crea la notificación del servicio en primer plano (SIN sonido).
     * La construye el servicio y la pasa a startForeground().
     */
    public Notification crearNotificacionForeground() {
        return new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setContentTitle("NotBateria activo")
                .setContentText("Monitoreando el nivel de batería en segundo plano")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSilent(true)
                .setOngoing(true)
                .build();
    }

    /**
     * Muestra la notificación de umbral (CON sonido, una sola vez).
     */
    public void mostrarNotificacionBateria(int porcentaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_UMBRAL)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(context.getString(R.string.notif_titulo))
                .setContentText(context.getString(R.string.notif_mensaje, porcentaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID_UMBRAL, builder.build());
    }
}