package apps.android.fattahnexx103.visionup;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

        //set actionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click + button to insert Image");

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT ).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:

                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT ).show();
                    }
                }
                break;


        }

    }

    //handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //after we got image from gallery, we crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
        }
        //get cropped image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri(); //get image uri

                //set Image to view
                mPreview.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable drawable = (BitmapDrawable) mPreview.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext())
                        .build();

                if(!recognizer.isOperational()){
                    Toast.makeText(this, "Error in getting Image", Toast.LENGTH_SHORT).show();
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();

                    //get text from sparse array until there is no text
                    for(int i = 0; i <items.size(); i++){
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("\n");
                    }

                    //update editText
                    mResult.setText(stringBuilder.toString());
                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
