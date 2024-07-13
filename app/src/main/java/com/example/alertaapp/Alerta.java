package com.example.alertaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alerta extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 201;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 202;

    private String currentPhotoPath;
    private ImageView photoImageView;
    private EditText photoNameEditText;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String audioFilePath;

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);

        // Verificar y solicitar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        // Configurar tabs
        setupTabs();

        // Referencias a los elementos de UI
        photoImageView = findViewById(R.id.photo);
        photoNameEditText = findViewById(R.id.msm_foto);
        Button takePhotoButton = findViewById(R.id.btn_take_photo);
        Button sendPhotoButton = findViewById(R.id.btn_send_photo);
        ImageButton recordButton = findViewById(R.id.sosButton);  // Botón de grabar

        // Asegurarse de que el botón de enviar foto esté visible
        sendPhotoButton.setVisibility(View.VISIBLE);

        // Configurar el listener para el botón de tomar foto
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Configurar el listener para el botón de enviar foto
        sendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhotoToFile();
            }
        });

        // Configurar el listener para el botón de grabar
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Asegurarse de que hay una actividad de cámara para manejar el intento
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crear el archivo donde irá la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error al crear el archivo
                ex.printStackTrace();
            }
            // Continuar solo si el archivo fue creado correctamente
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.alertaapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crear un nombre de archivo único con la fecha y hora
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        // Guardar un archivo: ruta para usar con los Intentos ACTION_VIEW
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Mostrar la imagen capturada en el ImageView
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                photoImageView.setImageURI(Uri.fromFile(file));
                // Asegurarse de que el botón de enviar foto esté visible
                findViewById(R.id.btn_send_photo).setVisibility(View.VISIBLE);
            }
        }
    }

    private void savePhotoToFile() {
        String photoName = photoNameEditText.getText().toString();
        if (currentPhotoPath != null && !photoName.isEmpty()) {
            File file = new File(currentPhotoPath);
            File newFile = new File(file.getParent(), photoName + ".jpg");
            if (file.renameTo(newFile)) {
                Toast.makeText(this, "Foto guardada como: " + newFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Nombre de foto vacío o no hay foto capturada", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + ".3gp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        audioFilePath = new File(storageDir, audioFileName).getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        Toast.makeText(this, "Grabación guardada en: " + audioFilePath, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                Toast.makeText(this, "Permisos necesarios no concedidos. La aplicación se cerrará.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
