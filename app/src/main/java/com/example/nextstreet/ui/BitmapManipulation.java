package com.example.nextstreet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Based on https://gist.github.com/nesquena/3885707fd3773c09f1bb
 */

public class BitmapManipulation {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public final static int PICK_PHOTO_CODE = 1046;

    /**
     * Scale and keep aspect ratio.
     * @param b, Bitmap to scale.
     * @param width, Desired width.
     * @return rescaled Bitmap
     */
    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    /**
     * Scale and keep aspect ratio.
     * @param b, Bitmap to scale.
     * @param height, Desired height.
     * @return rescaled Bitmap
     */
    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }


    /**
     * Scale and keep aspect ratio.
     * @param b, Bitmap to scale.
     * @param width, Desired width.
     * @param height, Desired height.
     * @return rescaled Bitmap
     */
    public static Bitmap scaleToFill(Bitmap b, int width, int height)
    {
        float factorH = height / (float) b.getWidth();
        float factorW = width / (float) b.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse),
                (int) (b.getHeight() * factorToUse), true);
    }


    /**
     * Scale and don't keep aspect ratio.
     * @param b, Bitmap to scale.
     * @param width, Desired width.
     * @param height, Desired height.
     * @return stretched Bitmap
     */
    public static Bitmap strechToFill(Bitmap b, int width, int height)
    {
        float factorH = height / (float) b.getHeight();
        float factorW = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorW),
                (int) (b.getHeight() * factorH), true);
    }

    /**
     * Rotate Bitmap based on EXIF orientation.
     * Based on https://stackoverflow.com/questions/12933085/android-camera-intent-saving-image-landscape-when-taken-portrait/12933632#12933632
     * @param photoFilePath, Path to file.
     * @return
     */
    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ?
                Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,
                bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }


    /**
     * Launches camera as a new Activity.
     * @param context, Context to launch camera in.
     * @param fragment, Fragment where camera is being launched.
     * @param photoFileName, Path to file.
     * @param TAG, TAG for log statements.
     * @return File containing photo.
     */
    public static File launchCamera(Activity context, Fragment fragment, String photoFileName, String TAG) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = getPhotoFileUri(context, photoFileName, TAG);

        Uri fileProvider = FileProvider.getUriForFile(context,
                "com.codepath.fileprovider.NextStreet", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            fragment.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

        return photoFile;
    }

    /**
     * Get URI of photo file.
     * @param context, Context to launch camera in.
     * @param fileName, Path to file.
     * @param TAG, TAG for log statements.
     * @return new File.
     */
    public static File getPhotoFileUri(Context context, String fileName, String TAG) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                TAG);

        if (!mediaStorageDir.exists()
                && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "getPhotoFileUri: failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    /**
     * Write a new file.
     * @param context, Context to launch camera in.
     * @param photoFileName, Path to file.
     * @param changedBitmap, Bitmap of changed image.
     * @param resized, String to add to end of file name.
     * @param TAG, TAG for log statements.
     * @return new File containing changed Bitmap.
     */
    public static File writeResizedBitmap(Context context, String photoFileName,
                                          Bitmap changedBitmap, String resized, String TAG){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        changedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        String filename = photoFileName + "_" + resized;
        File resizedFile = getPhotoFileUri(context, filename, TAG);

        try {
            resizedFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizedFile;
    }

    /**
     * Launches a photo gallery as a new Activity.
     * @param context, Context to launch camera in.
     * @param fragment, Fragment where gallery is being launched.
     */
    public static void onPickPhoto(Context context, Fragment fragment) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            fragment.startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    /**
     * Load an image from given URI.
     * @param context, Context to launch camera in.
     * @param photoUri, URI where Bitmap is located.
     * @return Bitmap of image in given URI.
     */
    public static Bitmap loadFromUri(Context context, Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}