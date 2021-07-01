package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText email_RegisterActivity,password_RegisterActivity,confirmPassword_RegisterActivity;
    private CheckBox showPasswordCheckBoxRegisterActivity;
    private Button registerButton_RegisterActivity,loginButton_RegisterActivity;
    private ProgressBar progressbar_RegisterActivity;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private Spinner spinner_SchoolSelction_RegisterActivity;
    private String school="none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_RegisterActivity= findViewById(R.id.email_RegisterActivity);
        password_RegisterActivity = findViewById(R.id.password_RegisterActivity);
        confirmPassword_RegisterActivity = findViewById(R.id.confirmPassword_RegisterActivity);

        //checkbox
        showPasswordCheckBoxRegisterActivity = findViewById(R.id.showPasswordCheckBoxRegisterActivity);

        //button
        registerButton_RegisterActivity = findViewById(R.id.registerButton_RegisterActivity);
        loginButton_RegisterActivity = findViewById(R.id.loginButton_RegisterActivity);

        //progressbar
        progressbar_RegisterActivity = findViewById(R.id.progressbar_RegisterActivity);

        //Spinner
        spinner_SchoolSelction_RegisterActivity = findViewById(R.id.spinner_SchoolSelction_RegisterActivity);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference();



        spinner_SchoolSelction_RegisterActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Toast.makeText(RegisterActivity.this, "Please Select a valid Institution Name", Toast.LENGTH_SHORT).show();

                }else{
                    school = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showPasswordCheckBoxRegisterActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password_RegisterActivity.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPassword_RegisterActivity.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    password_RegisterActivity.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword_RegisterActivity.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

        registerButton_RegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= email_RegisterActivity.getText().toString();
                String password= password_RegisterActivity.getText().toString().trim();
                String confPass = confirmPassword_RegisterActivity.getText().toString().trim();


                if(email.isEmpty() || password.isEmpty() || confPass.isEmpty() || school.equals("none")){
                    Toast.makeText(RegisterActivity.this, "Enter All the required Fields", Toast.LENGTH_SHORT).show();

                }else {
                    if (!password.equals(confPass)){
                        Toast.makeText(RegisterActivity.this, "Password and Confirm Password are not matching" + password +" "+ confPass, Toast.LENGTH_SHORT).show();

                    }else {
                        progressbar_RegisterActivity.setVisibility(View.VISIBLE);
                        registerButton_RegisterActivity.setVisibility(View.INVISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressbar_RegisterActivity.setVisibility(View.INVISIBLE);
                                    registerButton_RegisterActivity.setVisibility(View.VISIBLE);

                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Verification Mail Sent Successfully!",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Error : "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });

                                    mDbRef.child("All_Users").child(user.getUid()).child("school").setValue(school);
                                    mDbRef.child("All_Users").child(user.getUid()).child("email").setValue(email);
                                    mDbRef.child(school).child(user.getUid());

                                    if(!user.isEmailVerified()){
                                        Intent unverify = new Intent(RegisterActivity.this, UnVerifiedActivity.class);
                                        unverify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(unverify);
                                    }else if(user.isEmailVerified()){
                                        Intent verify = new Intent(RegisterActivity.this, MainActivity.class);
                                        verify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(verify);
                                    }



                                }else {
                                    Toast.makeText(RegisterActivity.this, "Error Try again",Toast.LENGTH_SHORT).show();

                                }




                            }
                        });
                    }
                }
            }
        });

    }
}