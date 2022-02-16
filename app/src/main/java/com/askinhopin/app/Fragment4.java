package com.askinhopin.app;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment4 extends Fragment implements View.OnClickListener {

    ImageView dp_f4;
    TextView tv_name_f4, tv_batch_f4, tv_bio_f4, tv_email_f4, tv_phone_f4, tv_followers_f4, tv_post_f4;
    ProgressBar progressbar_f4;
    ImageButton ib_editprofile_f4, ib_menu_f4, ib_fb_f4, ib_insta_f4,ib_twitter_f4,ib_linkedin_f4,ib_web_f4;
    CardView cv_dp_f4;
    String fbResult, instaResult, linkedinResult, twitterResult, webResult;
    Button add_batch_f4;
    RecyclerView rv_batch_f4;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment4, container, false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dp_f4 = getActivity().findViewById(R.id.dp_f4);
        tv_name_f4 = getActivity().findViewById(R.id.tv_name_f4);
        tv_batch_f4 = getActivity().findViewById(R.id.tv_batch_f4);
        tv_bio_f4 = getActivity().findViewById(R.id.tv_bio_f4);
        tv_email_f4 = getActivity().findViewById(R.id.tv_email_f4);
        tv_phone_f4 = getActivity().findViewById(R.id.tv_phone_f4);
        tv_followers_f4 = getActivity().findViewById(R.id.tv_followers_f4);
        tv_post_f4 = getActivity().findViewById(R.id.tv_post_f4);
        progressbar_f4 = getActivity().findViewById(R.id.progressbar_f4);
        ib_editprofile_f4 = getActivity().findViewById(R.id.ib_editprofile_f4);
        ib_menu_f4 = getActivity().findViewById(R.id.ib_menu_f4);
        cv_dp_f4 = getActivity().findViewById(R.id.cv_dp_f4);
        ib_fb_f4 = getActivity().findViewById(R.id.ib_fb_f4);
        ib_insta_f4 = getActivity().findViewById(R.id.ib_insta_f4);
        ib_twitter_f4 = getActivity().findViewById(R.id.ib_twitter_f4);
        ib_linkedin_f4 = getActivity().findViewById(R.id.ib_linkedin_f4);
        ib_web_f4 = getActivity().findViewById(R.id.ib_web_f4);
        add_batch_f4 = getActivity().findViewById(R.id.add_batch_f4);
        rv_batch_f4 = getActivity().findViewById(R.id.rv_batch_f4);

        ib_editprofile_f4.setOnClickListener(this);
        ib_menu_f4.setOnClickListener(this);
        cv_dp_f4.setOnClickListener(this);
        ib_insta_f4.setOnClickListener(this);
        ib_fb_f4.setOnClickListener(this);
        ib_web_f4.setOnClickListener(this);
        ib_twitter_f4.setOnClickListener(this);
        ib_linkedin_f4.setOnClickListener(this);
        add_batch_f4.setOnClickListener(this);
        tv_post_f4.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rv_batch_f4.setLayoutManager(horizontalLayoutManager);

        List<BatchModel> batchModelList = new ArrayList<>();
        setBatches(batchModelList);


    }

    private void setBatches(List<BatchModel> batchModelList) {
        DatabaseReference batchRef = databaseReference.child("Batches").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batchRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BatchModel batchModelChild = snapshot.getValue(BatchModel.class);
                batchModelList.add(batchModelChild);


                BatchHorizontalAdaptar batchHorizontalAdaptar = new BatchHorizontalAdaptar(batchModelList);


                rv_batch_f4.setAdapter(batchHorizontalAdaptar);
                batchHorizontalAdaptar.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_editprofile_f4 :
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;

            case R.id.ib_menu_f4 :
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
                break;

            case R.id.ib_insta_f4 :
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(instaResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_fb_f4 :
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(fbResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_web_f4 :
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(webResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_twitter_f4 :
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(twitterResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_linkedin_f4 :
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(linkedinResult));
                    startActivity(intent1);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cv_dp_f4 :
                startActivity(new Intent(getActivity(), DisplayPictureView.class));
                break;

            case R.id.tv_post_f4:
                startActivity(new Intent(getActivity(), AllUsersSearchActivity.class));
                break;

            case R.id.add_batch_f4 :
                addBatch();
                break;
        }
    }

    private void addBatch() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.add_batch_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText detail_add_batch_layout = promptsView.findViewById(R.id.detail_add_batch_layout);
        final RadioGroup selection_type_add_batch_layout = promptsView.findViewById(R.id.selection_type_add_batch_layout);



        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                progressbar_f4.setVisibility(View.VISIBLE);
                                String batchType = "none";

                                String description;
                                if(selection_type_add_batch_layout.getCheckedRadioButtonId() == R.id.skill_add_batch_layout){
                                    batchType = "skill";
                                }if(selection_type_add_batch_layout.getCheckedRadioButtonId() == R.id.achivement_add_batch_layout){
                                    batchType = "award";
                                }if(selection_type_add_batch_layout.getCheckedRadioButtonId() == R.id.job_add_batch_layout){
                                    batchType = "job";
                                }

                                description = detail_add_batch_layout.getText().toString().trim();

                                uploadBatch(batchType, description);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void uploadBatch( String batchType, String descr) {

        Map<String, String> profile = new HashMap<>();
        profile.put("batchType", batchType);
        profile.put("batchDesc", descr);
        int iconRef= R.drawable.ic_medal_solid;

        if(batchType == "none"){
            progressbar_f4.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Enter Valid details", Toast.LENGTH_SHORT).show();
            return;
        }else if(batchType == "skill"){
            iconRef = R.drawable.ic_medal_solid;
        }else if(batchType == "award"){
            iconRef = R.drawable.ic_trophy_solid;
        }else if(batchType == "job"){
            iconRef = R.drawable.ic_briefcase_solid;
        }

        DatabaseReference mRef= databaseReference.child("Batches").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String id = mRef.push().getKey();
        profile.put("id", id);
        BatchModel batchModel = new BatchModel(iconRef, batchType, descr, id);
        mRef.child(id).setValue(batchModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        progressbar_f4.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        DocumentReference reference ;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(currentuid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){



                            String nameResult = task.getResult().getString("name_str");
                            String batchResult = task.getResult().getString("batch_str");
                            String branchResult = task.getResult().getString("branch_str");
                            String bioResult = task.getResult().getString("bio_str");
                            String emailResult = task.getResult().getString("email_str");
                            String phoneResult = task.getResult().getString("phone_str");
                            String imageurlResult = task.getResult().getString("imageurl");
                            fbResult = task.getResult().getString("fb_str");
                            instaResult = task.getResult().getString("insta_str");
                            linkedinResult = task.getResult().getString("linkedin_str");
                            twitterResult = task.getResult().getString("twitter_str");
                            webResult = task.getResult().getString("web_str");

                            if(TextUtils.isEmpty(fbResult)){
                                ib_fb_f4.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(instaResult)){
                                ib_insta_f4.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(twitterResult)){
                                ib_twitter_f4.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(linkedinResult)){
                                ib_linkedin_f4.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(webResult)){
                                ib_web_f4.setVisibility(View.GONE);
                            }


                            if(!TextUtils.isEmpty(imageurlResult))
                                 Picasso.get().load(imageurlResult).fit().centerCrop().into(dp_f4);
                            tv_name_f4.setText(nameResult);
                            tv_batch_f4.setText(branchResult + " | " + batchResult);
                            tv_bio_f4.setText(bioResult);
                            tv_email_f4.setText(emailResult);
                            tv_phone_f4.setText(phoneResult);

                            progressbar_f4.setVisibility(View.GONE);


                        }else{
                            Intent intent = new Intent(getActivity(), CreateProfile.class);
                            startActivity(intent);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressbar_f4.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error Loading Profile Data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
