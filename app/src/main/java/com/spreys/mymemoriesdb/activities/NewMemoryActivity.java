package com.spreys.mymemoriesdb.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.spreys.mymemoriesdb.R;
import com.spreys.mymemoriesdb.db.MemoryDbHelper;
import com.spreys.mymemoriesdb.model.Memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;


public class NewMemoryActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private ImageView selectedImageView;
    private EditText titleEditText;

    CharSequence text = "Hello toast!";
    String url ="http://181.197.41.121:3000/testimg";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_memory_activity);

        this.selectedImageView = (ImageView) findViewById(R.id.new_memory_selected_image);
        this.titleEditText = (EditText) findViewById(R.id.new_memory_title);
    }

    public void openGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    public void openCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void save(View view) {
        Bitmap image = ((BitmapDrawable)selectedImageView.getDrawable()).getBitmap();
        new MemoryDbHelper(this).addMemory(new Memory(titleEditText.getText().toString(), image));


//        RequestQueue queue = Volley.newRequestQueue(this);
//        JSONObject datita = new JSONObject();
//
//        try{
////            datita.put("RUC", Base64.encodeToString(b, Base64.DEFAULT));
//            datita.put("RUC", "");
//
//        }catch (JSONException e){
//
//        }
//
//        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datita,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //TODO Manejo de recepcion de la respuesta del servidor
//                        Toast.makeText(getApplicationContext(), response.toString() , Toast.LENGTH_LONG).show();
//                        Log.d("DB", response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //TODO Manejo de error de la respuesta del servidor
//                        Toast.makeText(getApplicationContext(), "Conection Error" , Toast.LENGTH_LONG).show();
//                        Log.d("DB", error.toString());
//
//
//                    }
//                }
//        );
//        queue.add(jsonobj);



        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject datita = new JSONObject();

//
//        String JODER;
//        try{
////            datita.put("RUC", Base64.encodeToString(b, Base64.DEFAULT));
//            datita.put("ImageBase64", JODER);
//
//        }catch (JSONException e){
//
//        }
//
//
//        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datita,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //TODO Manejo de recepcion de la respuesta del servidor
//                        Toast.makeText(getApplicationContext(), response.toString() , Toast.LENGTH_LONG).show();
//                        Log.d("DB", response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //TODO Manejo de error de la respuesta del servidor
//                        Toast.makeText(getApplicationContext(), "Conection Error" , Toast.LENGTH_LONG).show();
//                        Log.d("DB", error.toString());
//
//
//                    }
//                }
//        );

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap original = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.e("Original   dimensions", original.getWidth()+" "+original.getHeight());
                byte [] z = out.toByteArray();
//                String Captcha = Base64.encodeToString(z, Base64.DEFAULT);

                datita.put("ImageBase64", Base64.encodeToString(z, Base64.DEFAULT));

                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, datita,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO Manejo de recepcion de la respuesta del servidor
                        Toast.makeText(getApplicationContext(), response.toString() , Toast.LENGTH_LONG).show();
                        Log.d("DB", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Manejo de error de la respuesta del servidor
                        Toast.makeText(getApplicationContext(), "Conection Error" , Toast.LENGTH_LONG).show();
                        Log.d("DB", error.toString());


                    }
                }
        );
                queue.add(jsonobj);


                selectedImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));

            } catch (IOException exception) {
                exception.printStackTrace();
            }catch (JSONException ex){

            }
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImageView.setImageBitmap(imageBitmap);
//            queue.add(jsonobj);

        }
    }
}
