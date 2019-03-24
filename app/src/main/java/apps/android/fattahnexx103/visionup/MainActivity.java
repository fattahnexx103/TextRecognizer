package apps.android.fattahnexx103.visionup;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import static apps.android.fattahnexx103.visionup.R.id.image;
import static apps.android.fattahnexx103.visionup.R.id.resultImageText;

public class MainActivity extends AppCompatActivity {

    EditText mResult;
    ImageView mPreview;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult =  (EditText) findViewById(R.id.result);
        mPreview = (ImageView) findViewById(R.id.imageID);

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    //actionBar menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //handle menu item clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.add_image){
            showImageDialog();
        }
        if(id == R.id.me_item){

        }
        return true;
    }

    private void showImageDialog() {

        //items to display in dialog
        String[] items = {"Camera", "Gallery" };
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //set the title
        dialog.setTitle("Select Image");

        //set the items
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //camera clicked
                    if(!checkCameraPermission()){
                        //camera permission not allowed so prompt it
                        requestCameraPermission();
                    }else{
                        //permission allowed
                        pickCamera();
                    }
                }
                if(i == 1){
                    //gallery clicked
                    if(!checkGalleryPermission()){
                        //camera permission not allowed so prompt it
                        requestStoragePermission();
                    }else{
                        //gallery permission allowed
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {

        //intent to pick images from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //set intent type to image
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickCamera() {
        // intent to take image from camera
        ContentValues vales = new ContentValues();
        vales.put(MediaStore.Images.Media.TITLE, "NewPic"); //title of the pic is NewPic
        vales.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, vales);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkGalleryPermission() {

        boolean resultGallery = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return resultGallery;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean resultCam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
         == (PackageManager.PERMISSION_GRANTED);

        boolean resultGallery = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return resultCam && resultGallery;

    }
}
