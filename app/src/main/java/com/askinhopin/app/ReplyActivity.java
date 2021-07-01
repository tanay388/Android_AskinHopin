package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReplyActivity extends AppCompatActivity {

    ImageButton ib_back_reply;
    TextView tv_name_ques_reply, tv_time_ques_reply, tv_category_ques_reply, tv_title_ques_reply, tv_disc_ques_reply, tv_add_reply_user;
    RecyclerView rv_answers;
    ImageView iv_dp_reply_user, iv_dp_ques_reply;
    String uid, school, url, name, key, title, disc, time, category, nameResult;
    DocumentReference reference, reference1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions, Notificationdb;
    DatabaseReference votesRefrence;
    Boolean checkUpvote = false;
    NotificationClass notificationClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ib_back_reply= findViewById(R.id.ib_back_reply);
        tv_name_ques_reply = findViewById(R.id.tv_name_ques_reply);
        tv_time_ques_reply = findViewById(R.id.tv_time_ques_reply);
        tv_category_ques_reply = findViewById(R.id.tv_category_ques_reply);
        tv_title_ques_reply = findViewById(R.id.tv_title_ques_reply);
        tv_disc_ques_reply = findViewById(R.id.tv_disc_ques_reply);
        tv_add_reply_user = findViewById(R.id.tv_add_reply_user);
        iv_dp_reply_user = findViewById(R.id.iv_dp_reply_user);
        iv_dp_ques_reply = findViewById(R.id.iv_dp_ques_reply);
        rv_answers = findViewById(R.id.rv_answers);
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

        reference1 = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference = db.collection("user").document(uid);

        rv_answers.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));
        votesRefrence = FirebaseDatabase.getInstance().getReference().child(school).child("votes");

        AllQuestions = database.getReference().child(school).child("All Questions").child(key).child("Answer");
        Notificationdb = database.getReference().child(school).child("user").child(uid).child("notification");

        ib_back_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_add_reply_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyActivity.this, WriteAnswer.class);
                intent.putExtra("Uid", uid);
                intent.putExtra("Name", name);
                intent.putExtra("Url", url);
                intent.putExtra("Key", key);
                intent.putExtra("Title", title);
                intent.putExtra("Discription", disc);
                intent.putExtra("Time", time);
                intent.putExtra("Category", category);
                intent.putExtra("School", school);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Question User Refrence
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");
                            nameResult = task.getResult().getString("name_str");


                            tv_name_ques_reply.setText(nameResult);
                            tv_time_ques_reply.setText(time);
                            tv_category_ques_reply.setText(category);
                            tv_title_ques_reply.setText(title);
                            tv_disc_ques_reply.setText(disc);

                            if(!TextUtils.isEmpty(imageurlResult))
                                Picasso.get().load(imageurlResult).fit().centerCrop().into(iv_dp_ques_reply);


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });

        //Refrence for Replying User
        reference1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult1 = task.getResult().getString("imageurl");

                            if(!TextUtils.isEmpty(imageurlResult1))
                                Picasso.get().load(imageurlResult1).fit().centerCrop().into(iv_dp_reply_user);


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });

        Query reverseQuery = AllQuestions.orderByChild("rev_timemilies");

        FirebaseRecyclerOptions<AnswerClassMember> options = new FirebaseRecyclerOptions.Builder<AnswerClassMember>()
                .setQuery(reverseQuery, AnswerClassMember.class)
                .build();
        FirebaseRecyclerAdapter<AnswerClassMember, ViewHolder_Answer> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AnswerClassMember, ViewHolder_Answer>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_Answer holder, int position, @NonNull AnswerClassMember model) {

                        String ansKey = getRef(position).getKey();
                        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        holder.setAnswer(getApplication(), model.getUid(), model.getTime(), model.getAnswer());

                        holder.upvotechecker(ansKey, school);







                        holder.tv_like_answer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkUpvote = true;
                                votesRefrence.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(checkUpvote==true){
                                            if(snapshot.child(ansKey).hasChild(currUid)){
                                                votesRefrence.child(ansKey).child(currUid).removeValue();
                                                for (Drawable drawable : holder.tv_like_answer.getCompoundDrawables()) {
                                                    if (drawable != null) {
                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_answer.getContext(), R.color.gray), PorterDuff.Mode.SRC_IN));
                                                    }
                                                }

                                                String votes= holder.tv_like_answer.getText().toString();
                                                int vot = Integer.parseInt(votes);
                                                vot--;
                                                holder.tv_like_answer.setText(Integer.toString(vot));
                                                checkUpvote = false;



                                            }else{
                                                votesRefrence.child(ansKey).child(currUid).setValue(true);
                                                checkUpvote = false;
                                                for (Drawable drawable : holder.tv_like_answer.getCompoundDrawables()) {
                                                    if (drawable != null) {
                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_answer.getContext(), R.color.text_color2), PorterDuff.Mode.SRC_IN));
                                                    }
                                                }

                                                String votes= holder.tv_like_answer.getText().toString();
                                                int vot = Integer.parseInt(votes);
                                                vot++;
                                                holder.tv_like_answer.setText(Integer.toString(vot));


                                                Calendar cdate = Calendar.getInstance();
                                                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String savedate = currentdate.format(cdate.getTime());

                                                Calendar ctime = Calendar.getInstance();
                                                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                                                final String savetime = currenttime.format(ctime.getTime());

                                                String finaltime = savedate + " | " + savetime;


                                                notificationClass.setType("Like");
                                                notificationClass.setPosttype("Answer_liked");
                                                notificationClass.setUnread(true);
                                                notificationClass.setPostid(ansKey);
                                                notificationClass.setSentuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                notificationClass.setRecivinguid(uid);
                                                notificationClass.setTime(finaltime);
                                                notificationClass.setPostTime(time);
                                                notificationClass.setCategory(category);
                                                notificationClass.setTitle(title);
                                                notificationClass.setDiscription(disc);
                                                notificationClass.setRev_timemillies((-1)*ctime.getTimeInMillis());

                                                String smallTitle = title.substring(0, Math.min(20, title.length()));
                                                String smallAnswer = model.getAnswer().substring(0, Math.min(20, model.getAnswer().length()));
                                                notificationClass.setContent(nameResult + " and " +(vot-1) + " others liked your answer "+ "' "+smallAnswer+ " '"+ " on Question " + " ' " + smallTitle + " '.");
                                                notificationClass.setKey(ansKey);
                                                Notificationdb.child(ansKey).setValue(notificationClass);

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });

                        holder.tv_name_ans_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ReplyActivity.this, PublicUserProfileView.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });

                        if(currUid.equals(model.getUid())){
                            holder.tv_delete_ans_item.setVisibility(View.VISIBLE);
                        }

                        holder.tv_delete_ans_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDelete(model.getTime());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder_Answer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.answer_item, parent, false);

                        return new ViewHolder_Answer(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
//        progressbar_f2.setVisibility(View.INVISIBLE);
        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rv_answers.setLayoutManager(mLayoutManager);
        rv_answers.setAdapter(firebaseRecyclerAdapter);
    }


    private void confirmDelete(String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyActivity.this);

        builder.setTitle("Delete Post")
                .setMessage("Are you sure you want to Delete this Post?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(key);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create();
        builder.show();
    }

    private void deletePost(String key) {
        Query query = AllQuestions.orderByChild("time").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}