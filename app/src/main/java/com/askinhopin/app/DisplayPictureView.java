package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisplayPictureView extends AppCompatActivity {

    private ImageView iv_dpv;
    private Button b_save_dpv, b_edit_dpv, b_remove_dpv;
    String url, schoolResult, rndUrl, currentid;
    DocumentReference reference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final int PICK_IMAGE=1;
    Uri imageUri;
    UploadTask uploadTask;
    List<String> rndUrlList;
    ProgressBar progressbar_dpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture_view);

//        Toast.makeText(this, "OnCreate", Toast.LENGTH_SHORT).show();
        iv_dpv = findViewById(R.id.iv_dpv);
        b_save_dpv = findViewById(R.id.b_save_dpv);
        b_edit_dpv = findViewById(R.id.b_edit_dpv);
        b_remove_dpv = findViewById(R.id.b_remove_dpv);
        progressbar_dpv = findViewById(R.id.progressbar_dpv);
        b_edit_dpv.setEnabled(false);
        b_remove_dpv.setEnabled(false);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentid = user.getUid();
        rndUrl = getImageUrl();
        reference = db.collection("user").document(currentid);

        rndUrlList = new ArrayList<>();
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F1.png?alt=media&token=010e5fa0-62cd-4df3-a08b-a29a58148c18");
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F2.png?alt=media&token=38acf330-1371-48ed-a85e-2251f8f58a32");
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F3.png?alt=media&token=14bc59d7-83b3-4280-9b3e-2677ba69c625");
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F4.png?alt=media&token=10e40efa-ef87-4de5-97d6-b825cf4a93be");
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F5.png?alt=media&token=5b5ca89f-eac1-4b07-b41f-fc847881df98");
        rndUrlList.add("https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F6.png?alt=media&token=62af023f-1dc8-42d4-bd1f-a1b776cc3002");


        b_remove_dpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rndUrlList.contains(url)){
                    Toast.makeText(DisplayPictureView.this, "Image is already set to default", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DisplayPictureView.this);

                    builder.setTitle("Remove Image")
                            .setMessage("Are you sure you want to delete this Image?\n Your Profile Picture will be set to default.")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Picasso.get().load(rndUrl).fit().centerCrop().into(iv_dpv);
                                    removeDp(rndUrl);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.create();
                    builder.show();
                }

            }
        });

        b_edit_dpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseimage();
                
            }
        });

        b_save_dpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    uploadNew();
                    progressbar_dpv.setVisibility(View.VISIBLE);
                    b_save_dpv.setVisibility(View.GONE);
                }

                else
                    Toast.makeText(DisplayPictureView.this, "No File Choosen", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void uploadNew() {
        final StorageReference reference = mFirebaseStorage.getReference("Profile images").child(System.currentTimeMillis() + "." + "jpg");
        uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadurl = task.getResult();
                if(downloadurl != null){
                    removeDp(downloadurl.toString());
                }else{
                    Toast.makeText(DisplayPictureView.this, "Error Fetching Uploaded Image", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DisplayPictureView.this, "Error Uploading File", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseimage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data !=null || data.getData() != null){
                Uri uri= data.getData();


                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "askinhopin/media" + System.currentTimeMillis());
                if(!file.exists())
                    file.mkdir();

                String filePath = SiliCompressor.with(this).compress(String.valueOf(uri), file, false);

                imageUri = Uri.parse(filePath);
                Picasso.get().load(imageUri).fit().centerCrop().into(iv_dpv);
                b_save_dpv.setVisibility(View.VISIBLE);

            }

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_LONG).show();
        }
    }


    private void removeDp(String s1) {



        if(rndUrlList.contains(url)){
            setNewPicture(s1);
        }else{
            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(url);

            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    setNewPicture(s1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(DisplayPictureView.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

        }



    }

    private void setNewPicture(String s) {
        final DocumentReference sDoc = db.collection("user").document(currentid);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                transaction.update(sDoc, "imageurl", s);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(schoolResult).child("user").child(currentid).child("imgurl").setValue(s);
                progressbar_dpv.setVisibility(View.GONE);
                b_save_dpv.setVisibility(View.VISIBLE);
                Toast.makeText(DisplayPictureView.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(DisplayPictureView.this, "failed " + e, Toast.LENGTH_SHORT).show();
                    }
        });
    }

    private String getImageUrl() {
        Random r = new Random();
        int i1 = r.nextInt(6 - 1) + 1;
        String ur;
        switch (i1){
            case 1:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F1.png?alt=media&token=010e5fa0-62cd-4df3-a08b-a29a58148c18";
                break;
            case 2:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F2.png?alt=media&token=38acf330-1371-48ed-a85e-2251f8f58a32";
                break;
            case 3:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F3.png?alt=media&token=14bc59d7-83b3-4280-9b3e-2677ba69c625";
                break;
            case 4:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F4.png?alt=media&token=10e40efa-ef87-4de5-97d6-b825cf4a93be";
                break;
            case 5:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F5.png?alt=media&token=5b5ca89f-eac1-4b07-b41f-fc847881df98";
                break;
            default:
                ur = "https://firebasestorage.googleapis.com/v0/b/askinhopin.appspot.com/o/Profile%20images%2Frandom%20faces%2F6.png?alt=media&token=62af023f-1dc8-42d4-bd1f-a1b776cc3002";
                break;
        }

        return ur;
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Toast.makeText(this, "Onstrat", Toast.LENGTH_SHORT).show();


        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            url = task.getResult().getString("imageurl");
                            schoolResult = task.getResult().getString("school_str");
                            b_edit_dpv.setEnabled(true);
                            b_remove_dpv.setEnabled(true);
                            if(imageUri != null){
                                Picasso.get().load(imageUri).fit().centerCrop().into(iv_dpv);
                            }else{
                                Picasso.get().load(url).fit().centerCrop().into(iv_dpv);
                            }
                        }else{
                            Toast.makeText(DisplayPictureView.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}