package com.askinhopin.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_dp_f2 extends BottomSheetDialogFragment {

    CardView cv_history_bottomsheet_f2, cv_historyPost_bottomsheet_f2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_f2, null);

        cv_history_bottomsheet_f2 = view.findViewById(R.id.cv_history_bottomsheet_f2);
        cv_historyPost_bottomsheet_f2 = view.findViewById(R.id.cv_historyPost_bottomsheet_f2);

        cv_history_bottomsheet_f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), YourQuestion.class));
            }
        });

        cv_historyPost_bottomsheet_f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), YourPosts.class));
            }
        });
        return view;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
