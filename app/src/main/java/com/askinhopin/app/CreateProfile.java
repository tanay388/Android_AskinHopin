package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateProfile extends AppCompatActivity {
    private EditText email_Profile,fullName_Profile,phone_Profile,batch_Profile,bio_Profile,facebook_Profile,insta_Profile,
            linkedin_Profile,twitter_Profile,website_Profile;
    private ProgressBar progressbar_Profile;
    private TextView school_Profile;
    private Button save_Profile;
    private ImageView dp_Profile;
    private RadioGroup gender_Profile;
    private Spinner branchSelction_Profile;
    private Uri imageUri;
    private String branch_str="none";
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;
    String uid;
    ProfileUser profileUser;


    String email_str;

    String path;
    Bitmap image_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        school_Profile = findViewById(R.id.school_Profile);

        email_Profile = findViewById(R.id.email_Profile);
        fullName_Profile = findViewById(R.id.fullName_Profile);
        phone_Profile = findViewById(R.id.phone_Profile);
        batch_Profile = findViewById(R.id.batch_Profile);
        bio_Profile = findViewById(R.id.bio_Profile);
        facebook_Profile = findViewById(R.id.facebook_Profile);
        insta_Profile = findViewById(R.id.insta_Profile);
        linkedin_Profile = findViewById(R.id.linkedin_Profile);
        twitter_Profile = findViewById(R.id.twitter_Profile);
        website_Profile = findViewById(R.id.website_Profile);

        progressbar_Profile = findViewById(R.id.progressbar_Profile);

        save_Profile = findViewById(R.id.save_Profile);

        dp_Profile = findViewById(R.id.dp_Profile);

        gender_Profile = findViewById(R.id.gender_Profile);

        branchSelction_Profile = findViewById(R.id.branchSelction_Profile);
        List<String> branchArray = new ArrayList<String>();
        branchArray.add("Choose your branch :");
        branchArray.add("Chemical Engineering");
        branchArray.add("Civil Engineering");
        branchArray.add("Computer Science");
        branchArray.add("ECE");
        branchArray.add("Electrical Engineering");
        branchArray.add("Information Technology");
        branchArray.add("Mechanical Engineering");
        branchArray.add("Metallurgical Engineering");
        branchArray.add("Mining");
        branchArray.add("Production Engineering");

        ArrayAdapter<String> adapterSpinnerBranch = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branchArray);
        branchSelction_Profile.setAdapter(adapterSpinnerBranch);

        request();


        profileUser = new ProfileUser();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef= databaseReference.child("All_Users").child(uid).child("school");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String school_name = snapshot.getValue(String.class);
                school_Profile.setText(school_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mRef= databaseReference.child("All_Users").child(uid).child("email");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String school_name = snapshot.getValue(String.class);
                email_Profile.setText(school_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        email_Profile.setEnabled(false);

        branchSelction_Profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){

                }else{
                    branch_str = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String current_user_school_name= school_Profile.getText().toString();

        documentReference = db.collection("user").document(uid);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");

        save_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_str= email_Profile.getText().toString();
                uploadData();
            }
        });

        dp_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });




    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data !=null || data.getData() != null){
                imageUri = data.getData();
                Picasso.get().load(imageUri).fit().centerCrop().into(dp_Profile);
            }

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    public String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }


    private void uploadData() {

        String email_str= email_Profile.getText().toString().trim();
        String name_str= fullName_Profile.getText().toString().trim();
        String phone_str= phone_Profile.getText().toString().trim();
        String gender_str= "none";
        if(gender_Profile.getCheckedRadioButtonId() == R.id.genderMale_Profile){
            gender_str = "Male";
        }
        if(gender_Profile.getCheckedRadioButtonId() == R.id.genderFemale_Profile){
            gender_str = "Female";
        }
        String school_str = school_Profile.getText().toString().trim();
        String batch_str = batch_Profile.getText().toString().trim();



        String bio_str = bio_Profile.getText().toString().trim();
        String fb_str = facebook_Profile.getText().toString().trim();
        String insta_str = insta_Profile.getText().toString().trim();
        String linkedin_str = linkedin_Profile.getText().toString().trim();
        String twitter_str = twitter_Profile.getText().toString().trim();
        String web_str = website_Profile.getText().toString().trim();

        if(!TextUtils.isEmpty(email_str) && !TextUtils.isEmpty(name_str) && !TextUtils.isEmpty(school_str) && !TextUtils.isEmpty(batch_str)
            && !"none".equals(branch_str) && !"none".equals(gender_str)){

            progressbar_Profile.setVisibility(View.VISIBLE);
            save_Profile.setVisibility(View.INVISIBLE);
            if(imageUri != null){
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
                uploadTask = reference.putFile(imageUri);

                String finalGender_str = gender_str;
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

                        Map<String, String> profile = new HashMap<>();
                        profile.put("name_str", name_str.toUpperCase() );
                        profile.put("email_str", email_str );
                        profile.put("gender_str", finalGender_str);
                        profile.put("school_str", school_str );
                        profile.put("batch_str", batch_str);
                        profile.put("phone_str", phone_str);
                        profile.put("branch_str", branch_str);
                        profile.put("bio_str", bio_str );
                        profile.put("fb_str", fb_str );
                        profile.put("insta_str", insta_str );
                        profile.put("linkedin_str", linkedin_str );
                        profile.put("twitter_str", twitter_str );
                        profile.put("web_str", web_str );
                        profile.put("uid", uid);
                        profile.put("imageurl",downloadurl.toString());

                        profileUser.setName(name_str.toUpperCase());
                        profileUser.setEmail(email_str);
                        profileUser.setPhone(phone_str);
                        profileUser.setGender(finalGender_str);
                        profileUser.setSchool(school_str);
                        profileUser.setBranch(branch_str);
                        profileUser.setBatch(batch_str);
                        profileUser.setBio(bio_str);
                        profileUser.setFb(fb_str);
                        profileUser.setInsta(insta_str);
                        profileUser.setLinkedin(linkedin_str);
                        profileUser.setTwitter(twitter_str);
                        profileUser.setWeb(web_str);
                        profileUser.setUid(uid);
                        profileUser.setImgurl(downloadurl.toString());

                        databaseReference.child(school_str).child("user").child(uid).setValue(profileUser);

                        documentReference.set(profile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressbar_Profile.setVisibility(View.VISIBLE);
                                        Toast.makeText(CreateProfile.this, "Profile Created Successfully !", Toast.LENGTH_SHORT).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(CreateProfile.this , MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        },2000);
                                    }
                                });


                    }
                });
            }
            else{
                String randImg = getImageUrl();
                Map<String, String> profile = new HashMap<>();
                profile.put("name_str", name_str.toUpperCase() );
                profile.put("email_str", email_str );
                profile.put("gender_str", gender_str);
                profile.put("school_str", school_str );
                profile.put("batch_str", batch_str);
                profile.put("phone_str", phone_str);
                profile.put("branch_str", branch_str);
                profile.put("bio_str", bio_str );
                profile.put("fb_str", fb_str );
                profile.put("insta_str", insta_str );
                profile.put("linkedin_str", linkedin_str );
                profile.put("twitter_str", twitter_str );
                profile.put("web_str", web_str );
                profile.put("uid", uid);
                profile.put("imageurl",randImg);

                profileUser.setName(name_str.toUpperCase());
                profileUser.setEmail(email_str);
                profileUser.setPhone(phone_str);
                profileUser.setGender(gender_str);
                profileUser.setSchool(school_str);
                profileUser.setBranch(branch_str);
                profileUser.setBio(bio_str);
                profileUser.setFb(fb_str);
                profileUser.setInsta(insta_str);
                profileUser.setLinkedin(linkedin_str);
                profileUser.setTwitter(twitter_str);
                profileUser.setWeb(web_str);
                profileUser.setUid(uid);
                profileUser.setImgurl(randImg);

                databaseReference.child(school_str).child("user").child(uid).setValue(profileUser);

                documentReference.set(profile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressbar_Profile.setVisibility(View.VISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created Successfully !", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Fragment1 fragment1 = new Fragment1();
//                                        FragmentManager manager = getSupportFragmentManager();
//                                        FragmentTransaction transaction = manager.beginTransaction();
//                                        transaction.add(fragment1, "create profile");
//                                        transaction.commit();
                                        Intent intent = new Intent(CreateProfile.this , Fragment1.class);
                                        startActivity(intent);

                                    }
                                },2000);
                            }
                        });

            }

        }
        else {
            Toast.makeText(this, "Enter All the Required Fields or try reselecting branch" , Toast.LENGTH_SHORT).show();
        }


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


    private  void request(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> list) {
                Toast.makeText(CreateProfile.this, "Permission is required for posting and saving media.", Toast.LENGTH_SHORT).show();

            }
        };

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    


}