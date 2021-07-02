package com.indrashekar.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

public class PostingActivity extends AppCompatActivity {
    Button btnChoose,btnUpload;
    ImageView imgView;
    Uri filePath;
    FirebaseStorage storage;
    FirebaseUser fuser;
    DatabaseReference reference;
    StorageReference storageReference;
    StorageTask uploadtask;
    private final int PICK_IMAGE_REQUEST = 22;
    public void buttonVisiblity(){
        if(filePath!=null){
            btnUpload.setEnabled(true);
        }
        else{
            btnUpload.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        Toolbar toolbar=findViewById(R.id.toolbar);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnChoose=findViewById(R.id.btnChoose);
        btnUpload=findViewById(R.id.btnUpload);
        imgView=findViewById(R.id.imgView);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        buttonVisiblity();
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here"),PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            filePath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imgView.setImageBitmap(bitmap);
                buttonVisiblity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void uploadImage(){
        String uid=fuser.getUid();
        if(filePath!=null){
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref=storageReference.child(uid+"/"+System.currentTimeMillis());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(PostingActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    String error=e.getMessage().toString();
                    Toast.makeText(PostingActivity.this,error, Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    String number=String.valueOf(progress);
                    progressDialog.setMessage("Uploaded "+number+"%");
                }
            });
        }
        else{
            Toast.makeText(this, "Choose an Image", Toast.LENGTH_SHORT).show();
        }
    }
}