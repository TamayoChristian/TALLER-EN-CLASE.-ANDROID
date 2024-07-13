package com.example.alertaapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Alerta extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 201;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private MediaRecorder recorder = null;
    private String fileName = null;

    private EditText phoneEditText;
    private EditText msmEditText;
    private Button saveButton;
    private ImageButton sosButton;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);

        // Verificar y solicitar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        // Configurar tabs
        setupTabs();

        // Referencias a los elementos de UI
        phoneEditText = findViewById(R.id.phone_config);
        msmEditText = findViewById(R.id.msm_config);
        saveButton = findViewById(R.id.save_config);
        sosButton = findViewById(R.id.sosButton);

        // Configurar el listener para el botón de guardar
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarConfiguracion();
            }
        });

        // Configurar el listener para el ImageButton
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarAudio();
            }
        });

        // Ruta del archivo de audio
        fileName = getExternalFilesDir(null).getAbsolutePath() + "/audiorecordtest.3gp";
    }

    private void setupTabs() {
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab 1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tab 1");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab 2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tab 2");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab 3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Tab 3");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab 4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Tab 4");
        tabHost.addTab(spec);
    }

    private void guardarConfiguracion() {
        String telefono = phoneEditText.getText().toString();
        String mensaje = msmEditText.getText().toString();

        File file = new File(getExternalFilesDir(null), "config.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Teléfono: " + telefono + "\n");
            writer.write("Mensaje: " + mensaje + "\n");
            Toast.makeText(this, "Configuración guardada en: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la configuración", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isRecording = false;

    private void grabarAudio() {
        if (!permissionToRecordAccepted || !permissionToWriteAccepted) {
            Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (!isRecording) {
                // Iniciar grabación
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    recorder.prepare();
                    recorder.start();
                    isRecording = true;
                    Toast.makeText(this, "Grabando audio...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al preparar la grabadora", Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Estado ilegal al preparar o iniciar la grabadora", Toast.LENGTH_SHORT).show();
                    recorder.release();
                    recorder = null;
                }
            } else {
                // Detener grabación
                try {
                    recorder.stop();
                    Toast.makeText(this, "Grabación detenida. Archivo guardado en: " + fileName, Toast.LENGTH_SHORT).show();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al detener la grabación", Toast.LENGTH_SHORT).show();
                } finally {
                    recorder.release();
                    recorder = null;
                    isRecording = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
            if (recorder != null) {
                recorder.release();
                recorder = null;
                isRecording = false;
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (!permissionToRecordAccepted || !permissionToWriteAccepted) {
                Toast.makeText(this, "Permisos necesarios no concedidos. La aplicación se cerrará.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
