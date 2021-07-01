package com.askinhopin.app;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder_Question extends RecyclerView.ViewHolder {

    ImageView iv_ques_item;
    TextView tv_name_ques_item, tv_time_ques_item, tv_category_ques_item, tv_title_ques_item,
            tv_disc_ques_item, tv_reply_ques_item, tv_answerno_feedpost_item;
    TextView tv_delete_yourques_item;
    TextView tv_reply_yourques_item, tv_title_yourques_item;


    public ViewHolder_Question(@NonNull View itemView) {
        super(itemView);
    }

    public void setitem(FragmentActivity activity, String name, String uid, String url, String key, String title, String discription, String time,
                        String category, String tags){
        iv_ques_item = itemView.findViewById(R.id.iv_ques_item);
        tv_name_ques_item = itemView.findViewById(R.id.tv_name_ques_item);
        tv_time_ques_item = itemView.findViewById(R.id.tv_time_ques_item);
        tv_category_ques_item = itemView.findViewById(R.id.tv_category_ques_item);
        tv_title_ques_item = itemView.findViewById(R.id.tv_title_ques_item);
        tv_disc_ques_item = itemView.findViewById(R.id.tv_disc_ques_item);
        tv_reply_ques_item = itemView.findViewById(R.id.tv_reply_ques_item);
        tv_answerno_feedpost_item = itemView.findViewById(R.id.tv_answerno_feedpost_item);

        if(url != null){
            Picasso.get().load(url).fit().centerCrop().into(iv_ques_item);
        }
        tv_name_ques_item.setText(name);
        tv_time_ques_item.setText(time);
        tv_category_ques_item.setText(category);
        tv_title_ques_item.setText(title);
        tv_disc_ques_item.setText(discription);

    }

    public void setitemdelete(Application activity, String name, String uid, String url, String key, String title, String discription, String time,
                              String category, String tags ){

        ImageView iv_yourques_item = itemView.findViewById(R.id.iv_yourques_item);
        TextView tv_name_yourques_item = itemView.findViewById(R.id.tv_name_yourques_item);
        TextView tv_time_yourques_item = itemView.findViewById(R.id.tv_time_yourques_item);
        TextView tv_category_yourques_item = itemView.findViewById(R.id.tv_category_yourques_item);
        tv_title_yourques_item = itemView.findViewById(R.id.tv_title_yourques_item);
        TextView tv_disc_yourques_item = itemView.findViewById(R.id.tv_disc_yourques_item);
        tv_reply_yourques_item = itemView.findViewById(R.id.tv_reply_yourques_item);
        tv_delete_yourques_item = itemView.findViewById(R.id.tv_delete_yourques_item);

        if(url != null){
            Picasso.get().load(url).fit().centerCrop().into(iv_yourques_item);
        }
        tv_name_yourques_item.setText(name);
        tv_time_yourques_item.setText(time);
        tv_category_yourques_item.setText(category);
        tv_title_yourques_item.setText(title);
        tv_disc_yourques_item.setText(discription);

    }
}
