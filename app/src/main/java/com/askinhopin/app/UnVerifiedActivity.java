package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UnVerifiedActivity extends AppCompatActivity {
    private FirebaseUser user;
    private ProgressBar progressbar_UnVerified,progressbar2_UnVerified;
    private Button button_UnVerified, button2_UnVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unverified);
        progressbar_UnVerified = findViewById(R.id.progressbar_UnVerified);
        progressbar2_UnVerified = findViewById(R.id.progressbar2_UnVerified);
        button_UnVerified = findViewById(R.id.button_UnVerified);
        button2_UnVerified = findViewById(R.id.button2_UnVerified);

        user = FirebaseAuth.getInstance().getCurrentUser();

        button_UnVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Intent logout = new Intent(UnVerifiedActivity.this, LoginActivity.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
            }
        });

        button2_UnVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_UnVerified.setClickable(false);

                progressbar2_UnVerified.setVisibility(View.VISIBLE);
                button2_UnVerified.setVisibility(View.GONE);

                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressbar2_UnVerified.setVisibility(View.GONE);
                        button2_UnVerified.setVisibility(View.VISIBLE);
                        Toast.makeText(UnVerifiedActivity.this, "Verification Mail Resent Successfully!",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressbar2_UnVerified.setVisibility(View.GONE);
                        button2_UnVerified.setVisibility(View.VISIBLE);
                        Toast.makeText(UnVerifiedActivity.this, "Error : "+ e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        user= FirebaseAuth.getInstance().getCurrentUser();

        if(user.isEmailVerified()){
            Intent unverify = new Intent(UnVerifiedActivity.this, MainActivity.class);
            unverify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(unverify);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        user= FirebaseAuth.getInstance().getCurrentUser();

        if(user.isEmailVerified()){
            Intent unverify = new Intent(UnVerifiedActivity.this, MainActivity.class);
            unverify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(unverify);
        }
    }
}