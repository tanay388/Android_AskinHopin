package com.askinhopin.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_AddPost extends BottomSheetDialogFragment {
    TextView feeds_bs_addpost, Blogs_bs_addpost, news_bs_addpost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_addpost, null);
        feeds_bs_addpost = view.findViewById(R.id.feeds_bs_addpost);
        Blogs_bs_addpost = view.findViewById(R.id.Blogs_bs_addpost);
        news_bs_addpost = view.findViewById(R.id.news_bs_addpost);

        feeds_bs_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("role", "feed");
                startActivity(intent);
            }
        });

        news_bs_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Only Admin Can add a News", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), PostActivity.class);
//                intent.putExtra("role", "news");
//                startActivity(intent);
            }
        });

        Blogs_bs_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), PostActivity.class);
//                intent.putExtra("role", "blogs");
//                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
