package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AllUsersSearchActivity extends AppCompatActivity {


    private SearchView sv_SearchUser_allUser;
    private ProgressBar progressbar_allUsers;
    private ImageButton ib_back_allUsers;


    RecyclerView rv_allUsers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String current_uid, school;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, UserQuestions, votesRefrence;
    Boolean checkUpvote = false;
    private FirebaseRecyclerPagingAdapter<ProfileUser, ViewHolder_UsersList> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_search);

        sv_SearchUser_allUser = findViewById(R.id.sv_SearchUser_allUser);
        ib_back_allUsers = findViewById(R.id.ib_back_allUsers);
        progressbar_allUsers = findViewById(R.id.progressbar_allUsers);
        rv_allUsers = findViewById(R.id.rv_allUsers);
        rv_allUsers.setHasFixedSize(true);
        rv_allUsers.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        current_uid = user.getUid();
        documentReference = db.collection("user").document(current_uid);

        ib_back_allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            school = task.getResult().getString("school_str");

                            retriveUsers();

                        }else{
                            Toast.makeText(AllUsersSearchActivity.this, "No Profile Found", Toast.LENGTH_SHORT).show();
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AllUsersSearchActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        sv_SearchUser_allUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });




    }

    private void retriveUsers() {
        databaseReference = database.getReference().child(school).child("user");
        databaseReference.keepSynced(true);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3)
                .build();
        DatabasePagingOptions<ProfileUser> options = new DatabasePagingOptions.Builder<ProfileUser>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference, config, ProfileUser.class)
                .build();

        mAdapter = new FirebaseRecyclerPagingAdapter<ProfileUser, ViewHolder_UsersList>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_UsersList holder, int position, @NonNull ProfileUser model) {
                String batch_str= model.getBranch() + " | " + model.getBatch();

                holder.setUsers(model.getUid(), model.getName(), batch_str, model.getBio(), model.getImgurl());

                holder.tv_name_allUsers_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PublicUserProfileView.class);
                        intent.putExtra("uid", model.getUid());
                        startActivity(intent);
                    }
                });

                holder.iv_allUsers_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PublicUserProfileView.class);
                        intent.putExtra("uid", model.getUid());
                        startActivity(intent);
                    }
                });

                if(current_uid.equals(model.getUid())){
                    holder.b_sendmessage_alluser_item.setVisibility(View.GONE);

                }

                holder.b_sendmessage_alluser_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(AllUsersSearchActivity.this, MessageActivity.class);
                        intent1.putExtra("uid", model.getUid());
                        intent1.putExtra("url", model.getImgurl());
                        intent1.putExtra("name", model.getName());
                        startActivity(intent1);

                    }
                });

            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        progressbar_allUsers.setVisibility(View.VISIBLE);
                        break;

                    case LOADED:
                        // Stop Animation

                        progressbar_allUsers
                                .animate()
                                .alpha(1)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        progressbar_allUsers.setVisibility(View.INVISIBLE);
                                    }
                                });
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        progressbar_allUsers
                                .animate()
                                .alpha(1)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        progressbar_allUsers.setVisibility(View.INVISIBLE);
                                    }
                                });
                        break;

                    case ERROR:
                        retry();
                        break;
                }
            }

            @NonNull
            @Override
            public ViewHolder_UsersList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alluser_item, parent, false);

                return new ViewHolder_UsersList(view);
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                progressbar_allUsers.setVisibility(View.INVISIBLE);
                databaseError.toException().printStackTrace();
            }
        };

        mAdapter.startListening();
        rv_allUsers.setAdapter(mAdapter);
        rv_allUsers.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        progressbar_allUsers.setVisibility(View.INVISIBLE);

    }

    private void filter(String s){
        progressbar_allUsers.setVisibility(View.VISIBLE);
        databaseReference = database.getReference().child(school).child("user");
        databaseReference.keepSynced(true);

        Query reverseQuery = databaseReference.orderByChild("name").startAt(s.toUpperCase()).endAt(s.toUpperCase()+ "~");
        FirebaseRecyclerOptions<ProfileUser> options = new FirebaseRecyclerOptions.Builder<ProfileUser>()
                .setQuery(reverseQuery, ProfileUser.class)
                .build();

        FirebaseRecyclerAdapter<ProfileUser, ViewHolder_UsersList> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ProfileUser, ViewHolder_UsersList>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_UsersList holder, int position, @NonNull ProfileUser model) {
                        String batch_str= model.getBranch() + " | " + model.getBatch();

                        holder.setUsers(model.getUid(), model.getName(), batch_str, model.getBio(), model.getImgurl());

                        holder.tv_name_allUsers_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), PublicUserProfileView.class);
                                intent.putExtra("uid", model.getUid());
                                startActivity(intent);
                            }
                        });

                        holder.iv_allUsers_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), PublicUserProfileView.class);
                                intent.putExtra("uid", model.getUid());
                                startActivity(intent);
                            }
                        });

                        holder.b_sendmessage_alluser_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(AllUsersSearchActivity.this, MessageActivity.class);
                                intent1.putExtra("uid", model.getUid());
                                intent1.putExtra("url", model.getImgurl());
                                intent1.putExtra("name", model.getName());
                                startActivity(intent1);

                            }
                        });
                    }


                    @NonNull
                    @Override
                    public ViewHolder_UsersList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.alluser_item, parent, false);

                        return new ViewHolder_UsersList(view);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return position;
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                };

        firebaseRecyclerAdapter.startListening();
        rv_allUsers.setAdapter(firebaseRecyclerAdapter);
        progressbar_allUsers.setVisibility(View.INVISIBLE);
    }
}