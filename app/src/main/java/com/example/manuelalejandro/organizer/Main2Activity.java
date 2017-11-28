package com.example.manuelalejandro.organizer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.manuelalejandro.organizer.Permission.PermissionUtil;
import com.example.manuelalejandro.organizer.Utils.Constants;
import com.example.manuelalejandro.organizer.activities.UsedImagesActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class Main2Activity extends AppCompatActivity {
    final String APP = "Organizer";
    private ProgressDialog pDialog;
    private static final int REQ_WIDTH = 350;
    private static final int REQ_HEIGHT = 250;
    public static final int REQUEST_CAPTURE = 5;
    public static final int PICK_IMAGE = 10;


    EditText edtArtist;
    EditText edtTitle;
    ImageView image;
    File destination;
    Long id = null;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        destination = new File(Environment.getExternalStorageDirectory(), "/organized/"+ System.currentTimeMillis() + ".jpg");
        edtArtist = (EditText) findViewById(R.id.txtArtistName);
        edtTitle = (EditText) findViewById(R.id.txtTitle);
        image = (ImageView) findViewById(R.id.image);
        id = intent.getLongExtra("rowId", -1);
        if (id != -1) {
            Map<String, Object> map = DatabaseAccessor.getRow(id + "");
            edtArtist.setText((String) map.get(DatabaseAccessor.TAG_ARTIST));
            edtTitle.setText((String) map.get(DatabaseAccessor.TAG_TITLE));
            destination = new File((String) map.get(DatabaseAccessor.TAG_IMAGE));
            new LoadImage().execute();

        } else {
            Button btnDelete = (Button) findViewById(R.id.btnDelete);
            btnDelete.setVisibility(View.GONE);

        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, Main2Activity.class);
        return intent;
    }

    public static Intent newIntent(Context context, long newId) {
        Intent intent = new Intent(context, Main2Activity.class);
        intent.putExtra("rowId", newId);
        return intent;
    }



    public void openImagePopup(View view) {
        final CharSequence[] items = {"Escoge imagenes ya guardas"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Captura de image")
                .setMessage("Deseas capturar imagen nueva o escoger imagen existente?")
                .setPositiveButton("Capturar Imagen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        takePic();
                    }
                })
                .setNegativeButton("Escoger Imagen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //chooseImage();
                        startActivity(UsedImagesActivity.newIntent(Main2Activity.this));
                        //findExistingImages();
                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:

                        }
                    }
                });
                builder.create().show();
    }

    private void selectImage(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
    private void chooseImage() {
        if(PermissionUtil.hasPermissionOrRequest(this, PermissionUtil.REQUEST_EXTERNAL_STORAGE_SELECTOR)){
            selectImage();
        }
    }

    private void takePic() {
        if(PermissionUtil.hasPermissionOrRequest(this, PermissionUtil.REQUEST_EXTERNAL_STORAGE_PHOTO)) {
            takePicWithPerm();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == PermissionUtil.REQUEST_EXTERNAL_STORAGE_PHOTO)
                takePicWithPerm();
            else if(requestCode == PermissionUtil.REQUEST_EXTERNAL_STORAGE_SELECTOR){
                selectImage();
            }
        }
        else{
            //Permission denied.
        }

    }
    private void takePicWithPerm(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
            startActivityForResult(intent, REQUEST_CAPTURE);
        }
    }
    public void save(View view) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null && id != -1) {
            map.put(DatabaseAccessor.TAG_ID, id);
        }
        if (validated()) {
            map.put(DatabaseAccessor.TAG_ARTIST, edtArtist.getText().toString());
            map.put(DatabaseAccessor.TAG_TITLE, edtTitle.getText().toString());
            map.put(DatabaseAccessor.TAG_IMAGE, destination.getAbsolutePath());
            Log.i("MainActivity2", destination.getAbsolutePath());
            new SaveRow().execute(map);
        }

    }

    private boolean validated() {
        StringBuffer sb = new StringBuffer();
        if (edtArtist.getText().toString().equals("")) {
            sb.append("Artista no puede estar vacio\n");
        }
        if (edtTitle.getText().toString().equals("")) {
            sb.append("Titulo no puede estar vacio\n");
        }
        if (sb.length() == 0) {
            return true;
        }
        Log.i("qwert", sb.toString());
        Toast.makeText(getBaseContext(), sb.toString(), Toast.LENGTH_SHORT).show();
        return false;
    }

    public void delete(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
        alertDialog.setTitle("Alerta!");
        alertDialog.setMessage("Estas seguro que quieres borrar?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseAccessor.deleteItem(id);
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

            }
        });
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                try {
                    File realImage = new File(PathUtil.getPath(this, selectedImage));
                    destination = ImageHelper.copyFile(realImage);

                    Log.i("asAAAAAAA", PathUtil.getPath(this, selectedImage) + "");
                } catch (Exception e) {
                    Log.i("asAAAAAAA", e.getMessage());

                }
            }
            new LoadImage().execute();
        }

    }

    @Override
    public void onPause() {

        super.onPause();
        if (pDialog != null)
            pDialog.dismiss();
    }

    private class SaveRow extends AsyncTask<Map, Void, Void> {
        @Override
        protected Void doInBackground(Map... map) {
            DatabaseAccessor.upsert(map[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null) {
                pDialog = new ProgressDialog(Main2Activity.this);
                pDialog.setMessage("Cargando...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }
            finish();
        }
    }


    private class LoadImage extends AsyncTask<Void, Void, Void> {

        Bitmap user_picture_bmp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null) {
                pDialog = new ProgressDialog(Main2Activity.this);
                pDialog.setMessage("Cargando...");
                pDialog.setCancelable(false);
                pDialog.show();
            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }
            image.setImageBitmap(user_picture_bmp);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                user_picture_bmp = ImageHelper.getImage(destination, REQ_WIDTH, REQ_HEIGHT, Main2Activity.this);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                user_picture_bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byteArray = bos.toByteArray();
            } catch (Exception e) {
                Log.d("AGGGGGGGRR", " bitmap factory==========" + e.getMessage());

            }
            return null;
        }

    }
}
