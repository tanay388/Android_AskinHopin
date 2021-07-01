package com.askinhopin.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class Fragment2 extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton_f2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    ImageView iv_dp_thumbnail_f2;
    ImageButton ib_menu_f2;
    ProgressBar progressbar_f2;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    String school_str;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        iv_dp_thumbnail_f2 = getActivity().findViewById(R.id.iv_dp_thumbnail_f2);
        floatingActionButton_f2 = getActivity().findViewById(R.id.floatingActionButton_f2);
        ib_menu_f2 = getActivity().findViewById(R.id.ib_menu_f2);
        progressbar_f2 = getActivity().findViewById(R.id.progressbar_f2);


        floatingActionButton_f2.setOnClickListener(this);
        ib_menu_f2.setOnClickListener(this);
        iv_dp_thumbnail_f2.setOnClickListener(this);

        reference = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            school_str = task.getResult().getString("school_str");

                            databaseReference = database.getReference().child(school_str).child("All Questions");

                            PagedList.Config config = new PagedList.Config.Builder()
                                    .setEnablePlaceholders(false)
                                    .setPrefetchDistance(2)
                                    .setPageSize(2)
                                    .build();
                            Query reverseQuery = databaseReference.orderByChild("rev_timemilies");

                            FirebaseRecyclerOptions<questionMember> options = new FirebaseRecyclerOptions.Builder<questionMember>()
                                    .setQuery(reverseQuery, questionMember.class)
                                    .build();
                            FirebaseRecyclerAdapter<questionMember, ViewHolder_Question> firebaseRecyclerAdapter =
                                    new FirebaseRecyclerAdapter<questionMember, ViewHolder_Question>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull ViewHolder_Question holder, int position, @NonNull questionMember model) {


                                            holder.setitem(getActivity(), model.getName(), model.getUid(), model.getUrl(), model.getKey(),
                                                    model.getTitle(), model.getDiscription(),model.getTime(), model.getCategory(), model.getTags());

                                            final String name_strResult = getItem(position).getName();
                                            final String uid_strResult = getItem(position).getUid();
                                            final String url_strResult = getItem(position).getUrl();
                                            final String key_strResult = getItem(position).getKey();
                                            final String title_strResult = getItem(position).getTitle();
                                            final String discription_strResult = getItem(position).getDiscription();
                                            final String time_strResult = getItem(position).getTime();
                                            final String category_strResult = getItem(position).getCategory();

                                            final DatabaseReference refAns = databaseReference.child(key_strResult).child("Answer");
                                            refAns.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int votesCount = (int)snapshot.getChildrenCount();
                                                    holder.tv_answerno_feedpost_item.setText(Integer.toString(votesCount));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            holder.tv_reply_ques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), ReplyActivity.class);
                                                    intent.putExtra("Uid", uid_strResult);
                                                    intent.putExtra("Name", name_strResult);
                                                    intent.putExtra("Url", url_strResult);
                                                    intent.putExtra("Key", key_strResult);
                                                    intent.putExtra("Title", title_strResult);
                                                    intent.putExtra("Discription", discription_strResult);
                                                    intent.putExtra("Time", time_strResult);
                                                    intent.putExtra("Category", category_strResult);
                                                    intent.putExtra("School", school_str);
                                                    startActivity(intent);

                                                }
                                            });

                                            holder.tv_title_ques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), ReplyActivity.class);
                                                    intent.putExtra("Uid", uid_strResult);
                                                    intent.putExtra("Name", name_strResult);
                                                    intent.putExtra("Url", url_strResult);
                                                    intent.putExtra("Key", key_strResult);
                                                    intent.putExtra("Title", title_strResult);
                                                    intent.putExtra("Discription", discription_strResult);
                                                    intent.putExtra("Time", time_strResult);
                                                    intent.putExtra("Category", category_strResult);
                                                    intent.putExtra("School", school_str);
                                                    startActivity(intent);
                                                }
                                            });
                                            holder.tv_name_ques_item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), PublicUserProfileView.class);
                                                    intent.putExtra("uid", uid_strResult);
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @NonNull
                                        @Override
                                        public ViewHolder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext())
                                                    .inflate(R.layout.question_item, parent, false);

                                            return new ViewHolder_Question(view);
                                        }
                                    };

                            firebaseRecyclerAdapter.startListening();

//                            recyclerView.setLayoutManager(mLayoutManager);

                            recyclerView.setAdapter(firebaseRecyclerAdapter);
                            progressbar_f2.setVisibility(View.INVISIBLE);

                        }else{
                            Toast.makeText(getActivity(), "No Profile Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floatingActionButton_f2 :
                Intent intent = new Intent(getActivity(), AskQuestion.class);
                startActivity(intent);
                break;

            case R.id.ib_menu_f2 :
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
                break;

            case R.id.iv_dp_thumbnail_f2 :
                BottomSheet_dp_f2 bottomSheetDpF2 = new BottomSheet_dp_f2();
                bottomSheetDpF2.show(getFragmentManager(), "bottom");
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");

                            if(!TextUtils.isEmpty(imageurlResult))
                                Picasso.get().load(imageurlResult).fit().centerCrop().into(iv_dp_thumbnail_f2);


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getSchool_str(){
        final String[] schoolResult = new String[1];
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            schoolResult[0] = task.getResult().getString("school_str");


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });

        return schoolResult[0];
    }
}
