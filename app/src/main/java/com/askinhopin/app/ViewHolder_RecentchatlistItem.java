package com.askinhopin.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewHolder_RecentchatlistItem extends RecyclerView.ViewHolder {

    ImageView iv_dp_thumbnail_recentchatlist_item;
    TextView tv_name_recentuserlist_item, tv_lastmessage_recentuserlist_item, tv_time_recentuserlist_item;
    LinearLayout ll_recentchatlist_item;

    public ViewHolder_RecentchatlistItem(@NonNull View itemView) {
        super(itemView);
    }


    public void setRecentChat(String name, String url, long time, String lastmessage){
        iv_dp_thumbnail_recentchatlist_item = itemView.findViewById(R.id.iv_dp_thumbnail_recentchatlist_item);
        tv_name_recentuserlist_item = itemView.findViewById(R.id.tv_name_recentuserlist_item);
        ll_recentchatlist_item = itemView.findViewById(R.id.ll_recentchatlist_item);
        tv_lastmessage_recentuserlist_item = itemView.findViewById(R.id.tv_lastmessage_recentuserlist_item);
        tv_time_recentuserlist_item = itemView.findViewById(R.id.tv_time_recentuserlist_item);


        tv_name_recentuserlist_item.setText(name);
        tv_lastmessage_recentuserlist_item.setText(lastmessage);
        tv_time_recentuserlist_item.setText(getDate(time, "DD/MM/YYYY HH:MM:ss"));
        Picasso.get().load(url).fit().centerCrop().into(iv_dp_thumbnail_recentchatlist_item);

    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
