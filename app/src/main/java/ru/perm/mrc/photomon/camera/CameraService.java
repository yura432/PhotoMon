package ru.perm.mrc.photomon.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import ru.perm.mrc.photomon.activities.CameraActivity;

import static android.hardware.camera2.CaptureRequest.JPEG_ORIENTATION;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static ru.perm.mrc.photomon.activities.CameraActivity.LOG_TAG;

public class CameraService {

    private final String CAMERA_ID = "0";
    public final int APPROPRIATE_MAX_SIDE = 2100;
    public final double APPROPRIATE_RATIO = 4.0/3;

    private File savePath;
    private String fileName = "new";
    private int i = 0;

    private CameraActivity activity;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader mImageReader;
    private CameraManager cameraManager;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private TextureView imageView;
    private CameraCharacteristics cameraCharacteristics;
    private Size cameraSizeOfImages;
    private Size realSizeOfImages;
    private DisplayMetrics metrics;

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CameraService(CameraActivity activity, CameraManager cameraManager, TextureView imageView, DisplayMetrics metrics, File savePath) {

        this.savePath = savePath;
        this.cameraManager = cameraManager;
        this.imageView = imageView;
        this.metrics = metrics;
        this.activity = activity;


        try {
            cameraCharacteristics = cameraManager.getCameraCharacteristics(CAMERA_ID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        cameraSizeOfImages = getAppropriateSize(cameraCharacteristics);
        realSizeOfImages = new Size(min(cameraSizeOfImages.getHeight(),cameraSizeOfImages.getWidth()),max(cameraSizeOfImages.getHeight(),cameraSizeOfImages.getWidth()));
    }


    private Size getAppropriateSize(CameraCharacteristics cameraCharacteristics){

        Comparator<Size> comparator = new Comparator<Size>() {
            @Override
            public int compare(Size size, Size t1) {
                return max(size.getHeight(),size.getWidth())-max(t1.getHeight(),t1.getWidth());
            }
        };

        Size[] sizesJPG = Objects.requireNonNull(cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageFormat.JPEG);
        Arrays.sort(sizesJPG,comparator);
        ArrayList<Size> sizes = new ArrayList<>();

        for (int i = sizesJPG.length - 1; i >= 0; i--){
            int max = max(sizesJPG[i].getHeight(),sizesJPG[i].getWidth());
            if (max < APPROPRIATE_MAX_SIDE*0.9){
                break;
            }
            if (max < APPROPRIATE_MAX_SIDE*1.1){
                sizes.add(sizesJPG[i]);
            }
        }
        if (sizes.isEmpty()){
            sizes.add(sizesJPG[sizesJPG.length-1]);
        }
        Size size = sizes.get(0);
        while (!sizes.isEmpty()){
            size = sizes.get(0);
            float width = size.getWidth();
            float height = size.getHeight();
            double ratio = (width/height);
            if (((ratio < APPROPRIATE_RATIO*1.05)&& (ratio > APPROPRIATE_RATIO*0.95)) ||
                    ((height/width < APPROPRIATE_RATIO*1.05) && (height/width > APPROPRIATE_RATIO*0.95))){
                return new Size(size.getWidth(),size.getHeight());
            }
            sizes.remove(0);
        }
        return new Size(size.getWidth(),size.getHeight());
    }

    public void makePhoto (){
        fileName = Integer.toString(i);
        i++;

        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE );
            captureBuilder.set(JPEG_ORIENTATION,180);

            captureBuilder.addTarget(mImageReader.getSurface());

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {

                }
            };
            captureSession.capture(captureBuilder.build(), CaptureCallback, backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            Log.i(LOG_TAG, "Open activity_camera  with id:"+ cameraDevice.getId());

            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();

            Log.i(LOG_TAG, "disconnect activity_camera  with id:"+ cameraDevice.getId());
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i(LOG_TAG, "error! activity_camera id:"+camera.getId()+" error:"+error);
        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            File photoPath = new File(savePath, fileName + ".jpg");
            backgroundHandler.post(new ImageSaver(reader.acquireNextImage(),90, photoPath));
            Log.i(LOG_TAG, savePath.toString());
        }
    };

    private void setLayoutParams() {

        float ratio = (((float)realSizeOfImages.getHeight())/realSizeOfImages.getWidth());
        final int height = (int) round(min(metrics.heightPixels*.6, metrics.widthPixels*ratio));
        final int width = round(height/ratio);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                imageView.setLayoutParams(new LinearLayout.LayoutParams (width,height));

            }
        });

    }

    private void createCameraPreviewSession() {

        mImageReader = ImageReader.newInstance(cameraSizeOfImages.getWidth(), cameraSizeOfImages.getHeight(), ImageFormat.JPEG,3);
        mImageReader.setOnImageAvailableListener(onImageAvailableListener, null);

        setLayoutParams();



        SurfaceTexture texture = imageView.getSurfaceTexture();


//        assert texture != null;
//        texture.setDefaultBufferSize(min(sizeOfImages.getHeight(),sizeOfImages.getWidth()),max(sizeOfImages.getHeight(),sizeOfImages.getWidth()));
        Surface surface = new Surface(texture);

        try {
            final CaptureRequest.Builder builder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            builder.addTarget(surface);




            cameraDevice.createCaptureSession(Arrays.asList(surface,mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            captureSession = session;
                            try {
                                captureSession.setRepeatingRequest(builder.build(),null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) { }}, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public boolean isOpen() {
        return cameraDevice != null;
    }

    public void openCamera() throws SecurityException{
        startBackgroundThread();
        try {
                cameraManager.openCamera(CAMERA_ID,mCameraCallback, backgroundHandler);
        }
        catch (CameraAccessException e) {
            Log.i(LOG_TAG,e.toString());
        }
    }

    public void closeCamera() {

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        stopBackgroundThread();
    }
}