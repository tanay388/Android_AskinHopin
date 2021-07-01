package com.askinhopin.app;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewHolder_Notification extends RecyclerView.ViewHolder{

    DocumentReference reference, reference1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    int likes;
    String school_str;

    ImageView iv_sender_dp_notification;
    TextView tv_content_notification, tv_time_notification;
    RelativeLayout rl_notification_parent;
    public ViewHolder_Notification(@NonNull View itemView) {
        super(itemView);
    }

    public void setNotification(String type, String postid,String recivinguid,String sentuid,String posttype, String content,
                                String time,String key, boolean unread, String Title, String discription, String category,
                                String postTime){
        tv_content_notification = itemView.findViewById(R.id.tv_content_notification);
        tv_time_notification = itemView.findViewById(R.id.tv_time_notification);
        rl_notification_parent = itemView.findViewById(R.id.rl_notification_parent);
        iv_sender_dp_notification = itemView.findViewById(R.id.iv_sender_dp_notification);

        reference = db.collection("user").document(sentuid);

        //Answer User Sender
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String imageurlResult = task.getResult().getString("imageurl");

                            if(imageurlResult != null)
                                Picasso.get().load(imageurlResult).fit().centerCrop().into(iv_sender_dp_notification);


                        }else{


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        tv_content_notification.setText(content);
        tv_time_notification.setText(time);


    }
}
