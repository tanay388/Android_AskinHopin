package com.askinhopin.app;

import android.app.Application;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewHolder_Message extends RecyclerView.ViewHolder {

    TextView tv_chat_reciever_item, tv_time_chat_item, tv_chat_sender_item, tv_time_chat_item2;
    LinearLayout ll_parent_chat_item, ll_reciever_item, ll_sender_item;
    public ViewHolder_Message(@NonNull View itemView) {
        super(itemView);
    }


    public void setChatMessage(Application application, String senderuid, String recieveruid, String time, String date, String message, String type){
        tv_chat_reciever_item = itemView.findViewById(R.id.tv_chat_reciever_item);
        tv_time_chat_item = itemView.findViewById(R.id.tv_time_chat_item);
        ll_parent_chat_item = itemView.findViewById(R.id.ll_parent_chat_item);
        ll_reciever_item = itemView.findViewById(R.id.ll_reciever_item);
        ll_sender_item = itemView.findViewById(R.id.ll_sender_item);
        tv_chat_sender_item = itemView.findViewById(R.id.tv_chat_sender_item);
        tv_time_chat_item2 = itemView.findViewById(R.id.tv_time_chat_item2);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        if(currentUserid.equals(senderuid)){
            ll_reciever_item.setVisibility(View.GONE);
            tv_chat_sender_item.setText(message);
            tv_time_chat_item2.setText(time);

        }else if(currentUserid.equals(recieveruid)){
            ll_sender_item.setVisibility(View.GONE);
            tv_chat_reciever_item.setText(message);
            tv_time_chat_item.setText(time);
        }


    }
}
