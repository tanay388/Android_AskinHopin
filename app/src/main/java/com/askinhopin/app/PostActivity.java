package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class PostActivity extends AppCompatActivity {

    private LinearLayout header_addallpost;
    private TextView tv_heading_addallpost, tv_name_addallpost;
    private ImageView ib_back_addallpost, dp_addallpost, iv_post_addallpost;
    private Button b_savepost_addallpost;
    private EditText et_postcontent_addallpost;
    private ProgressBar progressbar_addallpost;
    String role="none", school="none";
    private Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, UserPost, AllPost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;
    String uid, url,timeinmillies;
    byte[] final_image;
    FeedPostClass feedPostClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        header_addallpost = findViewById(R.id.header_addallpost);
        tv_heading_addallpost = findViewById(R.id.tv_heading_addallpost);
        ib_back_addallpost = findViewById(R.id.ib_back_addallpost);
        tv_name_addallpost = findViewById(R.id.tv_name_addallpost);
        dp_addallpost = findViewById(R.id.dp_addallpost);
        iv_post_addallpost = findViewById(R.id.iv_post_addallpost);
        b_savepost_addallpost = findViewById(R.id.b_savepost_addallpost);
        et_postcontent_addallpost = findViewById(R.id.et_postcontent_addallpost);
        progressbar_addallpost = findViewById(R.id.progressbar_addallpost);

        documentReference = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        feedPostClass = new FeedPostClass();

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            role = extra.getString("role");
            if(role.equals("feed")){
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = this.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(this.getResources().getColor(R.color.feed));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    header_addallpost.setBackgroundColor(this.getResources().getColor(R.color.feed));
                    tv_heading_addallpost.setText("Add Your Feeds");
                }
            }else if(role.equals("blogs")){
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = this.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(this.getResources().getColor(R.color.blog));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    header_addallpost.setBackgroundColor(this.getResources().getColor(R.color.blog));
                    tv_heading_addallpost.setText("Add Blogs");
                }
            }else if(role.equals("news")){
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = this.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(this.getResources().getColor(R.color.news));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    header_addallpost.setBackgroundColor(this.getResources().getColor(R.color.news));
                    tv_heading_addallpost.setText("Add News");
                }
            }

        }else {
            Toast.makeText(this, "Opps! Some Unexpected Error", Toast.LENGTH_SHORT).show();
        }

        storageReference = FirebaseStorage.getInstance().getReference("Post images");

        ib_back_addallpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        b_savepost_addallpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createpost();
            }
        });
        
        iv_post_addallpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
                chooseimage();
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
                Picasso.get().load(imageUri).into(iv_post_addallpost);

            }

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_LONG).show();
        }
    }

    private void createpost() {

        String discription = et_postcontent_addallpost.getText().toString().trim() ;
        if(discription != null && imageUri != null){
            b_savepost_addallpost.setEnabled(false);
            b_savepost_addallpost.setVisibility(View.INVISIBLE);
            progressbar_addallpost.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(uid).child(System.currentTimeMillis() + "." + "jpg");
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

                    Calendar cdate = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String savedate = currentdate.format(cdate.getTime());

                    Calendar ctime = Calendar.getInstance();
                    SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                    final String savetime = currenttime.format(ctime.getTime());

                    String time = savedate + " | " + savetime;
                    long tmili =ctime.getTimeInMillis();
                    feedPostClass.setTime(time);
                    feedPostClass.setDiscription(discription);
                    feedPostClass.setUid(uid);
                    feedPostClass.setUrl(downloadurl.toString());
                    feedPostClass.setRev_timemillies(((-1)*tmili));

                    DatabaseReference postRef = databaseReference.child(uid);
                    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("post_key");
                    final String[] key = new String[1];

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean done=false;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            key[0] = snapshot.getValue(String.class);

                            if(done == false){

                                feedPostClass.setKey(key[0]);
                                postRef.child(key[0]).setValue(feedPostClass);
                                AllPost = database.getReference().child(school).child("All Posts").child(key[0]);
                                AllPost.setValue(feedPostClass)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                long k = Long.parseLong(key[0]);
                                                k--;
                                                mRef.setValue(Long.toString(k));
                                                Toast.makeText(PostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                progressbar_addallpost.setVisibility(View.INVISIBLE);
                                                b_savepost_addallpost.setVisibility(View.VISIBLE);
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onBackPressed();
                                                    }
                                                },2000);
                                            }
                                        });

                            }
                            done = true;


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });







                }
            });
        }else{
            Toast.makeText(this, "Please Select a Image and write a description.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            school = task.getResult().getString("school_str");
                            String nameResult = task.getResult().getString("name_str");
                            String urlResult = task.getResult().getString("imageurl");
                            tv_name_addallpost.setText(nameResult);

                            if(urlResult != null){
                                Picasso.get().load(urlResult).fit().centerCrop().into(dp_addallpost);
                            }
                            b_savepost_addallpost.setEnabled(true);

                            databaseReference = database.getReference().child(school).child("User Post");

                        }else{
                            Toast.makeText(PostActivity.this, "No Profile Found", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private  void request(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> list) {
                Toast.makeText(PostActivity.this, "Permission is required for posting and saving media.", Toast.LENGTH_SHORT).show();

            }
        };

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}