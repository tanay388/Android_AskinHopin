package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AskQuestion extends AppCompatActivity {

    EditText et_title_aq, et_discription_aq, et_tags_aq;
    Spinner spinner_category_aq;
    CheckBox cb_anonymous_aq, cb_tnc_aq;
    ProgressBar progressbar_aq;
    ImageButton ib_back_aq;
    String category_str, school_str, name_str, url_str, uid_str;
    TextView b_post_aq;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestion, UserQuestion ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    questionMember member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        et_title_aq = findViewById(R.id.et_title_aq);
        et_discription_aq = findViewById(R.id.et_discription_aq);
        spinner_category_aq = findViewById(R.id.spinner_category_aq);
        et_tags_aq = findViewById(R.id.tags_aq);
        cb_anonymous_aq = findViewById(R.id.cb_anonymous_aq);
        cb_tnc_aq = findViewById(R.id.cb_tnc_aq);
        b_post_aq = findViewById(R.id.b_post_aq);
        progressbar_aq = findViewById(R.id.progressbar_aq);
        ib_back_aq = findViewById(R.id.ib_back_aq);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        List<String> category = new ArrayList<String>();
        category.add("Choose a category :");
        category.add("Academics");
        category.add("Arts and Litrature");
        category.add("Coding");
        category.add("College");
        category.add("Placement / Internships");
        category.add("Startups");
        category.add("Others");

        ArrayAdapter<String> adapterSpinnerCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
        spinner_category_aq.setAdapter(adapterSpinnerCategory);

        spinner_category_aq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Source location
                if(position != 0)
                category_str = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        documentReference = db.collection("user").document(currentuid);


        member = new questionMember();

        b_post_aq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar_aq.setVisibility(View.VISIBLE);

                String title = et_title_aq.getText().toString().trim();
                String discription = et_discription_aq.getText().toString().trim();
                String tags = et_tags_aq.getText().toString().trim();

                Calendar cdate = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String savedate = currentdate.format(cdate.getTime());

                Calendar ctime = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate + " | " + savetime;

                if(cb_tnc_aq.isChecked() == true){
                    if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(discription) && !TextUtils.isEmpty(category_str) ){

                        long tinmilli = (-1)*ctime.getTimeInMillis();

                        member.setCategory(category_str);
                        member.setTitle(title);
                        member.setDiscription(discription);
                        member.setTags(tags);
                        member.setName(name_str);
                        member.setUid(uid_str);
                        member.setUrl(url_str);
                        member.setTime(time);
                        member.setRev_timemilies(tinmilli);
                        if(cb_anonymous_aq.isChecked()){
                            member.setAnonymous(true);
                        }

                        if(!TextUtils.isEmpty(school_str)){
                            AllQuestion = database.getReference().child(school_str).child("All Questions");
                            UserQuestion = database.getReference().child(school_str).child("User Questions").child(currentuid);

                            String id = Long.toString((-1*tinmilli));
                            UserQuestion.child(id).setValue(member);
                            member.setKey(id);

                            AllQuestion.child(id).setValue(member);

                            Toast.makeText(AskQuestion.this, "Your Question Has been Posted Successfully!", Toast.LENGTH_SHORT).show();
                            b_post_aq.setEnabled(false);
                            progressbar_aq.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(AskQuestion.this, "Error Fetching School Detail", Toast.LENGTH_SHORT).show();
                        }



                    }else{
                        progressbar_aq.setVisibility(View.GONE);
                        Toast.makeText(AskQuestion.this, "Enter All The Required Fields", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    progressbar_aq.setVisibility(View.GONE);
                    Toast.makeText(AskQuestion.this, "You Must agree to T&C in order to post a question.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        ib_back_aq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                            url_str = task.getResult().getString("imageurl");
                            name_str = task.getResult().getString("name_str");
                            uid_str = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            progressbar_aq.setVisibility(View.GONE);

                        }else{
                            progressbar_aq.setVisibility(View.GONE);
                            Toast.makeText(AskQuestion.this, "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressbar_aq.setVisibility(View.GONE);
                Toast.makeText(AskQuestion.this, "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}