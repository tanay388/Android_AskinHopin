package com.askinhopin.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fragment1 extends Fragment implements View.OnClickListener {
    Button b_Addpost_f1;
    TextView tv_name_currentuser_f1;
    ImageView iv_dp_thumbnail_f1, iv_message_f1;
    ImageButton ib_menu_f1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String current_uid, school;
    RecyclerView recyclerView;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,  UserQuestions, votesRefrence, Notificationdb, rootref1;
    ProgressBar progressbar_f1;
    Boolean checkUpvote = false;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerPagingAdapter<FeedPostClass, ViewHolder_Post> mAdapter;
    LinearLayout ll_profiledetail_f1;
    NotificationClass notificationClass;


    long count_notification_unread, message_count=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{

            current_uid = user.getUid();
            documentReference = db.collection("user").document(current_uid);



            b_Addpost_f1 = getActivity().findViewById(R.id.b_Addpost_f1);
            tv_name_currentuser_f1 = getActivity().findViewById(R.id.tv_name_currentuser_f1);
            iv_dp_thumbnail_f1 = getActivity().findViewById(R.id.iv_dp_thumbnail_f1);
            progressbar_f1 = getActivity().findViewById(R.id.progressbar_f1);
            recyclerView = getActivity().findViewById(R.id.rv_f1);
            ib_menu_f1 = getActivity().findViewById(R.id.ib_menu_f1);
            iv_message_f1 = getActivity().findViewById(R.id.iv_message_f1);
            ll_profiledetail_f1 = getActivity().findViewById(R.id.ll_profiledetail_f1);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            notificationClass = new NotificationClass();



            b_Addpost_f1.setOnClickListener(this);
            ib_menu_f1.setOnClickListener(this);
            iv_message_f1.setOnClickListener(this);
            rootref1 = database.getReference().child("Conversation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            rootref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(!dataSnapshot.child("read").getValue(Boolean.class)){
                            message_count++;

                            if(message_count>0){
                                iv_message_f1.setBackground(getResources().getDrawable(R.drawable.ic_message_unread));
                            }else{
                                iv_message_f1.setBackground(getResources().getDrawable(R.drawable.ic_message_svgrepo_com));

                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                school = task.getResult().getString("school_str");
                                String nameResult = task.getResult().getString("name_str");
                                String urlResult = task.getResult().getString("imageurl");
                                tv_name_currentuser_f1.setText(nameResult);

                                if(urlResult != null){
                                    Picasso.get().load(urlResult).fit().centerCrop().into(iv_dp_thumbnail_f1);
                                }
                                retrivePost();

                            }else{
                                Toast.makeText(getActivity(), "No Profile Found", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), CreateProfile.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_Addpost_f1 :
                BottomSheet_AddPost bottomSheet_addPost = new BottomSheet_AddPost();
                bottomSheet_addPost.show(getFragmentManager(), "bottom_add_post");
                break;
            case R.id.ib_menu_f1 :
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
                break;
            case R.id.iv_message_f1 :
                Intent intent = new Intent(getActivity(), RecentChatList.class);
                startActivity(intent);
                break;


        }
    }

    @Override
    public void onStart() {

        super.onStart();


    }

    private void retrivePost() {
//        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);

        databaseReference = database.getReference().child(school).child("All Posts");
        UserQuestions = database.getReference().child(school).child("User Post").child(current_uid);
        votesRefrence = FirebaseDatabase.getInstance().getReference().child(school).child("votes");
        Notificationdb = database.getReference().child(school).child("user");

        databaseReference.keepSynced(true);
        votesRefrence.keepSynced(true);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3)
                .build();
        DatabasePagingOptions<FeedPostClass> options = new DatabasePagingOptions.Builder<FeedPostClass>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference, config, FeedPostClass.class)
                .build();


        mAdapter = new FirebaseRecyclerPagingAdapter<FeedPostClass, ViewHolder_Post>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_Post holder, int position, @NonNull FeedPostClass model) {
                        holder.setPostFeed(getActivity(), model.getKey(), model.getUid(), model.getTime(), model.getUrl(),
                                model.getDiscription());
                        String uid_strResult = model.getUid();
                        String time = model.getTime();
                        String ansKey = getRef(position).getKey();
                        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                        holder.tv_name_feedpost_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), PublicUserProfileView.class);
                                intent.putExtra("uid", uid_strResult);
                                startActivity(intent);
                            }
                        });

                        if(uid_strResult.equals(current_uid)){
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

                                                Calendar cdate = Calendar.getInstance();
                                                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String savedate = currentdate.format(cdate.getTime());

                                                Calendar ctime = Calendar.getInstance();
                                                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                                                final String savetime = currenttime.format(ctime.getTime());

                                                String finaltime = savedate + " | " + savetime;

                                                notificationClass.setType("Like");
                                                notificationClass.setPosttype("Post_liked");
                                                notificationClass.setUnread(true);
                                                notificationClass.setPostid(ansKey);
                                                notificationClass.setSentuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                notificationClass.setRecivinguid(uid_strResult);
                                                notificationClass.setTime(finaltime);
                                                notificationClass.setPostTime(time);
                                                notificationClass.setCategory("");
                                                notificationClass.setTitle("");
                                                notificationClass.setDiscription("");
                                                notificationClass.setRev_timemillies((-1)*ctime.getTimeInMillis());


                                                String smallPostDiscription = model.getDiscription().substring(0, Math.min(40, model.getDiscription().length()));
                                                notificationClass.setContent(tv_name_currentuser_f1.getText().toString() + " and " +(vot-1) + " others liked your Post " + " ' " + smallPostDiscription + " '.");
                                                notificationClass.setKey(ansKey);
                                                Notificationdb.child(uid_strResult).child("notification").child(ansKey).setValue(notificationClass);
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

                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        switch (state) {
                            case LOADING_INITIAL:
                            case LOADING_MORE:
                                // Do your loading animation
                                progressbar_f1.setVisibility(View.VISIBLE);
                                break;

                            case LOADED:
                                // Stop Animation

                                progressbar_f1
                                        .animate()
                                        .alpha(1)
                                        .setDuration(1000)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                progressbar_f1.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                break;

                            case FINISHED:
                                //Reached end of Data set
                                progressbar_f1
                                        .animate()
                                        .alpha(1)
                                        .setDuration(1000)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                progressbar_f1.setVisibility(View.INVISIBLE);
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
                    public ViewHolder_Post onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.feedpost_item, parent, false);

                        return new ViewHolder_Post(view);
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
                        progressbar_f1.setVisibility(View.INVISIBLE);
                        databaseError.toException().printStackTrace();
                    }
                };

