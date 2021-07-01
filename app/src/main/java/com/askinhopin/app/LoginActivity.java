package com.askinhopin.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText email_LoginActivity,password_LoginActivity;
    private CheckBox showPasswordCheckBoxLoginActivity;
    private Button loginButton_LoginActivity,registerButton_LoginActivity;
    private ProgressBar progressbar_LoginActivity;
    private FirebaseAuth mAuth;
    private TextView forgot_password_LoginActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //EditText
        email_LoginActivity = findViewById(R.id.email_LoginActivity);
        password_LoginActivity = findViewById(R.id.password_LoginActivity);

        //CheckBox
        showPasswordCheckBoxLoginActivity = findViewById(R.id.showPasswordCheckBoxLoginActivity);

        //Progress Bar
        progressbar_LoginActivity = findViewById(R.id.progressbar_LoginActivity);

        //Buttons
        loginButton_LoginActivity = findViewById(R.id.loginButton_LoginActivity);
        registerButton_LoginActivity = findViewById(R.id.registerButton_LoginActivity);

        //TextView
        forgot_password_LoginActivity = findViewById(R.id.forgot_password_LoginActivity);

        mAuth = FirebaseAuth.getInstance();



        //Register Button Click Activity
        registerButton_LoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_to_Signup = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(login_to_Signup);
            }
        });

        //Show-Hide Password
        showPasswordCheckBoxLoginActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password_LoginActivity.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    confirmPassword_LoginActivity.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    password_LoginActivity.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    confirmPassword_RegisterActivity.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

        //LoginAction
        loginButton_LoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email_LoginActivity.getText().toString();
                String mPassword = password_LoginActivity.getText().toString();

                if(mEmail.isEmpty() || mPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter All the required Fields", Toast.LENGTH_SHORT).show();

                }else {
                    progressbar_LoginActivity.setVisibility(View.VISIBLE);
                    loginButton_LoginActivity.setVisibility(View.INVISIBLE);
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressbar_LoginActivity.setVisibility(View.GONE);
                                loginButton_LoginActivity.setVisibility(View.VISIBLE);


                                FirebaseUser user = mAuth.getCurrentUser();
                                if(!user.isEmailVerified()){
                                    Intent unverify = new Intent(LoginActivity.this, UnVerifiedActivity.class);
                                    unverify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(unverify);
                                }else if(user.isEmailVerified()){
                                    Intent verify = new Intent(LoginActivity.this, MainActivity.class);
                                    verify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(verify);
                                }
                            }else {

                                String error =task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : "+ error,Toast.LENGTH_SHORT).show();
                                progressbar_LoginActivity.setVisibility(View.GONE);
                                loginButton_LoginActivity.setVisibility(View.VISIBLE);
                            }
                        }
                    });




                }
            }
        });

        //Forgot Password
        forgot_password_LoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email_LoginActivity.getText().toString();

                if(mEmail.isEmpty()){
                    AlertDialog.Builder dialog=new AlertDialog.Builder(LoginActivity.this);
                    dialog.setMessage("Please Enter Your eMail Address to reset password and click this again.");
                    dialog.setTitle("Forgot Password");
                    dialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    password_LoginActivity.setVisibility(View.GONE);
                                }
                            });
                    dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog=dialog.create();
                    alertDialog.show();
                }else {
                    progressbar_LoginActivity.setVisibility(View.VISIBLE);
                    loginButton_LoginActivity.setVisibility(View.GONE);

                    mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }

                                    progressbar_LoginActivity.setVisibility(View.GONE);
                                    loginButton_LoginActivity.setVisibility(View.VISIBLE);
                                    password_LoginActivity.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }


        });



    }
}