package com.askinhopin.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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


public class ViewHolder_Answer extends RecyclerView.ViewHolder implements View.OnClickListener {

    DocumentReference reference, reference1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    int likes;
    String school_str;

    ImageView iv_ans_item;
    TextView tv_name_ans_item, tv_time_ans_item, tv_content_ans_item, tv_like_answer, tv_delete_ans_item, tv_like_answer_fun;
    public ViewHolder_Answer(@NonNull View itemView) {
        super(itemView);
    }

    public void setAnswer(Application application, String uid, String time, String answer){
        iv_ans_item = itemView.findViewById(R.id.iv_ans_item);
        tv_name_ans_item = itemView.findViewById(R.id.tv_name_ans_item);
        tv_time_ans_item = itemView.findViewById(R.id.tv_time_ans_item);
        tv_content_ans_item = itemView.findViewById(R.id.tv_content_ans_item);
        tv_like_answer = itemView.findViewById(R.id.tv_like_answer);
        tv_delete_ans_item = itemView.findViewById(R.id.tv_delete_answer);

        reference = db.collection("user").document(uid);

        //Answer User Refrence
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");
                            String nameResult = task.getResult().getString("name_str");

                            tv_name_ans_item.setText(nameResult);
                            tv_time_ans_item.setText(time);
                            tv_content_ans_item.setText(answer);
                            if(imageurlResult != null)
                                Picasso.get().load(imageurlResult).fit().centerCrop().into(iv_ans_item);


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




    }

    public void upvotechecker(String postKey, String school) {

        databaseReference = database.getReference().child(school).child("votes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        tv_like_answer = itemView.findViewById(R.id.tv_like_answer);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(postKey)){


                    if(snapshot.child(postKey).hasChild(currentuid)){
                        setTextViewDrawableColor();
                        int votesCount = (int)snapshot.child(postKey).getChildrenCount();
                        tv_like_answer.setText(Integer.toString(votesCount));
                    }else{
                        int votesCount = (int)snapshot.child(postKey).getChildrenCount();
                        tv_like_answer.setText(Integer.toString(votesCount));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTextViewDrawableColor() {
        for (Drawable drawable : tv_like_answer.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(tv_like_answer.getContext(), R.color.text_color2), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
