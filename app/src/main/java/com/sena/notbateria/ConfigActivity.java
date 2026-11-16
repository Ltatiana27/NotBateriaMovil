package com.sena.notbateria;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.sena.notbateria.data.ConfiguracionDbHelper;

public class ConfigActivity extends AppCompatActivity {

    private static final int LIMITE_MIN = 50;
    private static final int LIMITE_MAX = 100;

    private TextView tvValorActual;
    private EditText etLimite;
    private ConfiguracionDbHelper dbHelper;
    private int limiteActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        tvValorActual = findViewById(R.id.tvValorActual);
        SeekBar seekBarLimite = findViewById(R.id.seekBarLimite);
        etLimite = findViewById(R.id.etLimite);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        dbHelper = new ConfiguracionDbHelper(this);
        limiteActual = dbHelper.obtenerLimite();

        seekBarLimite.setMax(LIMITE_MAX - LIMITE_MIN);
        seekBarLimite.setProgress(limiteActual - LIMITE_MIN);
        tvValorActual.setText(limiteActual + "%");
        etLimite.setText(String.valueOf(limiteActual));

        seekBarLimite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                limiteActual = progress + LIMITE_MIN;
                tvValorActual.setText(limiteActual + "%");
                etLimite.setText(String.valueOf(limiteActual));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnGuardar.setOnClickListener(v -> {
            String texto = etLimite.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show();
                return;
            }
            int valor = Integer.parseInt(texto);
            if (valor < LIMITE_MIN) valor = LIMITE_MIN;
            if (valor > LIMITE_MAX) valor = LIMITE_MAX;

            dbHelper.guardarLimite(valor);
            Toast.makeText(this, "Umbral guardado: " + valor + "%", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancelar.setOnClickListener(v -> finish());
    }
}