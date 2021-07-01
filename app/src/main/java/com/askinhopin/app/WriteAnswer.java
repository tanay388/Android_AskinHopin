package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WriteAnswer extends AppCompatActivity {

    ImageButton ib_back_writeans;
    TextView b_post_writeans;
    EditText et_title_writeans;
    CheckBox cb_anonymous_writeans, cb_tnc_writeans;
    String uid, school, url, name, key, title, disc, time, category;
    questionMember member;
    AnswerClassMember answerClassMember;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions, Notificationdb;
    NotificationClass notificationClass;
    ProgressBar progressbar_writeanswer;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String currName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_answer);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ib_back_writeans = findViewById(R.id.ib_back_writeans);
        b_post_writeans = findViewById(R.id.b_post_writeans);
        et_title_writeans =findViewById(R.id.et_title_writeans);
        cb_anonymous_writeans = findViewById(R.id.cb_anonymous_writeans);
        cb_tnc_writeans = findViewById(R.id.cb_tnc_writeans);
        progressbar_writeanswer =findViewById(R.id.progressbar_writeanswer);

        member = new questionMember();
        answerClassMember = new AnswerClassMember();
        notificationClass = new NotificationClass();
        Bundle extra = getIntent().getExtras();

        if(extra != null){
            uid = extra.getString("Uid");
            url = extra.getString("Url");
            name = extra.getString("Name");
            school = extra.getString("School");
            key = extra.getString("Key");
            title = extra.getString("Title");
            disc = extra.getString("Discription");
            time = extra.getString("Time");
            category = extra.getString("Category");
        }else{
            Toast.makeText(this, "Opps, Error Loading!", Toast.LENGTH_SHORT).show();
        }

        AllQuestions = database.getReference().child(school).child("All Questions").child(key).child("Answer");
        Notificationdb = database.getReference().child(school).child("user").child(uid).child("notification");


        documentReference = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            currName = task.getResult().getString("name_str");


                        }else{
                            Toast.makeText(getApplicationContext(), "No Profile Found", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        b_post_writeans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveans();
            }
        });


        ib_back_writeans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void saveans() {
        String answer = et_title_writeans.getText().toString().trim();



        if(answer != null){
            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            String finaltime = savedate + " | " + savetime;

            answerClassMember.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            answerClassMember.setTime(finaltime);
            answerClassMember.setAnswer(answer);
            answerClassMember.setRev_timemillies((-1)*ctime.getTimeInMillis());


            notificationClass.setType("Post");
            notificationClass.setPosttype("Answer_added");
            notificationClass.setUnread(true);
            notificationClass.setPostid(key);
            notificationClass.setSentuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationClass.setRecivinguid(uid);
            notificationClass.setTime(finaltime);
            notificationClass.setPostTime(time);
            notificationClass.setCategory(category);
            notificationClass.setTitle(title);
            notificationClass.setDiscription(disc);
            notificationClass.setRev_timemillies((-1)*ctime.getTimeInMillis());
            String smallTitle = title.substring(0, Math.min(40, title.length()));
            notificationClass.setContent(currName + " " + getResources().getString(R.string.answer_add_string) + " ' " + smallTitle + " '.");

            String id = AllQuestions.push().getKey();
            answerClassMember.setKey(id);
            String notification_key= Notificationdb.push().getKey();
            notificationClass.setKey(notification_key);
            AllQuestions.child(id).setValue(answerClassMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Notificationdb.child(notification_key).setValue(notificationClass);
                }
            });

            Toast.makeText(this, "Your answer is successfully submitted", Toast.LENGTH_SHORT).show();
            b_post_writeans.setEnabled(false);
            b_post_writeans.setBackgroundColor(getResources().getColor(R.color.gray));

        }else{
            Toast.makeText(this, "Please write answer to continue.", Toast.LENGTH_SHORT).show();
        }

    }


}