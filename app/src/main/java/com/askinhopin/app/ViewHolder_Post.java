package com.askinhopin.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewHolder_Post extends RecyclerView.ViewHolder {
    DocumentReference reference, reference1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    String school_str, currid;


    ImageView iv_dp_thumbnail_feedpost_item, iv_post_feedpost_item;
    TextView tv_disc_feedpost_item, tv_name_feedpost_item, tv_time_feedpost_item, tv_like_feedpost_item, tv_comment_feedpost_item, tv_delete_feedpost_item;
//    Button  tv_delete_feedpost_item;


    public ViewHolder_Post(@NonNull View itemView) {
        super(itemView);
    }

    public void setPostFeed(FragmentActivity activity, String key, String uid, String time, String postUrl, String content){
        reference = db.collection("user").document(uid);

        iv_dp_thumbnail_feedpost_item =itemView.findViewById(R.id.iv_dp_thumbnail_feedpost_item);
        tv_disc_feedpost_item = itemView.findViewById(R.id.tv_disc_feedpost_item);
        tv_name_feedpost_item = itemView.findViewById(R.id.tv_name_feedpost_item);
        tv_time_feedpost_item = itemView.findViewById(R.id.tv_time_feedpost_item);
        iv_post_feedpost_item = itemView.findViewById(R.id.iv_post_feedpost_item);
        tv_like_feedpost_item = itemView.findViewById(R.id.tv_like_feedpost_item);
        tv_comment_feedpost_item = itemView.findViewById(R.id.tv_comment_feedpost_item);
        tv_delete_feedpost_item = itemView.findViewById(R.id.tv_delete_feedpost_item);


        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");
                            String nameResult = task.getResult().getString("name_str");

                            if(imageurlResult != null)
                                Picasso.get().load(imageurlResult).into(iv_dp_thumbnail_feedpost_item);
                            if(postUrl != null)
                                Picasso.get().load(postUrl).into(iv_post_feedpost_item);
                            tv_name_feedpost_item.setText(nameResult);
                            tv_disc_feedpost_item.setText(content);
                            tv_time_feedpost_item.setText(time);


                        }else{


                        }
                    }
                });






    }

    public void setPostFeedApplication(Application activity, String key, String uid, String time, String postUrl, String content){
        reference = db.collection("user").document(uid);

        iv_dp_thumbnail_feedpost_item =itemView.findViewById(R.id.iv_dp_thumbnail_feedpost_item);
        tv_disc_feedpost_item = itemView.findViewById(R.id.tv_disc_feedpost_item);
        tv_name_feedpost_item = itemView.findViewById(R.id.tv_name_feedpost_item);
        tv_time_feedpost_item = itemView.findViewById(R.id.tv_time_feedpost_item);
        iv_post_feedpost_item = itemView.findViewById(R.id.iv_post_feedpost_item);
        tv_like_feedpost_item = itemView.findViewById(R.id.tv_like_feedpost_item);
        tv_comment_feedpost_item = itemView.findViewById(R.id.tv_comment_feedpost_item);
        tv_delete_feedpost_item = itemView.findViewById(R.id.tv_delete_feedpost_item);


        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");
                            String nameResult = task.getResult().getString("name_str");

                            if(imageurlResult != null)
                                Picasso.get().load(imageurlResult).into(iv_dp_thumbnail_feedpost_item);
                            if(postUrl != null)
                                Picasso.get().load(postUrl).into(iv_post_feedpost_item);
                            tv_name_feedpost_item.setText(nameResult);
                            tv_disc_feedpost_item.setText(content);
                            tv_time_feedpost_item.setText(time);


                        }else{


                        }
                    }
                });






    }

    public void upvotechecker(String postKey, String school) {

        databaseReference = database.getReference().child(school).child("votes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        tv_like_feedpost_item = itemView.findViewById(R.id.tv_like_feedpost_item);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(postKey)){


                    if(snapshot.child(postKey).hasChild(currentuid)){
                        setTextViewDrawableColor();
                        int votesCount = (int)snapshot.child(postKey).getChildrenCount();
                        tv_like_feedpost_item.setText(Integer.toString(votesCount));
                    }else{
                        int votesCount = (int)snapshot.child(postKey).getChildrenCount();
                        tv_like_feedpost_item.setText(Integer.toString(votesCount));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setTextViewDrawableColor() {
        for (Drawable drawable : tv_like_feedpost_item.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setTint(R.color.text_color2);
                tv_like_feedpost_item.setTextColor(R.color.text_color2);
            }
        }
    }
}
