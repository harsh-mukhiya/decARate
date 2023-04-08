package com.example.decARate.login;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.decARate.CropperActivity;
import com.example.decARate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class Profile extends AppCompatActivity {
    SharedPreferences sp;
    String name = "";
    String email = "";
    String mobile = "";
    String address = "";

    TextView save;
    TextView acc_name, acc_mobile, acc_address, acc_email;
    EditText edit_name, edit_mobile, edit_address;
    ImageView edit_image, imageView;
    private final int cameraRequestCode = 101;
    private final int galleryRequestCode = 202;
    Dialog dialog;
    ActivityResultLauncher<String> mGetImageContent;


    ProgressDialog progressDialog;
    protected String encodedImage;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.account_edit) {
            acc_name.setVisibility(View.GONE);
            edit_name.setText(acc_name.getText());
            edit_name.setVisibility(View.VISIBLE);

            acc_mobile.setVisibility(View.GONE);
            edit_mobile.setText(acc_mobile.getText());
            edit_mobile.setVisibility(View.VISIBLE);

            acc_address.setVisibility(View.GONE);
            edit_address.setText(acc_address.getText());
            edit_address.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == cameraRequestCode) {
                Bitmap img = (Bitmap) data.getExtras().get("data");
                Bitmap resizedBmp = Bitmap.createBitmap(img, 0, 0, 100, 100);
                imageView.setImageBitmap(resizedBmp);
                encodedImage = encodeImage(resizedBmp);
                uploadImage(encodedImage);

            } else if (requestCode == galleryRequestCode) {
                String result = data.getStringExtra("RESULT");
                Uri resultUri = Uri.parse(result);
                imageView.setImageURI(resultUri);

                InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    //upload in database
                    uploadImage(encodedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public void uploadImage(String base64) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyqk-yfHGJ-f-n2qw9En6bnTA7P5605bQhaOPv-6EiF8pzU4fx01T-KhTxddVlHzHiM/exec"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";

                try {
                    JSONObject jobj = new JSONObject(response);
                    output = jobj.getString("update");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", sp.getString("email", ""));
                params.put("image", base64);
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        progressDialog = new ProgressDialog(Profile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        edit_image = findViewById(R.id.edit_image);
        imageView = findViewById(R.id.imageView_profile);


        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(Profile.this);
                dialog.setContentView(R.layout.profile_image);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animaion;
                dialog.show();

                TextView camera_photo = dialog.findViewById(R.id.camera_photo);
                TextView gallery_photo = dialog.findViewById(R.id.gallery_photo);
                TextView cancel_photo = dialog.findViewById(R.id.cancel_photo);

                camera_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(Profile.this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera, cameraRequestCode);
                            dialog.cancel();
                        } else {
                            requestCameraPermission();
                        }
                    }
                });
                gallery_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            mGetImageContent.launch("image/*");
                            dialog.cancel();
                        } else {
                            requestStoragePermission();
                        }
                    }
                });
                cancel_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        mGetImageContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(Profile.this, CropperActivity.class);
                if(result!=null){
                    intent.putExtra("DATA", result.toString());
                    startActivityForResult(intent,galleryRequestCode);
                }
            }
        });


        acc_email = findViewById(R.id.account_email);
        acc_mobile = findViewById(R.id.account_mobile);
        acc_name = findViewById(R.id.accountName);
        acc_address = findViewById(R.id.account_address);

        edit_address = findViewById(R.id.edit_address);
        edit_mobile = findViewById(R.id.edit_mobile);
        edit_name = findViewById(R.id.edit_name);
        save = findViewById(R.id.save_profile);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Account Info ");
        actionBar.setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences("ID", MODE_PRIVATE);

        getAccountDetails();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Saving...");
                progressDialog.show();

                if (edit_mobile.getText().length() == 10) {

                    editAccountDetails();
                    acc_name.setVisibility(View.VISIBLE);
                    edit_name.setVisibility(View.GONE);

                    acc_mobile.setVisibility(View.VISIBLE);
                    edit_mobile.setVisibility(View.GONE);

                    acc_address.setVisibility(View.VISIBLE);
                    edit_address.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                } else {
                    Toast.makeText(Profile.this, "InValid mobile no", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void editAccountDetails() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxRTgmA10LS2EVtASCl_u-U7bd5lCh8hMtiHpNYUOabWgxqIE2Qpv5NgPv-qlRV8n6N/exec"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";

                try {
                    JSONObject jobj = new JSONObject(response);
                    output = jobj.getString("output");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAccountDetails();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", edit_name.getText().toString());
                params.put("mobile", edit_mobile.getText().toString());
                params.put("email", acc_email.getText().toString());
                params.put("address", edit_address.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getAccountDetails() {
        String s1 = sp.getString("email", "");
        String url = "https://script.google.com/macros/s/AKfycbxRTgmA10LS2EVtASCl_u-U7bd5lCh8hMtiHpNYUOabWgxqIE2Qpv5NgPv-qlRV8n6N/exec?email=";

        String s2 = url + s1;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, s2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            name = response.getString("name");
                            email = response.getString("email");
                            mobile = response.getString("mobile");
                            address = response.getString("address");
                            String image = response.getString("image");
                            // get image and decode it
                            if (!image.equalsIgnoreCase(""))
                                decode(image);
                            else
                                imageView.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));

                            acc_name.setText(name);
                            acc_email.setText(email);
                            acc_mobile.setText(mobile);
                            if (!address.equalsIgnoreCase(""))
                                acc_address.setText(address);

                            progressDialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void decode(String image) {
        byte[] b = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0, b.length);
        imageView.setImageBitmap(decodedByte);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Cart 24 Seller needs permission to access Camera on your device")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Profile.this,
                                    new String[]{Manifest.permission.CAMERA}, cameraRequestCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, cameraRequestCode);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Cart 24 Seller needs permission to access Gallery on your device")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Profile.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, galleryRequestCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, galleryRequestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, cameraRequestCode);
                dialog.cancel();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == galleryRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGetImageContent.launch("image/*");
                dialog.cancel();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

