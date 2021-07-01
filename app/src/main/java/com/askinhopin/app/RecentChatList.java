package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class RecentChatList extends AppCompatActivity {

    private ImageButton ib_back_recentchat;
    private RecyclerView rv_recentpost;

    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference rootref1, rootref2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    String name_str, url_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat_list);

        rv_recentpost = findViewById(R.id.rv_recentpost);
        ib_back_recentchat = findViewById(R.id.ib_back_recentchat);

        rv_recentpost.setHasFixedSize(true);
        rv_recentpost.setLayoutManager(new LinearLayoutManager(RecentChatList.this));

        rootref1 = database.getReference().child("Conversation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        retriveList(rootref1);
        ib_back_recentchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void retriveList(DatabaseReference rootref1) {

        FirebaseRecyclerOptions<ConversationClass> options = new FirebaseRecyclerOptions.Builder<ConversationClass>()
                .setQuery(rootref1, ConversationClass.class)
                .build();

        FirebaseRecyclerAdapter<ConversationClass, ViewHolder_RecentchatlistItem> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ConversationClass, ViewHolder_RecentchatlistItem>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_RecentchatlistItem holder, int position, @NonNull ConversationClass model) {

                        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(model.getRecieveruid().equals(currentUid)){
                            documentReference = db.collection("user").document(model.getSenderuid());

                            documentReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                name_str = task.getResult().getString("name_str");
                                                url_str = task.getResult().getString("imageurl");
                                                holder.setRecentChat(name_str, url_str, model.getTime(), model.getLastmessage());
                                                if(!model.isRead()){
                                                    holder.tv_name_recentuserlist_item.setTextColor(getResources().getColor(R.color.chatUnread));
                                                    holder.tv_lastmessage_recentuserlist_item.setTextColor(getResources().getColor(R.color.chatUnread));
                                                }
                                                holder.ll_recentchatlist_item.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent1 = new Intent(RecentChatList.this, MessageActivity.class);
                                                        intent1.putExtra("uid", model.getSenderuid());
                                                        intent1.putExtra("url", url_str);
                                                        intent1.putExtra("name", name_str);
                                                        startActivity(intent1);
                                                    }
                                                });
                                            }
                                        }
                                    });



                        }else if(model.getSenderuid().equals(currentUid)){
                            documentReference = db.collection("user").document(model.getRecieveruid());

                            documentReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                name_str = task.getResult().getString("name_str");
                                                url_str = task.getResult().getString("imageurl");
                                                holder.setRecentChat(name_str, url_str, model.getTime(), model.getLastmessage());
                                                if(!model.isRead()){
                                                    holder.tv_name_recentuserlist_item.setTextColor(getResources().getColor(R.color.chatUnread));
                                                    holder.tv_lastmessage_recentuserlist_item.setTextColor(getResources().getColor(R.color.chatUnread));
                                                }

                                                holder.ll_recentchatlist_item.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent1 = new Intent(RecentChatList.this, MessageActivity.class);
                                                        intent1.putExtra("uid", model.getRecieveruid());
                                                        intent1.putExtra("url", url_str);
                                                        intent1.putExtra("name", name_str);
                                                        startActivity(intent1);
                                                    }
                                                });
                                            }
                                        }
                                    });


                        }

                    }


                    @NonNull
                    @Override
                    public ViewHolder_RecentchatlistItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recentchatlist_item, parent, false);

                        return new ViewHolder_RecentchatlistItem(view);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return position;
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                };

        firebaseRecyclerAdapter.startListening();
        rv_recentpost.setAdapter(firebaseRecyclerAdapter);
        rv_recentpost.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }
}