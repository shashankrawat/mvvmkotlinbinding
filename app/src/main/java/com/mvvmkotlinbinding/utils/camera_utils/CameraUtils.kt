package com.mvvmkotlinbinding.utils.camera_utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.FileProvider
import com.mvvmkotlinbinding.utils.FileProviderUtilsHelper
import com.mvvmkotlinbinding.utils.ScalingUtilities
import com.mvvmwithdatabinding.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

class CameraUtils(context: Context) : FileProviderUtilsHelper {
    /**
     * The current `Context` of the app
     */
    private var weakContext: WeakReference<Context?>?
    /**
     * @return The [File.getAbsolutePath] of the last picture taken.
     */
    /**
     * The path of the last picture taken.
     */
    var currentPhotoPath = ""
        private set

    /**
     * The listener for image processing
     */
    interface ImageProcessingListener {
        /**
         * Called before the compression starts
         */
        fun preCompression()

        /**
         * Called after the compression has completed successfully.
         *
         * @param oldPath The [File.getAbsolutePath] for the image that was compressed
         * and then deleted.
         * @param newPath The [File.getAbsolutePath] for the image that was created when
         * the `oldPath` was compressed. Update your reference to teh image
         * file.
         */
        fun postCompression(oldPath: String, newPath: String)

        /**
         * Called if processing had an error.
         */
        fun onError()

        /**
         * @return `true` if you are currently compressing an image,
         * `false` if not.
         */
        val isCompressing: Boolean
    }

    /**
     * the instance of the `ImageProcessingListener`
     */
    private var listener: ImageProcessingListener? = null

    /**
     * @param l Your instance of the `ImageProcessingListener`.
     */
    fun setImageProcessingListener(l: ImageProcessingListener?) {
        listener = l
    }

    /**
     * @return `true` if the device has a camera, `false` if the device does not
     * have a camera.
     */
    fun checkCameraHardware(): Boolean {
        return if (weakContext!!.get() == null) {
            false
        } else weakContext!!.get()!!.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * Called to build an `Intent` that when used with
     * [Activity.startActivityForResult] will launch the Camera app to take a
     * picture for use.
     *
     * @return The `Intent` to open the Camera, or `null` if something went wrong.
     */
    fun buildTakePictureIntent(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (weakContext!!.get() != null &&
            intent.resolveActivity(weakContext!!.get()!!.packageManager) != null
        ) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                Log.e(TAG, "buildTakePictureIntent:$e")
            }
            if (photoFile != null) {
                val photoUri = getFileUri(photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                return intent
            }
        }
        return null
    }

    /**
     * Called from the  or [Fragment.onDestroy] to clean the
     * [.weakContext]
     */
    fun onDestroy() {
        if (weakContext != null) {
            weakContext!!.clear()
            weakContext = null
        }
        listener = null
    }

    /**
     * Called to create a file to hold the future image
     *
     * @return The `File` for the image to be saved.
     * @throws IOException If there were IO errors.
     */
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp = System.currentTimeMillis().toString()
        val imageFileName = weakContext!!.get()!!.getString(R.string.app_name) + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private val storageDir: File?
        private get() {
            val storageDir =
                weakContext!!.get()!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (storageDir != null) {
                Log.e("STORAGE_PATH", "" + storageDir.absolutePath)
            }
            return storageDir
        }

    fun emptyDirectory() {
        val dirctory = storageDir
        if (dirctory != null && dirctory.exists() && dirctory.length() > 0) {
            for (file in dirctory.listFiles()) {
                file.delete()
            }
        }
    }

    /**
     * Called to compress the last picture taken.
     *
     * @see .compressImage
     */
    @MainThread
    fun compressImage() {
        compressImage(currentPhotoPath)
    }

