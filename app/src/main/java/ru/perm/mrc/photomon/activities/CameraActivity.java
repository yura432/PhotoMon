package ru.perm.mrc.photomon.activities;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ru.perm.mrc.photomon.R;
import ru.perm.mrc.photomon.camera.CameraService;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {


    public static final String LOG_TAG = "myLogs";


    private CameraService cameraService = null;
    private TextureView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);




        Log.d(LOG_TAG, "Запрашиваем разрешение");
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }


        Button buttonToMakeShot = findViewById(R.id.button);
        imageView = findViewById(R.id.textureView);
        imageView.setSurfaceTextureListener(this);

        buttonToMakeShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!cameraService.isOpen()) cameraService.openCamera();
                cameraService.makePhoto();
            }
        });

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();


        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraService = new CameraService(this, cameraManager, imageView, metrics, getExternalFilesDir(null));


    }

    @Override
    public void onPause() {
        cameraService.closeCamera();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != imageView.getSurfaceTexture())
            cameraService.openCamera();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            cameraService.openCamera();

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }
}