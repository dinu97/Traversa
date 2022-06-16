package com.example.travelguidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguidapplication.databinding.ActivityForgetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    ActivityForgetBinding binding;
    private EditText forEmail;
    private Button btnForgetPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        forEmail = findViewById(R.id.forEmail);
        btnForgetPassword =findViewById(R.id.btnForgetPassword);

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
});

     btnForgetPassword.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             email =forEmail.getText().toString();

             if (email.isEmpty()){
                 Toast.makeText(ForgetActivity.this,"Please provide your email",Toast.LENGTH_SHORT).show();

             }else {
                 forgotPassword();
             }
         }
     });

    }

    private void forgotPassword() {
        FirebaseAuth auth =FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgetActivity.this,"check your email",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                            finish();
                        }else {
                            Toast.makeText(ForgetActivity.this,"Error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}