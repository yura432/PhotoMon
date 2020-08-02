package ru.perm.mrc.photomon.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageSaver implements Runnable {

    private final Image image;
    private final File fileToSave;
    private final int rotateDegree;

    ImageSaver(Image image, int rotateDegree, File fileToSave) {
        this.image = image;
        this.fileToSave = fileToSave;
        this.rotateDegree = rotateDegree;
    }

    @Override
    public void run() {

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        try {

            FileOutputStream os = new FileOutputStream(fileToSave);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(rotateDegree);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,90, os);


            os.flush();
            os.close();
            image.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
