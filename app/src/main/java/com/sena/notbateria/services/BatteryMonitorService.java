package com.sena.notbateria.services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.sena.notbateria.data.ConfiguracionDbHelper;
import com.sena.notbateria.utils.NotificationHelper;

/**
 * Servicio en segundo plano que monitorea el nivel de batería
 * y emite una notificación cuando se alcanza el umbral configurado.
 */
public class BatteryMonitorService extends Service {

    private static final int FOREGROUND_ID = 2001;
    private BroadcastReceiver batteryReceiver;
    private NotificationHelper notificationHelper;
    private ConfiguracionDbHelper dbHelper;
    private boolean yaNotifico = false;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        dbHelper = new ConfiguracionDbHelper(this);
        iniciarForeground();
        registrarReceiverBateria();
    }

    private void iniciarForeground() {
        Notification notificacion = new NotificationCompat.Builder(this, "notbateria_channel")
                .setContentTitle("NotBateria activo")
                .setContentText("Monitoreando el nivel de batería en segundo plano")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(FOREGROUND_ID, notificacion);
    }

    private void registrarReceiverBateria() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level == -1 || scale == -1) return;

                int porcentaje = (int) ((level / (float) scale) * 100);
                int limite = dbHelper.obtenerLimite();

                if (porcentaje >= limite && !yaNotifico) {
                    notificationHelper.mostrarNotificacionBateria(porcentaje);
                    yaNotifico = true;
                }
                if (porcentaje < limite) {
                    yaNotifico = false;
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }
}