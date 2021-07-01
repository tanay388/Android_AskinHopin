package com.askinhopin.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Fragment3 extends Fragment implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    ImageView iv_dp_thumbnail_f3;
    ImageButton ib_menu_f3;
    ProgressBar progressbar_f3;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, postRefrence;
    RecyclerView recyclerView;
    questionMember questionMembers;

    String school_str,current_imgurl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, container, false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.rv_f3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        iv_dp_thumbnail_f3 = getActivity().findViewById(R.id.iv_dp_thumbnail_f3);
        ib_menu_f3 = getActivity().findViewById(R.id.ib_menu_f3);
        progressbar_f3 = getActivity().findViewById(R.id.progressbar_f3);


        ib_menu_f3.setOnClickListener(this);
        iv_dp_thumbnail_f3.setOnClickListener(this);


        reference = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    school_str = task.getResult().getString("school_str");
                    current_imgurl = task.getResult().getString("imageurl");
                    Picasso.get().load(current_imgurl).fit().centerCrop().into(iv_dp_thumbnail_f3);

                    databaseReference = database.getReference().child(school_str).child("user").
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notification");

                    Query reverseQuery = databaseReference.orderByChild("rev_timemillies");

                    FirebaseRecyclerOptions<NotificationClass> options = new FirebaseRecyclerOptions.Builder<NotificationClass>()
                            .setQuery(reverseQuery, NotificationClass.class)
                            .build();
                    FirebaseRecyclerAdapter<NotificationClass, ViewHolder_Notification> firebaseRecyclerAdapter =
                            new FirebaseRecyclerAdapter<NotificationClass, ViewHolder_Notification>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ViewHolder_Notification holder, int position, @NonNull NotificationClass model) {


                                    holder.setNotification(model.getType(), model.getPostid(), model.getRecivinguid(), model.getSentuid(),
                                            model.getPosttype(), model.getContent(),model.getTime(), model.getKey(), model.getUnread(), model.getTitle(),
                                            model.getDiscription(), model.getCategory(), model.getPostTime());

                                    final String type_strResult = getItem(position).getType();
                                    final String postType_strResult = getItem(position).getPosttype();
                                    final String uid_strResult = getItem(position).getRecivinguid();
                                    final String postid_strResult = getItem(position).getPostid();
                                    final String key_strResult = getItem(position).getKey();
                                    final String content_strResult = getItem(position).getContent();
                                    final String time_strResult = getItem(position).getTime();
                                    final boolean unread_strResult = getItem(position).getUnread();

                                    if (unread_strResult) {
                                        Drawable buttonDrawable = holder.rl_notification_parent.getBackground();
                                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);

                                        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.text_color2_light));
                                        holder.rl_notification_parent.setBackground(buttonDrawable);
                                    }else{
                                        Drawable buttonDrawable = holder.rl_notification_parent.getBackground();
                                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);

                                        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.white));
                                    }


                                    holder.rl_notification_parent.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            progressbar_f3.setVisibility(View.VISIBLE);
                                            Toast.makeText(getActivity(), ""+ postType_strResult, Toast.LENGTH_SHORT).show();

                                            if(postType_strResult.equals("Answer_added")){

                                                postRefrence = database.getReference().child("User Question").child(uid_strResult).child(postid_strResult);
                                                final String name_strResult = " ";
                                                final String url_strResult = " ";
                                                final String title_strResult = model.getTitle();
                                                final String discription_strResult = model.getDiscription();
                                                final String category_strResult = model.getCategory();


                                                databaseReference.child(key_strResult).child("unread").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent intent = new Intent(getActivity(), ReplyActivity.class);
                                                        intent.putExtra("Uid", uid_strResult);
                                                        intent.putExtra("Name", name_strResult);
                                                        intent.putExtra("Url", url_strResult);
                                                        intent.putExtra("Key", postid_strResult);
                                                        intent.putExtra("Title", title_strResult);
                                                        intent.putExtra("Discription", discription_strResult);
                                                        intent.putExtra("Time", model.getPostTime());
                                                        intent.putExtra("Category", category_strResult);
                                                        intent.putExtra("School", school_str);
                                                        startActivity(intent);

                                                        progressbar_f3.setVisibility(View.INVISIBLE);
                                                    }
                                                });


                                            }else if(postType_strResult.equals("Answer_liked")){
                                                postRefrence = database.getReference().child("User Question").child(uid_strResult).child(postid_strResult);
                                                final String name_strResult = " ";
                                                final String url_strResult = " ";
                                                final String title_strResult = model.getTitle();
                                                final String discription_strResult = model.getDiscription();
                                                final String category_strResult = model.getCategory();


                                                databaseReference.child(key_strResult).child("unread").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent intent = new Intent(getActivity(), ReplyActivity.class);
                                                        intent.putExtra("Uid", uid_strResult);
                                                        intent.putExtra("Name", name_strResult);
                                                        intent.putExtra("Url", url_strResult);
                                                        intent.putExtra("Key", postid_strResult);
                                                        intent.putExtra("Title", title_strResult);
                                                        intent.putExtra("Discription", discription_strResult);
                                                        intent.putExtra("Time", model.getPostTime());
                                                        intent.putExtra("Category", category_strResult);
                                                        intent.putExtra("School", school_str);
                                                        startActivity(intent);

                                                        progressbar_f3.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            }


//

                                        }
                                    });

                                }

                                @NonNull
                                @Override
                                public ViewHolder_Notification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.notification_item, parent, false);

                                    return new ViewHolder_Notification(view);
                                }
                            };

                    firebaseRecyclerAdapter.startListening();

//                            recyclerView.setLayoutManager(mLayoutManager);

                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    progressbar_f3.setVisibility(View.INVISIBLE);

                }else{
                    Toast.makeText(getActivity(), "No Profile Exists", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_menu_f3 :
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
                break;

            case R.id.iv_dp_thumbnail_f3 :
                BottomSheet_dp_f2 bottomSheetDpF2 = new BottomSheet_dp_f2();
                bottomSheetDpF2.show(getFragmentManager(), "bottom");
                break;
        }
    }
}
