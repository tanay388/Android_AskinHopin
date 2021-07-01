package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class YourQuestion extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions, UserQuestions;
    DocumentReference reference;
    ProgressBar progressBar;
    ImageButton ib_back_yourques;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_your_question);
        ib_back_yourques =findViewById(R.id.ib_back_yourques);
        recyclerView = findViewById(R.id.rv_yourques);
        progressBar = findViewById(R.id.progressbar_yourques);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            currentuid = extra.getString("uid");
        }



        String finalCurrentuid = currentuid;
        reference = db.collection("user").document(finalCurrentuid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String school_str = task.getResult().getString("school_str");
                            AllQuestions = database.getReference().child(school_str).child("All Questions");
                            UserQuestions = database.getReference().child(school_str).child("User Questions").child(finalCurrentuid);
                            Query reverseQuery = UserQuestions.orderByChild("rev_timemilies");

                            FirebaseRecyclerOptions<questionMember> options = new FirebaseRecyclerOptions.Builder<questionMember>()
                                    .setQuery(reverseQuery, questionMember.class)
                                    .build();
                            FirebaseRecyclerAdapter<questionMember, ViewHolder_Question> firebaseRecyclerAdapter =
                                    new FirebaseRecyclerAdapter<questionMember, ViewHolder_Question>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull ViewHolder_Question holder, int position, @NonNull questionMember model) {

                                            final String postkey = getRef(position).getKey();

                                            holder.setitemdelete(getApplication(), model.getName(), model.getUid(), model.getUrl(), model.getKey(),
                                                    model.getTitle(), model.getDiscription(),model.getTime(), model.getCategory(), model.getTags());
                                            progressBar.setVisibility(View.INVISIBLE);
                                            final String time = getItem(position).getTime();

                                            if(finalCurrentuid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                holder.tv_delete_yourques_item.setVisibility(View.VISIBLE);
                                            }

                                            holder.tv_delete_yourques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(YourQuestion.this);

                                                    builder.setTitle("Delete")
                                                            .setMessage("Are you sure you want to delete this Question?\n You will not be able to retrive it again.")
                                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    delete(time);
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
                                            });
//                                            Toast.makeText(YourQuestion.this, ""+postkey, Toast.LENGTH_SHORT).show();

                                            holder.tv_reply_yourques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(YourQuestion.this, ReplyActivity.class);
                                                    intent.putExtra("Uid", model.getUid());
                                                    intent.putExtra("Name", model.getName());
                                                    intent.putExtra("Url", model.getUrl());
                                                    intent.putExtra("Key", postkey);
                                                    intent.putExtra("Title", model.getTitle());
                                                    intent.putExtra("Discription", model.getDiscription());
                                                    intent.putExtra("Time", model.getTitle());
                                                    intent.putExtra("Category", model.getCategory());
                                                    intent.putExtra("School", school_str);
                                                    startActivity(intent);
                                                }
                                            });

                                            holder.tv_title_yourques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(YourQuestion.this, ReplyActivity.class);
                                                    intent.putExtra("Uid", model.getUid());
                                                    intent.putExtra("Name", model.getName());
                                                    intent.putExtra("Url", model.getUrl());
                                                    intent.putExtra("Key", postkey);
                                                    intent.putExtra("Title", model.getTitle());
                                                    intent.putExtra("Discription", model.getDiscription());
                                                    intent.putExtra("Time", model.getTitle());
                                                    intent.putExtra("Category", model.getCategory());
                                                    intent.putExtra("School", school_str);
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @NonNull
                                        @Override
                                        public ViewHolder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext())
                                                    .inflate(R.layout.yourquestion_item, parent, false);

                                            return new ViewHolder_Question(view);
                                        }
                                    };

                            firebaseRecyclerAdapter.startListening();
                            recyclerView.setAdapter(firebaseRecyclerAdapter);

                        }else{
                            Toast.makeText(getApplication(), "No Profile Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });

        ib_back_yourques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void delete(String time) {


        Query query = UserQuestions.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(getApplication(), "Your Question has been removed successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query1 = AllQuestions.orderByChild("time").equalTo(time);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(getApplication(), "Your Question has been removed successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}