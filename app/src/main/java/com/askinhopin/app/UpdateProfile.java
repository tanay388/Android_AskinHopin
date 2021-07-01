package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    private EditText email_UProfile,fullName_UProfile,phone_UProfile,batch_UProfile,bio_UProfile,facebook_UProfile,insta_UProfile,
            linkedin_UProfile,twitter_UProfile,website_UProfile;
    private ProgressBar progressbar_UProfile,progressbar_top_up;
    private TextView school_UProfile;
    private Button save_UProfile;
    private Spinner branchSelction_UProfile;
    private String branch_str="none";
    DocumentReference documentreference;
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    String currentid, urlResult, genderResult;
    ProfileUser profileUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        school_UProfile = findViewById(R.id.school_UProfile);

        email_UProfile = findViewById(R.id.email_UProfile);
        fullName_UProfile = findViewById(R.id.fullName_UProfile);
        phone_UProfile = findViewById(R.id.phone_UProfile);
        batch_UProfile = findViewById(R.id.batch_UProfile);
        bio_UProfile = findViewById(R.id.bio_UProfile);
        facebook_UProfile = findViewById(R.id.facebook_UProfile);
        insta_UProfile = findViewById(R.id.insta_UProfile);
        linkedin_UProfile = findViewById(R.id.linkedin_UProfile);
        twitter_UProfile = findViewById(R.id.twitter_UProfile);
        website_UProfile = findViewById(R.id.website_UProfile);

        progressbar_UProfile = findViewById(R.id.progressbar_UProfile);
        progressbar_top_up = findViewById(R.id.progressbar_top_up);

        save_UProfile = findViewById(R.id.save_UProfile);
        branchSelction_UProfile = findViewById(R.id.branchSelction_UProfile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileUser = new ProfileUser();

        branchSelction_UProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 1)
                    branch_str = branchSelction_UProfile.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        save_UProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
            }
        });
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentid = user.getUid();

        documentreference = db.collection("user").document(currentid);

        documentreference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){

                            String nameResult = task.getResult().getString("name_str");
                            String batchResult = task.getResult().getString("batch_str");
                            String branchResult = task.getResult().getString("branch_str");
                            String bioResult = task.getResult().getString("bio_str");
                            String emailResult = task.getResult().getString("email_str");
                            String phoneResult = task.getResult().getString("phone_str");
                            String schoolResult = task.getResult().getString("school_str");
                            String fbResult = task.getResult().getString("fb_str");
                            String instaResult = task.getResult().getString("insta_str");
                            String linkedinResult = task.getResult().getString("linkedin_str");
                            String twitterResult = task.getResult().getString("twitter_str");
                            String webResult = task.getResult().getString("web_str");
                            urlResult = task.getResult().getString("imageurl");
                            genderResult =  task.getResult().getString("gender_str");

                            email_UProfile.setText(emailResult);
                            email_UProfile.setEnabled(false);
                            fullName_UProfile.setText(nameResult);
                            phone_UProfile.setText(phoneResult);
                            batch_UProfile.setText(batchResult);
                            if(!TextUtils.isEmpty(schoolResult))
                                school_UProfile.setText(schoolResult);

                            branchSelction_UProfile.setSelection(getIndex(branchSelction_UProfile, branchResult));
                            facebook_UProfile.setText(fbResult);
                            insta_UProfile.setText(instaResult);
                            twitter_UProfile.setText(twitterResult);
                            website_UProfile.setText(webResult);
                            linkedin_UProfile.setText(linkedinResult);
                            bio_UProfile.setText(bioResult);
                            progressbar_top_up.setVisibility(View.GONE);

                        }else{
                            Toast.makeText(UpdateProfile.this, "No Profile Exists", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UpdateProfile.this, CreateProfile.class);
                            startActivity(intent);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, "Error Loading Profile Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateprofile() {


        String email_str= email_UProfile.getText().toString().trim();
        String name_str= fullName_UProfile.getText().toString().trim().toUpperCase();
        String phone_str= phone_UProfile.getText().toString().trim();
        String school_str = school_UProfile.getText().toString().trim();
        String batch_str = batch_UProfile.getText().toString().trim();



        String bio_str = bio_UProfile.getText().toString().trim();
        String fb_str = facebook_UProfile.getText().toString().trim();
        String insta_str = insta_UProfile.getText().toString().trim();
        String linkedin_str = linkedin_UProfile.getText().toString().trim();
        String twitter_str = twitter_UProfile.getText().toString().trim();
        String web_str = website_UProfile.getText().toString().trim();

        if(email_str.length()>3 && name_str.length()>1 && school_str.length()>1 && batch_str.length()>3 && bio_str.length()>0){
            final DocumentReference sDoc = db.collection("user").document(currentid);
            progressbar_UProfile.setVisibility(View.VISIBLE);
            save_UProfile.setVisibility(View.INVISIBLE);


            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                    transaction.update(sDoc, "name_str", name_str);
                    transaction.update(sDoc, "batch_str", batch_str);
                    transaction.update(sDoc, "phone_str", phone_str);
                    if(branch_str != "none")
                        transaction.update(sDoc, "branch_str", branch_str);
                    transaction.update(sDoc, "bio_str", bio_str);
                    transaction.update(sDoc, "fb_str", fb_str);
                    transaction.update(sDoc, "insta_str", insta_str);
                    transaction.update(sDoc, "linkedin_str", linkedin_str);
                    transaction.update(sDoc, "twitter_str", twitter_str);
                    transaction.update(sDoc, "web_str", web_str);

                    profileUser.setName(name_str);
                    profileUser.setEmail(email_str);
                    profileUser.setPhone(phone_str);
                    profileUser.setGender(genderResult);
                    profileUser.setSchool(school_str);
                    profileUser.setBatch(batch_str);
                    profileUser.setBranch(branch_str);
                    profileUser.setBio(bio_str);
                    profileUser.setFb(fb_str);
                    profileUser.setInsta(insta_str);
                    profileUser.setLinkedin(linkedin_str);
                    profileUser.setTwitter(twitter_str);
                    profileUser.setWeb(web_str);
                    profileUser.setUid(currentid);
                    profileUser.setImgurl(urlResult);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(school_str).child("user").child(currentid).setValue(profileUser);
                    progressbar_UProfile.setVisibility(View.GONE);
                    save_UProfile.setVisibility(View.VISIBLE);
                    Toast.makeText(UpdateProfile.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar_UProfile.setVisibility(View.GONE);
                            save_UProfile.setVisibility(View.VISIBLE);
                            Toast.makeText(UpdateProfile.this, "failed " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "Fill all the required data", Toast.LENGTH_SHORT).show();
        }


    }
}