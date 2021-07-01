package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference Notificationdb;
    long count_notification_unread;
    String school;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        RelativeLayout parent_f1 = findViewById(R.id.parent_f1);


        BottomNavigationView botton_nav = findViewById(R.id.botton_nav);
        FrameLayout frameLayout = findViewById(R.id.frame_layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                parent_f1.setBackgroundColor(getResources().getColor(R.color.bg_main));
                botton_nav.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else{

                    botton_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            Fragment fragment = null;

                            switch (item.getItemId()){
                                case R.id.home_bottom:
                                    fragment = new Fragment1();
                                    break;

                                case R.id.questions_bottom:
                                    fragment = new Fragment2();
                                    break;

                                case R.id.follow_bottom:
                                    fragment = new Fragment3();
                                    break;

                                case R.id.profile_bottom:
                                    fragment = new Fragment4();
                                    break;
                            }

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

                            return true;
                        }
                    });

                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Fragment1()).commitAllowingStateLoss();

                    DatabaseReference mRef= database.getReference().child("All_Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school");

                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            school = snapshot.getValue(String.class);
                            Notificationdb = database.getReference().child(school).child("user")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notification");
                            Notificationdb.orderByChild("unread").equalTo(true ).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    count_notification_unread = dataSnapshot.getChildrenCount();
                                    botton_nav.getOrCreateBadge(R.id.follow_bottom).setNumber(Integer.parseInt(String.valueOf(count_notification_unread)));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });



                }


            }
        },5000);




//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser() == null){
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }



    }

}