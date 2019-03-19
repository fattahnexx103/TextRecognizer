package apps.android.fattahnexx103.visionup;

import android.Manifest;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA
         == ())
        return false;
    }
}