//        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
        progressbar_f1.setVisibility(View.INVISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dy = 0;
            Handler handler = new Handler();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final boolean isOnTop = linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                if ( newState == 1)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dy <= 0) {
                                ll_profiledetail_f1.animate()
                                        .translationY(0)
                                        .alpha(1)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);

                                            }

                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                ll_profiledetail_f1.setVisibility(View.VISIBLE);
                                                ll_profiledetail_f1.setBackgroundColor(getResources().getColor(R.color.white));
                                            }
                                        });
                            }
                        }
                    }, 20);
                else if (dy > 0)
                    ll_profiledetail_f1.animate()
                        .translationY(-1 * ll_profiledetail_f1.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ll_profiledetail_f1.setVisibility(View.INVISIBLE);
                                ll_profiledetail_f1.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        });
//                    KLog.e("gone filter");

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                this.dy = dy;
            }
        });


    }

    private void confirmDelete(String s, String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

                            Toast.makeText(getActivity(), "Your Post has been removed successfully!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        });




    }

    void unused(){

//        FirebaseRecyclerAdapter<FeedPostClass, ViewHolder_Post> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<FeedPostClass, ViewHolder_Post>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull ViewHolder_Post holder, int position, @NonNull FeedPostClass model) {
//                        holder.setPostFeed(getActivity(), model.getKey(), model.getUid(), model.getTime(), model.getUrl(),
//                                model.getDiscription());
//                        String uid_strResult = getItem(position).getUid();
//                        String time = getItem(position).getTime();
//                        String ansKey = getRef(position).getKey();
//                        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//                        holder.tv_name_feedpost_item.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getActivity(), PublicUserProfileView.class);
//                                intent.putExtra("uid", uid_strResult);
//                                startActivity(intent);
//                            }
//                        });
//
//                        if(uid_strResult.equals(current_uid)){
//                            holder.tv_delete_feedpost_item.setVisibility(View.VISIBLE);
//                        }
//
//                        holder.tv_delete_feedpost_item.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                confirmDelete(time, model.getUrl());
//                            }
//                        });
//
//                        holder.upvotechecker(ansKey, school);
//
//                        holder.tv_like_feedpost_item.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                checkUpvote = true;
//                                votesRefrence.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if(checkUpvote==true){
//                                            if(snapshot.child(ansKey).hasChild(currUid)){
//                                                votesRefrence.child(ansKey).child(currUid).removeValue();
//                                                holder.tv_like_feedpost_item.setTextColor(getResources().getColor(R.color.text_color));
//                                                for (Drawable drawable : holder.tv_like_feedpost_item.getCompoundDrawables()) {
//                                                    if (drawable != null) {
//                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_feedpost_item.getContext(), R.color.gray), PorterDuff.Mode.SRC_IN));
//                                                    }
//                                                }
//
//                                                String votes= holder.tv_like_feedpost_item.getText().toString();
//                                                int vot = Integer.parseInt(votes);
//                                                vot--;
//                                                holder.tv_like_feedpost_item.setText(Integer.toString(vot));
//                                                checkUpvote = false;
//                                            }else{
//                                                votesRefrence.child(ansKey).child(currUid).setValue(true);
//                                                checkUpvote = false;
//                                                holder.tv_like_feedpost_item.setTextColor(getResources().getColor(R.color.text_color2));
//                                                for (Drawable drawable : holder.tv_like_feedpost_item.getCompoundDrawables()) {
//                                                    if (drawable != null) {
//                                                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(holder.tv_like_feedpost_item.getContext(), R.color.text_color2), PorterDuff.Mode.SRC_IN));
//                                                    }
//                                                }
//
//                                                String votes= holder.tv_like_feedpost_item.getText().toString();
//                                                int vot = Integer.parseInt(votes);
//                                                vot++;
//                                                holder.tv_like_feedpost_item.setText(Integer.toString(vot));
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//                            }
//                        });
//
//
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ViewHolder_Post onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.feedpost_item, parent, false);
//
//                        return new ViewHolder_Post(view);
//                    }
//
//                    @Override
//                    public int getItemViewType(int position) {
//                        return position;
//                    }
//
//                    @Override
//                    public long getItemId(int position) {
//                        return position;
//                    }
//                };
    }
}
