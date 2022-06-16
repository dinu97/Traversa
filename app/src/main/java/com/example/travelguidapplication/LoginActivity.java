package com.example.travelguidapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguidapplication.Utility.LoadingDialog;
import com.example.travelguidapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private String email,password;
    private LoadingDialog loadingDialog;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private TextView btn_forgetPassword;
    private String userId,fullName,profilePicture,emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        btn_forgetPassword=findViewById(R.id.txtForgetPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        });

        binding.txtForgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
        });
        binding.btnLogin.setOnClickListener(view -> {
            if (areFieldReady()){
                login();

            }
        });

        btn_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login() {
        loadingDialog.startLoading();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {


                        loadingDialog.stopLoading();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> email) {
                                if (email.isSuccessful()) {
                                    loadingDialog.stopLoading();
                                    Toast.makeText(LoginActivity.this, "Please verify email", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingDialog.stopLoading();
                                    Toast.makeText(LoginActivity.this, "Error : " + email.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else {
                    loadingDialog.stopLoading();
                    int blue = Color.parseColor("#2f3ced");
                    new StyleableToast.Builder(LoginActivity.this)
                            .text("User not found,Check your email and password")
                            .backgroundColor(blue)
                            .solidBackground()
                            .textColor(Color.WHITE)
                            .gravity(Gravity.TOP)
                            .cornerRadius(50)
                            .textSize(12)
                            .show();}

            }
        });


    }

    private boolean areFieldReady(){

        email=binding.edtEmail.getText().toString().trim();
        password=binding.edtPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;


       if (email.isEmpty()){
            binding.edtEmail.setError("Field is required");
            flag = true;
            requestView =binding.edtEmail;
        }else if (password.isEmpty()){
            binding.edtPassword.setError("Field is required");
            flag = true;
            requestView =binding.edtPassword;
        } else if (password.length()<8){
            binding.edtPassword.setError("Minimum 8 characters required");
            flag = true;
            requestView =binding.edtPassword;

        }

        if (flag){
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();

    }


}