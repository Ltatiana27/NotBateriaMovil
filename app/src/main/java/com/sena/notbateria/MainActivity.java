package com.sena.notbateria;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.sena.notbateria.data.ConfiguracionDbHelper;
import com.sena.notbateria.services.BatteryMonitorService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    private TextView tvBatteryLevel;
    private TextView tvLimiteActual;
    private ConfiguracionDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBatteryLevel = findViewById(R.id.tvBatteryLevel);
        tvLimiteActual = findViewById(R.id.tvLimiteActual);
        Button btnConfigurar = findViewById(R.id.btnConfigurar);
        Button btnIniciar = findViewById(R.id.btnIniciar);
        Button btnDetener = findViewById(R.id.btnDetener);

        dbHelper = new ConfiguracionDbHelper(this);
        actualizarNivelBateria();
        actualizarLimiteMostrado();

        btnConfigurar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
            startActivity(intent);
        });

        btnIniciar.setOnClickListener(v -> {
            solicitarPermisoNotificaciones();
            Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        });

        btnDetener.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
            stopService(serviceIntent);
        });

        solicitarPermisoNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarNivelBateria();
        actualizarLimiteMostrado();
    }

    private void actualizarNivelBateria() {
        int porcentaje = (int) (getBatteryLevel() * 100);
        tvBatteryLevel.setText(getString(R.string.nivel_bateria, porcentaje));
    }

    private void actualizarLimiteMostrado() {
        int limite = dbHelper.obtenerLimite();
        tvLimiteActual.setText("Umbral configurado: " + limite + "%");
    }

    private float getBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        if (batteryStatus == null) return 0f;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return level / (float) scale;
    }

    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }
}