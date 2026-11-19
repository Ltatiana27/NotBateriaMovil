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
        android.util.Log.d("BatteryMonitor", ">>> onCreate() iniciado <<<");

        try {
            notificationHelper = new NotificationHelper(this);
            android.util.Log.d("BatteryMonitor", "NotificationHelper creado");
        } catch (Exception e) {
            android.util.Log.e("BatteryMonitor", "Error NotificationHelper: " + e.getMessage(), e);
            stopSelf();
            return;
        }

        try {
            dbHelper = new ConfiguracionDbHelper(this);
            android.util.Log.d("BatteryMonitor", "DbHelper creado, límite=" + dbHelper.obtenerLimite());
        } catch (Exception e) {
            android.util.Log.e("BatteryMonitor", "Error DbHelper: " + e.getMessage(), e);
        }

        try {
            iniciarForeground();
            android.util.Log.d("BatteryMonitor", "startForeground() ejecutado con éxito");
        } catch (Exception e) {
            android.util.Log.e("BatteryMonitor", "Error startForeground: " + e.getMessage(), e);
            stopSelf();
            return;
        }

        registrarReceiverBateria();
        android.util.Log.d("BatteryMonitor", ">>> Servicio completamente iniciado <<<");
    }

    private void iniciarForeground() {
        Notification notificacion = notificationHelper.crearNotificacionForeground();
        startForeground(FOREGROUND_ID, notificacion);
    }

    private void registrarReceiverBateria() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                android.util.Log.d("BatteryMonitor", "onReceive: level=" + level + ", scale=" + scale);
                if (level == -1 || scale == -1) return;

                int porcentaje = (int) ((level / (float) scale) * 100);
                int limite = dbHelper.obtenerLimite();
                android.util.Log.d("BatteryMonitor", "Porcentaje=" + porcentaje + ", Límite=" + limite + ", yaNotifico=" + yaNotifico);

                if (porcentaje >= limite && !yaNotifico) {
                    android.util.Log.d("BatteryMonitor", ">>> EMITIENDO NOTIFICACIÓN DE UMBRAL <<<");
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