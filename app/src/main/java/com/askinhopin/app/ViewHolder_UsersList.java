package com.askinhopin.app;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewHolder_UsersList extends RecyclerView.ViewHolder{

    DocumentReference reference, reference1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    int likes;
    String school_str;

    TextView tv_name_allUsers_item, tv_batch_allUsers_item, tv_bio_allUsers_item;
    LinearLayout ll_parent_allUsers_item;
    ImageView iv_allUsers_item;
    Button b_sendmessage_alluser_item;

    public ViewHolder_UsersList(@NonNull  View itemView) {
        super(itemView);


    }

    public void setUsers(String uid, String name, String batch, String bio, String url){
        tv_name_allUsers_item = itemView.findViewById(R.id.tv_name_allUsers_item);
        tv_batch_allUsers_item = itemView.findViewById(R.id.tv_batch_allUsers_item);
        tv_bio_allUsers_item = itemView.findViewById(R.id.tv_bio_allUsers_item);
        ll_parent_allUsers_item = itemView.findViewById(R.id.ll_parent_allUsers_item);
        iv_allUsers_item = itemView.findViewById(R.id.iv_allUsers_item);
        b_sendmessage_alluser_item = itemView.findViewById(R.id.b_sendmessage_alluser_item);


        tv_name_allUsers_item.setText(name);
        tv_batch_allUsers_item.setText(batch);
        tv_bio_allUsers_item.setText(bio);

        if(url!= null){
            Picasso.get().load(url).fit().centerCrop().into(iv_allUsers_item);
        }



    }
}
