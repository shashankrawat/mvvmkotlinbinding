package com.mvvmkotlinbinding.utils.camera_utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.mvvmkotlinbinding.utils.FileProviderUtilsHelper;
import com.mvvmkotlinbinding.utils.ScalingUtilities;
import com.mvvmwithdatabinding.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;


@SuppressWarnings("deprecation")
public class CameraUtils implements FileProviderUtilsHelper {


    private static final String TAG = "CameraUtils";

    /**
     * The current {@code Context} of the app
     */
    private WeakReference<Context> weakContext;

    /**
     * The path of the last picture taken.
     */
    private String currentPhotoPath = "";

    /**
     * {@code true} if you are currently compressing an image, {@code false} if not.
     */
    private static boolean isCompressing = false;

    /**
     * The listener for image processing
     */
    public interface ImageProcessingListener {

        /**
         * Called before the compression starts
         */
        void preCompression();

        /**
         * Called after the compression has completed successfully.
         *
         * @param oldPath The {@link File#getAbsolutePath()} for the image that was compressed
         *                and then deleted.
         * @param newPath The {@link File#getAbsolutePath()} for the image that was created when
         *                the {@code oldPath} was compressed. Update your reference to teh image
         *                file.
         */
        void postCompression(@NonNull String oldPath, @NonNull String newPath);

        /**
         * Called if processing had an error.
         */
        void onError();

        /**
         * @return {@code true} if you are currently compressing an image,
         * {@code false} if not.
         */
        boolean isCompressing();
    }

    /**
     * the instance of the {@code ImageProcessingListener}
     */
    private ImageProcessingListener listener;

    /**
     * Constructor for the {@code CameraUtils} util
     *
     * @param context The current {@code Context} of the app. This will be stored in a
     *                {@link WeakReference} so no leaks should occur.
     */
    public CameraUtils(@NonNull Context context) {
        weakContext = new WeakReference<>(context);
    }

    /**
     * @param l Your instance of the {@code ImageProcessingListener}.
     */
    public void setImageProcessingListener(@Nullable ImageProcessingListener l) {
        listener = l;
    }

    /**
     * @return {@code true} if the device has a camera, {@code false} if the device does not
     * have a camera.
     */
    public boolean checkCameraHardware() {
        if (weakContext.get() == null) {
            return false;
        }
        return weakContext.get().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * @return Uses {@link Environment#MEDIA_MOUNTED} to check to see is the external media is
     * mounted
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Called to build an {@code Intent} that when used with
     * {@link Activity#startActivityForResult(Intent, int)} will launch the Camera app to take a
     * picture for use.
     *
     * @return The {@code Intent} to open the Camera, or {@code null} if something went wrong.
     */
    @Nullable
    public Intent buildTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (weakContext.get() != null &&
                intent.resolveActivity(weakContext.get().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "buildTakePictureIntent:" + e.toString());
            }

            if (photoFile != null) {
                Uri photoUri = getFileUri(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                return intent;
            }
        }
        return null;
    }

    /**
     * Called from the  or {@link Fragment#onDestroy()} to clean the
     * {@link #weakContext}
     */
    public void onDestroy() {
        if (weakContext != null) {
            weakContext.clear();
            weakContext = null;
        }
        listener = null;
    }

    /**
     * Called to create a file to hold the future image
     *
     * @return The {@code File} for the image to be saved.
     * @throws IOException If there were IO errors.
     */
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = weakContext.get().getString(R.string.app_name) + timeStamp + "_";

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                getStorageDir()      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private File getStorageDir(){
        File storageDir = weakContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            Log.e("STORAGE_PATH",""+storageDir.getAbsolutePath());
        }
        return storageDir;
    }

    public void emptyDirectory(){
        File dirctory = getStorageDir();
        if(dirctory != null && dirctory.exists() && dirctory.length() > 0){
            for(File file : dirctory.listFiles()){
                file.delete();
            }
        }
    }

    /**
     * @return The {@link File#getAbsolutePath()} of the last picture taken.
     */
    @NonNull
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    /**
     * Called to compress the last picture taken.
     *
     * @see #compressImage(String)
     */
    @SuppressWarnings("unused")
    @MainThread
    public void compressImage() {
        compressImage(currentPhotoPath);
    }

    /**
     * Called to compress a JPEG image at the given path. If the given file path is successfully
     * compressed then {@link ImageProcessingListener#postCompression(String, String)} will be
     * called with the new file name as the old "Full size image" will be deleted.
     *
     * @param filePath The {@link File#getAbsolutePath()} of the picture file to compress.
     */
    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void compressImage(@NonNull String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "compressImage: The filePath was null of empty.");
            return;
        }

