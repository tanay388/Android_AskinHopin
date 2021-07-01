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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class YourPosts extends AppCompatActivity {

    ProgressBar progressbar_yourPosts;
    ImageButton ib_back_yourPost;
    RecyclerView rv_yourPost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String current_uid, school;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, UserQuestions, votesRefrence;
    Boolean checkUpvote = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_posts);

        ib_back_yourPost = findViewById(R.id.ib_back_yourPost);
        rv_yourPost = findViewById(R.id.rv_yourPost);
        progressbar_yourPosts = findViewById(R.id.progressbar_yourPosts);
        rv_yourPost.setHasFixedSize(true);
        rv_yourPost.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = user.getUid();
        documentReference = db.collection("user").document(current_uid);

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            current_uid = extra.getString("Uid");
        }



        ib_back_yourPost.setOnClickListener(new View.OnClickListener() {
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
                            school = task.getResult().getString("school_str");

                            retrivePost();

                        }else{
                            Toast.makeText(YourPosts.this, "No Profile Found", Toast.LENGTH_SHORT).show();
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(YourPosts.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void retrivePost() {
//        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);

        databaseReference = database.getReference().child(school).child("All Posts");

        UserQuestions = database.getReference().child(school).child("User Post").child(current_uid);
        votesRefrence = FirebaseDatabase.getInstance().getReference().child(school).child("votes");
        Query reverseQuery = UserQuestions.orderByChild("rev_timemillies");
        FirebaseRecyclerOptions<FeedPostClass> options = new FirebaseRecyclerOptions.Builder<FeedPostClass>()
                .setQuery(reverseQuery, FeedPostClass.class)
                .build();

        FirebaseRecyclerAdapter<FeedPostClass, ViewHolder_Post> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FeedPostClass, ViewHolder_Post>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_Post holder, int position, @NonNull FeedPostClass model) {
                        holder.setPostFeedApplication(getApplication(), model.getKey(), model.getUid(), model.getTime(), model.getUrl(),
                                model.getDiscription());

                        String uid_strResult = getItem(position).getUid();
                        String time = getItem(position).getTime();
                        String ansKey = getRef(position).getKey();
                        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                        holder.tv_name_feedpost_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(YourPosts.this, PublicUserProfileView.class);
                                intent.putExtra("uid", uid_strResult);
                                startActivity(intent);
                            }
                        });

                        if(uid_strResult.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            holder.tv_delete_feedpost_item.setVisibility(View.VISIBLE);
                        }

                        holder.tv_delete_feedpost_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDelete(time, model.getUrl());
                            }
                        });

                        holder.upvotechecker(ansKey, school);

                        holder.tv_like_feedpost_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkUpvote = true;
                                votesRefrence.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(checkUpvote==true){
                                            if(snapshot.child(ansKey).hasChild(currUid)){
                                                votesRefrence.child(ansKey).child(currUid).removeValue();
                                                holder.tv_like_feedpost_item.setTextColor(getResources().getColor(R.color.text_color));
                                                for (Drawable drawable : holder.tv_like_feedpost_item.getCompoundDrawables()) {
                                                    if (drawable != null) {
                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_feedpost_item.getContext(), R.color.gray), PorterDuff.Mode.SRC_IN));
                                                    }
                                                }

                                                String votes= holder.tv_like_feedpost_item.getText().toString();
                                                int vot = Integer.parseInt(votes);
                                                vot--;
                                                holder.tv_like_feedpost_item.setText(Integer.toString(vot));
                                                checkUpvote = false;
                                            }else{
                                                votesRefrence.child(ansKey).child(currUid).setValue(true);
                                                checkUpvote = false;
                                                holder.tv_like_feedpost_item.setTextColor(getResources().getColor(R.color.text_color2));
                                                for (Drawable drawable : holder.tv_like_feedpost_item.getCompoundDrawables()) {
                                                    if (drawable != null) {
                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_feedpost_item.getContext(), R.color.text_color2), PorterDuff.Mode.SRC_IN));
                                                    }
                                                }

                                                String votes= holder.tv_like_feedpost_item.getText().toString();
                                                int vot = Integer.parseInt(votes);
                                                vot++;
                                                holder.tv_like_feedpost_item.setText(Integer.toString(vot));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public ViewHolder_Post onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.feedpost_item, parent, false);

                        return new ViewHolder_Post(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();

//        recyclerView.setLayoutManager(mLayoutManager);

        rv_yourPost.setAdapter(firebaseRecyclerAdapter);
        progressbar_yourPosts.setVisibility(View.GONE);
        rv_yourPost.setVisibility(View.VISIBLE);


    }

    private void confirmDelete(String s, String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(YourPosts.this);

        builder.setTitle("Delete Post")
                .setMessage("Are you sure you want to Delete this Post?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(s, url);
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

    private void deletePost(String time, String url) {

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Query query = UserQuestions.orderByChild("time").equalTo(time);
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

                Query query1 = databaseReference.orderByChild("time").equalTo(time);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();

                            Toast.makeText(YourPosts.this, "Your Post has been removed successfully!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(YourPosts.this, "Error", Toast.LENGTH_LONG).show();
            }
        });




    }
}