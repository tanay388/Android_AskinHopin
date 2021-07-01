package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {

    private EditText et_send_message;
    private ImageButton ib_send_message, ib_back_message;
    private RecyclerView rv_message;
    private ImageView iv_dp_thumbnail_message;
    private TextView tv_chat_reciever_name_message;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference rootref1, rootref2;
    MessageMember messageMember;
    ConversationClass conversationClass1, conversationClass2;

    String reciever_uid, reciever_url, reciever_name, sender_uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        et_send_message = findViewById(R.id.et_send_message);
        ib_send_message = findViewById(R.id.ib_send_message);
        rv_message = findViewById(R.id.rv_message);
        ib_back_message = findViewById(R.id.ib_back_message);
        iv_dp_thumbnail_message = findViewById(R.id.iv_dp_thumbnail_message);
        tv_chat_reciever_name_message = findViewById(R.id.tv_chat_reciever_name_message);
        messageMember = new MessageMember();
        conversationClass1 = new ConversationClass();
        conversationClass2 = new ConversationClass();

        rv_message.setHasFixedSize(true);


        Bundle extras= getIntent().getExtras();

        if(extras == null){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else{
            reciever_name = extras.getString("name");
            reciever_uid = extras.getString("uid");
            reciever_url = extras.getString("url");
        }


        Picasso.get().load(reciever_url).fit().centerCrop().into(iv_dp_thumbnail_message);
        tv_chat_reciever_name_message.setText(reciever_name);

        sender_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootref1 = database.getReference().child("Messages").child(reciever_uid).child(sender_uid);
        rootref2 = database.getReference().child("Messages").child(sender_uid).child(reciever_uid);

        DatabaseReference conref= database.getReference().child("Conversation").child(sender_uid).child(reciever_uid);

        conref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    Boolean read = snapshot.child("read").getValue(Boolean.class);
                     if(!read){
                        conref.child("read").setValue(true);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });





        ib_back_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ib_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MessageMember> options = new FirebaseRecyclerOptions.Builder<MessageMember>()
                .setQuery(rootref2, MessageMember.class)
                .build();

        FirebaseRecyclerAdapter<MessageMember, ViewHolder_Message> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MessageMember, ViewHolder_Message>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_Message holder, int position, @NonNull MessageMember model) {
                        holder.setChatMessage(getApplication(), model.getSenderuid(), model.getRecieveruid(), model.getTime(), model.date, model.getMessage(), model.getType());

                    }


                    @NonNull
                    @Override
                    public ViewHolder_Message onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_item, parent, false);

                        return new ViewHolder_Message(view);
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




        firebaseRecyclerAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        firebaseRecyclerAdapter.startListening();
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setStackFromEnd(true);
        rv_message.setLayoutManager(mLinearLayoutManager);
        rv_message.setAdapter(firebaseRecyclerAdapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                rv_message.scrollToPosition(firebaseRecyclerAdapter.getItemCount());
            }
        }, 200);
    }

    private void sendMessage() {
        String message= et_send_message.getText().toString().trim();

        if(message.isEmpty()){
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            String time = savedate + " | " + savetime;

            messageMember.setDate(savedate);
            messageMember.setTime(time);
            messageMember.setMessage(message);
            messageMember.setSenderuid(sender_uid);
            messageMember.setType("text");
            messageMember.setRecieveruid(reciever_uid);

            String key = rootref2.push().getKey();

            DatabaseReference conref1= database.getReference().child("Conversation").child(sender_uid).child(reciever_uid);
            DatabaseReference conref2= database.getReference().child("Conversation").child(reciever_uid).child(sender_uid);

            conversationClass1.setLastmessage(message.substring(0, Math.min(message.length(), 30)));
            conversationClass1.setTime(cdate.getTimeInMillis());
            conversationClass1.setRead(true);
            conversationClass1.setSenderuid(sender_uid);
            conversationClass1.setRecieveruid(reciever_uid);

            conversationClass2.setLastmessage(message.substring(0, Math.min(message.length(), 30)));
            conversationClass2.setTime(cdate.getTimeInMillis());
            conversationClass2.setRead(false);
            conversationClass2.setSenderuid(sender_uid);
            conversationClass2.setRecieveruid(reciever_uid);

            rootref2.child(key).setValue(messageMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    et_send_message.setText("");
                    rootref1.child(key).setValue(messageMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            conref1.setValue(conversationClass1);
                            conref2.setValue(conversationClass2);
                        }
                    });
                }
            });
        }
    }
}