        new AsyncTask<String, Void, Bundle>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isCompressing = true;
                if (listener != null) {
                    listener.preCompression();
                }
            }

            @Override
            protected Bundle doInBackground(String... params) {
                Log.d(TAG, "doInBackground: File path is %1$s  " + params[0]);

                File photoFile = new File(params[0]);

                if (!photoFile.exists()) {
                    Log.d(TAG, "doInBackground: File %1$s does not exists.  " + params[0]);
                    return null;
                }

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                    bitmap = rotation(photoFile, bitmap);
                    File compressedFile = getFilefromBitmap(bitmap);

                    Bundle args = new Bundle(2);
                    args.putString("oldPath", params[0]);
                    args.putString("newPath", compressedFile.getAbsolutePath());
                    return args;

                } catch (Exception e) {
                    Log.e(TAG, "doInBackground:" + e.toString());
                } finally {
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        //noinspection UnusedAssignment
                        bitmap = null;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bundle args) {
                super.onPostExecute(args);
                isCompressing = false;
                if (listener != null) {
                    if (args == null) {
                        listener.onError();
                    } else {
                        listener.postCompression(args.getString("oldPath", ""),
                                args.getString("newPath", ""));
                    }
                }
            }
        }.execute(filePath);
    }

    /**
     * Class that implements the {@link ImageProcessingListener}
     */
    public static class SimpleImageProcessingListener implements ImageProcessingListener {

        @Override
        public void preCompression() {
        }

        @Override
        public void postCompression(@NonNull String oldPath, @NonNull String newPath) {
        }

        @Override
        public void onError() {
        }

        @Override
        public boolean isCompressing() {
            return isCompressing;
        }
    }

    private Bitmap rotation(File imageFile, Bitmap sourceBitmap) {
        Bitmap rotatedBitmap = null;
        try {
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.e("IMAGE_FILE_9",""+orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(sourceBitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(sourceBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(sourceBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = sourceBitmap;
            }

        } catch (Exception e) {
            Log.e("IMAGE_ROT_ERROR",""+e.toString());
        }

        return rotatedBitmap;

//        /****** Image rotation ****/
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotate);
//        Bitmap cropped = Bitmap.createBitmap(scaled, x, y, width, height, matrix, true);
//
//
//  return cropped
    }

    public Uri getFileUri(File photoFile){
        return FileProvider.getUriForFile(weakContext.get(),
                CAMERA_URI_AUTHORITIES,
                photoFile);
    }


    public Bitmap handleSamplingAndRotationBitmap(File imageFile) throws IOException
    {
        Uri selectedImage = getFileUri(imageFile);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = weakContext.get().getContentResolver().openInputStream(selectedImage);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, options);

        if (imageStream != null) {
            imageStream.close();
        }
        return bitmap;
    }


    public File getFilefromBitmap(Bitmap bitmap)
    {
        OutputStream os;
        try {
            File imageFile = createImageFile();
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();
            return imageFile;

        } catch (Exception e) {
            Log.e("BITMAP_EXP",""+e.toString());
        }
        return null;
    }


    public static Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public String decodeFile(String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 1000, 1000, ScalingUtilities.ScalingLogic.CROP);

            if (!(unscaledBitmap.getWidth() <= 1000 && unscaledBitmap.getHeight() <= 1000)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 1000, 1000, ScalingUtilities.ScalingLogic.CROP);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            File scaledImageFile = getFilefromBitmap(scaledBitmap);
            scaledBitmap = rotation(scaledImageFile, scaledBitmap);
            File updateImage = getFilefromBitmap(scaledBitmap);

            if (updateImage != null) {
                scaledImageFile.delete();
                scaledImageFile = updateImage;
            }

            strMyImagePath = scaledImageFile.getAbsolutePath();

            scaledBitmap.recycle();
        } catch (Throwable e) {
            Log.d("IMAGE_ERROR",""+e.toString());
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

}