    /**
     * Called to compress a JPEG image at the given path. If the given file path is successfully
     * compressed then [ImageProcessingListener.postCompression] will be
     * called with the new file name as the old "Full size image" will be deleted.
     *
     * @param filePath The [File.getAbsolutePath] of the picture file to compress.
     */
    @SuppressLint("StaticFieldLeak")
    @MainThread
    fun compressImage(filePath: String) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "compressImage: The filePath was null of empty.")
            return
        }
        object : AsyncTask<String?, Void?, Bundle?>() {
            override fun onPreExecute() {
                super.onPreExecute()
                isCompressing = true
                if (listener != null) {
                    listener!!.preCompression()
                }
            }

            override fun onPostExecute(args: Bundle?) {
                super.onPostExecute(args)
                isCompressing = false
                if (listener != null) {
                    if (args == null) {
                        listener!!.onError()
                    } else {
                        listener!!.postCompression(
                            args.getString("oldPath", ""),
                            args.getString("newPath", "")
                        )
                    }
                }
            }

            override fun doInBackground(vararg params: String?): Bundle? {
                Log.d(TAG, "doInBackground: File path is %1\$s  " + params[0])
                val photoFile = File(params[0])
                if (!photoFile.exists()) {
                    Log.d(TAG, "doInBackground: File %1\$s does not exists.  " + params[0])
                    return null
                }
                var bitmap: Bitmap? = null
                try {
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    bitmap = rotation(photoFile, bitmap)
                    val compressedFile = getFilefromBitmap(bitmap)
                    val args = Bundle(2)
                    args.putString("oldPath", params[0])
                    args.putString("newPath", compressedFile!!.absolutePath)
                    return args
                } catch (e: Exception) {
                    Log.e(TAG, "doInBackground:$e")
                } finally {
                    if (photoFile.exists()) {
                        photoFile.delete()
                    }
                    if (bitmap != null) {
                        bitmap.recycle()
                        bitmap = null
                    }
                }
                return null
            }
        }.execute(filePath)
    }

    /**
     * Class that implements the [ImageProcessingListener]
     */
    class SimpleImageProcessingListener : ImageProcessingListener {
        override fun preCompression() {}
        override fun postCompression(oldPath: String, newPath: String) {}
        override fun onError() {}
        override val isCompressing: Boolean
            get() = Companion.isCompressing
    }

    private fun rotation(imageFile: File?, sourceBitmap: Bitmap?): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            val exif = ExifInterface(
                imageFile!!.absolutePath
            )
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            Log.e("IMAGE_FILE_9", "" + orientation)
            rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(sourceBitmap, 270)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(sourceBitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(sourceBitmap, 90)
                ExifInterface.ORIENTATION_NORMAL -> sourceBitmap
                else -> sourceBitmap
            }
        } catch (e: Exception) {
            Log.e("IMAGE_ROT_ERROR", "" + e.toString())
        }
        return rotatedBitmap

//        /****** Image rotation ****/
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotate);
//        Bitmap cropped = Bitmap.createBitmap(scaled, x, y, width, height, matrix, true);
//
//
//  return cropped
    }

    fun getFileUri(photoFile: File?): Uri {
        return FileProvider.getUriForFile(
            weakContext!!.get()!!,
            FileProviderUtilsHelper.CAMERA_URI_AUTHORITIES,
            photoFile!!
        )
    }

    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(imageFile: File?): Bitmap? {
        val selectedImage = getFileUri(imageFile)
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val imageStream = weakContext!!.get()!!.contentResolver.openInputStream(selectedImage)
        val bitmap = BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()
        return bitmap
    }

    fun getFilefromBitmap(bitmap: Bitmap?): File? {
        val os: OutputStream
        try {
            val imageFile = createImageFile()
            os = FileOutputStream(imageFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, os)
            os.flush()
            os.close()
            return imageFile
        } catch (e: Exception) {
            Log.e("BITMAP_EXP", "" + e.toString())
        }
        return null
    }

    fun decodeFile(path: String): String {
        var strMyImagePath: String? = null
        var scaledBitmap: Bitmap?
        try {
            // Part 1: Decode image
            val unscaledBitmap =
                ScalingUtilities.decodeFile(path, 1000, 1000, ScalingUtilities.ScalingLogic.CROP)
            scaledBitmap = if (!(unscaledBitmap.width <= 1000 && unscaledBitmap.height <= 1000)) {
                // Part 2: Scale image
                ScalingUtilities.createScaledBitmap(
                    unscaledBitmap,
                    1000,
                    1000,
                    ScalingUtilities.ScalingLogic.CROP
                )
            } else {
                unscaledBitmap.recycle()
                return path
            }
            var scaledImageFile = getFilefromBitmap(scaledBitmap)
            scaledBitmap = rotation(scaledImageFile, scaledBitmap)
            val updateImage = getFilefromBitmap(scaledBitmap)
            if (updateImage != null) {
                scaledImageFile!!.delete()
                scaledImageFile = updateImage
            }
            strMyImagePath = scaledImageFile!!.absolutePath
            scaledBitmap!!.recycle()
        } catch (e: Throwable) {
            Log.d("IMAGE_ERROR", "" + e.toString())
        }
        return strMyImagePath ?: path
    }

    companion object {
        private const val TAG = "CameraUtils"

        /**
         * `true` if you are currently compressing an image, `false` if not.
         */
        private var isCompressing = false

        /**
         * @return Uses [Environment.MEDIA_MOUNTED] to check to see is the external media is
         * mounted
         */
        val isExternalStorageMounted: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

        fun rotateImage(source: Bitmap?, angle: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            return Bitmap.createBitmap(
                source!!, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }

    /**
     * Constructor for the `CameraUtils` util
     *
     * @param context The current `Context` of the app. This will be stored in a
     * [WeakReference] so no leaks should occur.
     */
    init {
        weakContext = WeakReference(context)
    }
}