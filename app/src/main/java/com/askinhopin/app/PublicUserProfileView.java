package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PublicUserProfileView extends AppCompatActivity {
    ImageView iv_back_allprofile, iv_dp_allprofile, iv_fb_allprofile, iv_insta_allprofile, iv_linkedin_allprofile, iv_twitter_allprofile;
    TextView tv_nameuser_allprofile, tv_batch_allprofile, tv_noOfPost_allprofile, tv_bio_allprofile, tv_noOfQuestions_allprofile, tv_phone_allprofile,
            tv_email_allprofile, tv_web_allprofile;
    String currentuid, school_str;
    DocumentReference documentReference;
    ProgressBar progressbar_allprofile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    String fbResult, instaResult, linkedinResult, twitterResult, webResult;
    ScrollView scrollview_allprofile;
    LinearLayout ll_phone_allprofile, ll_web_allprofile, ll_questionsholder_allprofile, ll_postholder_allprofile;
    RecyclerView rv_batch_allprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_user_profile_view);

        iv_back_allprofile = findViewById(R.id.iv_back_allprofile);
        iv_dp_allprofile = findViewById(R.id.iv_dp_allprofile);
        tv_nameuser_allprofile = findViewById(R.id.tv_nameuser_allprofile);
        tv_batch_allprofile = findViewById(R.id.tv_batch_allprofile);
        tv_noOfPost_allprofile = findViewById(R.id.tv_noOfPost_allprofile);
        tv_bio_allprofile = findViewById(R.id.tv_bio_allprofile);
        tv_noOfQuestions_allprofile = findViewById(R.id.tv_noOfQuestions_allprofile);
        tv_phone_allprofile = findViewById(R.id.tv_phone_allprofile);
        tv_email_allprofile = findViewById(R.id.tv_email_allprofile);
        tv_web_allprofile = findViewById(R.id.tv_web_allprofile);
        iv_fb_allprofile = findViewById(R.id.iv_fb_allprofile);
        iv_insta_allprofile = findViewById(R.id.iv_insta_allprofile);
        iv_linkedin_allprofile = findViewById(R.id.iv_linkedin_allprofile);
        iv_twitter_allprofile = findViewById(R.id.iv_twitter_allprofile);
        progressbar_allprofile = findViewById(R.id.progressbar_allprofile);
        scrollview_allprofile = findViewById(R.id.scrollview_allprofile);
        ll_phone_allprofile = findViewById(R.id.ll_phone_allprofile);
        ll_web_allprofile = findViewById(R.id.ll_web_allprofile);
        ll_questionsholder_allprofile = findViewById(R.id.ll_questionsholder_allprofile);
        ll_postholder_allprofile = findViewById(R.id.ll_postholder_allprofile);
        rv_batch_allprofile = findViewById(R.id.rv_batch_allprofile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            currentuid = extra.getString("uid");
        }

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(PublicUserProfileView.this, RecyclerView.HORIZONTAL, false);
        rv_batch_allprofile.setLayoutManager(horizontalLayoutManager);

        List<BatchModel> batchModelList = new ArrayList<>();
        setBatches(batchModelList, currentuid);

        documentReference = db.collection("user").document(currentuid);
        databaseReference = database.getReference();

        ll_questionsholder_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicUserProfileView.this, YourQuestion.class);
                intent.putExtra("uid", currentuid);
                startActivity(intent);
            }
        });

        ll_postholder_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicUserProfileView.this, YourPosts.class);
                intent.putExtra("Uid", currentuid);
                startActivity(intent);
            }
        });

        iv_back_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Social url click intent
        iv_fb_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(fbResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_insta_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(instaResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_twitter_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(twitterResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_linkedin_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(linkedinResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_web_allprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(webResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setBatches(List<BatchModel> batchModelList, String currentuid) {
        DatabaseReference batchRef = databaseReference.child("Batches").child(currentuid);
        batchRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BatchModel batchModelChild = snapshot.getValue(BatchModel.class);
                batchModelList.add(batchModelChild);
//                Toast.makeText(getActivity(), "" + batchModelChild.getBatchDesc(), Toast.LENGTH_SHORT).show();


                BatchHorizontalAdaptar batchHorizontalAdaptar = new BatchHorizontalAdaptar(batchModelList);


                rv_batch_allprofile.setAdapter(batchHorizontalAdaptar);
                batchHorizontalAdaptar.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            school_str = task.getResult().getString("school_str");
                            String nameResult = task.getResult().getString("name_str");
                            String batchResult = task.getResult().getString("batch_str");
                            String branchResult = task.getResult().getString("branch_str");
                            String bioResult = task.getResult().getString("bio_str");
                            String emailResult = task.getResult().getString("email_str");
                            String phoneResult = task.getResult().getString("phone_str");
                            String imageurlResult = task.getResult().getString("imageurl");
                            fbResult = task.getResult().getString("fb_str");
                            instaResult = task.getResult().getString("insta_str");
                            linkedinResult = task.getResult().getString("linkedin_str");
                            twitterResult = task.getResult().getString("twitter_str");
                            webResult = task.getResult().getString("web_str");

                            if(TextUtils.isEmpty(fbResult)){
                                iv_fb_allprofile.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(instaResult)){
                                iv_insta_allprofile.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(twitterResult)){
                                iv_twitter_allprofile.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(linkedinResult)){
                                iv_linkedin_allprofile.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(webResult)){
                                ll_web_allprofile.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(phoneResult)){
                                ll_phone_allprofile.setVisibility(View.GONE);
                            }
                            if(!TextUtils.isEmpty(imageurlResult))
                                Picasso.get().load(imageurlResult).fit().centerCrop().into(iv_dp_allprofile);
                            tv_web_allprofile.setText(webResult);
                            tv_nameuser_allprofile.setText(nameResult);
                            tv_batch_allprofile.setText(branchResult + " | " + batchResult);
                            tv_bio_allprofile.setText(bioResult);
                            tv_email_allprofile.setText(emailResult);
                            tv_phone_allprofile.setText(phoneResult);

                            DatabaseReference dbRefPost = databaseReference.child(school_str).child("User Post").child(currentuid);

                            dbRefPost.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int votesCount = (int)snapshot.getChildrenCount();
                                            tv_noOfPost_allprofile.setText(Integer.toString(votesCount));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            DatabaseReference dbRefQuestn = databaseReference.child(school_str).child("User Questions").child(currentuid);

                            dbRefQuestn.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int votesCount = (int)snapshot.getChildrenCount();
                                    tv_noOfQuestions_allprofile.setText(Integer.toString(votesCount));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            progressbar_allprofile.setVisibility(View.GONE);
                            scrollview_allprofile.setVisibility(View.VISIBLE);


                        }else{
                            Toast.makeText(PublicUserProfileView.this, "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PublicUserProfileView.this, "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }
}