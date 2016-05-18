package com.example.inkanetapplication.inkanetapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "My Inkanet Images";
    private Uri fileUri;
    private ImageButton btnCapturePicture;
    private ImageButton btnFolder;
    private String gallerySaveImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCapturePicture = (ImageButton) findViewById(R.id.buttonCamera);
        btnFolder = (ImageButton)findViewById(R.id.buttonFolder);
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Capture image button click event
     * */
    public void capturePicture(View view){
        Context context = this;
        boolean isdevicesupprtsCamera = isDeviceSupportCamera();
        if(isdevicesupprtsCamera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            saveInTheGallery();
        }else{
            Toast.makeText(getApplicationContext(),
                    "This device does not support camera", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private void saveInTheGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File galleyFile = new File("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Uri contentUri = Uri.fromFile(galleyFile);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
        else
        {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()+ File.separator+"InkaNetApp")));
        }
    }
    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new   File(Environment.getExternalStorageDirectory()+ File.separator+"InkaNetApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("InkaNetApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }else {
            return null;
        }

        return mediaFile;
    }
    public void openFolder(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri root =  Uri.parse(Environment.getExternalStorageDirectory()+ File.separator+"InkaNetApp");

        intent.setDataAndType(root,"resource/folder");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Sorry! No such image or directory present", Toast.LENGTH_SHORT)
                    .show();
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
        }

    }
    /*
      * Display image from a path to ImageView
      */
    private void previewCapturedImage() {
        try {
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            //btnFolder.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
