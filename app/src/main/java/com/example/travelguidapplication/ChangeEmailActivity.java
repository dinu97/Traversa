package com.example.travelguidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguidapplication.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangeEmailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextInputEditText txt_currentEmail,txt_newEmail,txt_password;
    private TextInputLayout txt_currentEmailLayout,txt_newEmailLayout,txt_passwordLayout;
    private Button btn_saveChanges;
    private DatabaseReference databaseReference;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        txt_currentEmail = findViewById(R.id.textInputEditTextCurrentEmail);
        txt_password  = findViewById(R.id.textInputEditTextChangeEmailPassword);
        txt_newEmail = findViewById(R.id.textInputEditTextNewEmail);
        btn_saveChanges = findViewById(R.id.changeEmailSaveChangesButton);

        txt_currentEmailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutCurrentEmail);
        txt_newEmailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutNewEmail);
        txt_passwordLayout = (TextInputLayout) findViewById(R.id.textInputLayoutChangeEmailPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        userId=firebaseAuth.getUid();

        getUserData();

        btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateCurrentEmail() | !validatePassword() | !validateNewEmail()) {
                    return;
                }
                else {
                    ChangeEmail(txt_currentEmail.getText().toString(), txt_password.getText().toString(),txt_newEmail.getText().toString());

                }
            }
        });

        txt_currentEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                txt_currentEmailLayout.setError(null);
                txt_currentEmail.setBackground(getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_newEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                txt_newEmailLayout.setError(null);
                txt_newEmail.setBackground(getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                txt_passwordLayout.setError(null);
                txt_password.setBackground(getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void ChangeEmail(String currentEmail, String password,String newEmail) {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(currentEmail, password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> tasks) {
                if (tasks.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("email", newEmail);

                                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent(ChangeEmailActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangeEmailActivity.this, "Authentication Fail", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(ChangeEmailActivity.this, "Check your current email & Password", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    private void getUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    txt_currentEmail.setText(userModel.getEmail());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private boolean validateCurrentEmail() {

        if (TextUtils.isEmpty(txt_currentEmail.getText())) {
            txt_currentEmailLayout.setError("Current email can not be empty");
            txt_currentEmail.requestFocus();
            return false;
        } else {
            txt_currentEmailLayout.setError(null);
            return true;
        }

    }

    private boolean validateNewEmail() {

        if (TextUtils.isEmpty(txt_newEmail.getText())) {
            txt_newEmailLayout.setError("New email can not be empty");
            txt_newEmail.requestFocus();
            return false;
        } else {
            txt_newEmailLayout.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {

        if (TextUtils.isEmpty(txt_password.getText())) {
            txt_passwordLayout.setError("Password can not be empty");
            txt_password.requestFocus();
            return false;
        }else if (txt_password.getText().length()<8) {
            txt_passwordLayout.setError("Minimum 8 characters required");
            txt_password.requestFocus();
            return false;
        }
        else {
            txt_passwordLayout.setError(null);
            return true;
        }

    }
}