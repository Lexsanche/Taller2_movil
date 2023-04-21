package com.example.taller2_movil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.taller2_movil.databinding.ActivityCamaraBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camara extends AppCompatActivity {

    private ActivityCamaraBinding cameraBinding;

    private final int CAMERA_PERMISSION_ID = 101;

    private final int VIDEO_PERMISSION_ID = 103;
    private final int GALLERY_PERMISSION_ID = 102;
    String cameraPerm = Manifest.permission.CAMERA;
    String storagePerm = Manifest.permission.READ_EXTERNAL_STORAGE;

    String currentPhotoPath;
    String currentVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraBinding = ActivityCamaraBinding.inflate(getLayoutInflater());
        setContentView(cameraBinding.getRoot());

        if(cameraBinding.videoChange.isChecked() == false){
            cameraBinding.showVideo.setVisibility(View.GONE);
            cameraBinding.showPicture.setVisibility(View.VISIBLE);
            cameraBinding.takePhoto.setOnClickListener(view -> {
                if (requestPermission(this, cameraPerm, CAMERA_PERMISSION_ID)){
                    startCamera(cameraBinding.getRoot());
                }
            });
        }
        cameraBinding.pickPhoto.setOnClickListener(view -> {
            if (requestGalleryPermission(this, storagePerm, GALLERY_PERMISSION_ID)){
                startGallery(cameraBinding.getRoot());
            }
        });

    }





    private boolean requestPermission(Activity context,String permission, int id){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    id);
            return false;
        }
        return true;
    }

    private boolean requestGalleryPermission(Activity context, String permission, int id){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{permission},
                    id);
            return false;
        }
        return true;
    }

    private void startVideoRecording(View view) {
        if (ActivityCompat.checkSelfPermission(this, cameraPerm) == PackageManager.PERMISSION_GRANTED){
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            if(takeVideoIntent != null){
                File video = null;

                try{
                    video = createVideoFile();
                } catch (IOException exception){
                    Toast.makeText(this, "No se generó el video", Toast.LENGTH_SHORT).show();
                }

                if (video != null){
                    Uri videoUri = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            video);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    startActivityForResult(takeVideoIntent, VIDEO_PERMISSION_ID);
                }
            }
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private File createVideoFile() throws IOException {
        // Crear un nombre de archivo de video
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "CAMARA_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(
                videoFileName,  /* prefijo */
                ".mp4",         /* sufijo */
                storageDir      /* directorio */
        );

        // Guardar la ruta del archivo de video para usarla con intents de ACTION_VIEW
        currentVideoPath = video.getAbsolutePath();
        return video;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_ID){
            // Verificar si la actividad está en un estado válido
            if (!isFinishing() && !isDestroyed()) {
                startCamera(cameraBinding.getRoot());
            }
        }
        if(requestCode == VIDEO_PERMISSION_ID){
            // Verificar si la actividad está en un estado válido
            if (!isFinishing() && !isDestroyed()) {
                startVideoRecording(cameraBinding.getRoot());
            }
        }
    }

    public void startCamera(View view){
        if (ContextCompat.checkSelfPermission(this, cameraPerm)
                == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }else{
            Toast.makeText(this, "No se pudo abrir la camara", Toast.LENGTH_SHORT).show();

        }
    }

    private void openCamera(){
        Intent takePhotIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePhotIntent != null){

            File photo = null;

            try{
                photo = createImageFile();
            } catch (IOException exeption){
                Toast toast = Toast.makeText(this, "No se genero la imagen", Toast.LENGTH_SHORT);
            }

            if (photo != null){
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photo);
                takePhotIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePhotIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    public void startGallery(View view){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage.createChooser(pickGalleryImage,"Escoge la aplicacion"), GALLERY_PERMISSION_ID);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CAMARA_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_PERMISSION_ID:
                    cameraBinding.showPicture.setImageURI(Uri.parse(currentPhotoPath));
                    break;
                case GALLERY_PERMISSION_ID:
                    Uri imageUri = data.getData();
                    cameraBinding.showPicture.setImageURI(imageUri);
                    break;
                case VIDEO_PERMISSION_ID:
                    cameraBinding.showVideo.setVideoURI(Uri.parse(currentVideoPath));
                    cameraBinding.showVideo.start();
                    break;
            }
        }
    }

    public void changeWay (View view){
        if(cameraBinding.videoChange.isChecked()) {
            cameraBinding.showVideo.setVisibility(View.VISIBLE);
            cameraBinding.showPicture.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraBinding.takePhoto.setOnClickListener(v -> {
                    if (requestPermission(this, cameraPerm, VIDEO_PERMISSION_ID)) {
                        startCamera(cameraBinding.getRoot());
                    }
                });
            } else {
                cameraBinding.takePhoto.setOnClickListener(v -> {
                    startVideoRecording(cameraBinding.getRoot());
                });
            }
        }else{
            cameraBinding.showVideo.setVisibility(View.GONE);
            cameraBinding.showPicture.setVisibility(View.VISIBLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraBinding.takePhoto.setOnClickListener(v -> {
                    if (requestPermission(this, cameraPerm, CAMERA_PERMISSION_ID)) {
                        startCamera(cameraBinding.getRoot());
                    }
                });
            } else {
                cameraBinding.takePhoto.setOnClickListener(v -> {
                    startCamera(cameraBinding.getRoot());
                });
            }
        }
    }
}