package com.example.travelguidapplication.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.travelguidapplication.ChangeEmailActivity;
import com.example.travelguidapplication.Interface.AllConstant;
import com.example.travelguidapplication.MainActivity;
import com.example.travelguidapplication.Model.UserModel;
import com.example.travelguidapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private TextInputEditText txt_name;
    private TextInputLayout txt_nameLayout;
    private Button btn_saveChanges;
    private DatabaseReference databaseReference;
    private String userId,name,profilePicture;
    private CircleImageView btn_changePicture,img_profilePicture;
    private FirebaseAuth firebaseAuth;
    private TextView btn_changeEmail;

    private Uri imageUrl;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private String image="";
    private String checker = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_settings, container, false);

        txt_name =  (TextInputEditText)view.findViewById(R.id.textInputEditTextSettingsName);
        img_profilePicture =  (CircleImageView)view.findViewById(R.id.settingsProfilePicture);
        btn_changePicture =  (CircleImageView)view.findViewById(R.id.settingsProfilePictureChange);
        btn_saveChanges =  (Button) view.findViewById(R.id.settingsSaveChangesButton);
        btn_changeEmail=(TextView) view.findViewById(R.id.textViewChangeEmail);

        txt_nameLayout = (TextInputLayout) view.findViewById(R.id.textInputLayoutSettingsName);

        userId=firebaseAuth.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        getUserData();

        btn_changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        btn_changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker = "clicked";

                /*CropImage.activity(imageUrl)
                        .setAspectRatio(1, 1)
                        .start(ProfileSettingsActivity.this);*/

                OpenGallery();

            }
        });

        btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = txt_name.getText().toString();

                if (!validateName()) {
                    return;
                }
                else {

                    if (checker.equals("clicked")) {
                        uploadProfilePicture(name,userId);
                    } else {


                        updateOnlyUserInfo(name,userId);
                    }
                }

            }

        });

        txt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                txt_nameLayout.setError(null);
                txt_name.setBackground(getContext().getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void getUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    Glide.with(getContext()).load(userModel.getImage()).into(img_profilePicture);
                    txt_name.setText(userModel.getUsername());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image from here..."), CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void updateOnlyUserInfo(String name,String userId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("username",name);
        ref.child(userId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //Data insert Successfully
                Intent intent = new Intent(getContext(), MainActivity.class);

                startActivity(intent);

            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUrl = data.getData();
            img_profilePicture.setImageURI(imageUrl);
        }
        else
        {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));

        }
    }

    private void uploadProfilePicture(String name,String userId)
    {
        final StorageReference fileRef = storageReference.child(userId+ AllConstant.IMAGE_PATH);

        uploadTask = fileRef.putFile(imageUrl);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception
            {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }

                return fileRef.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        Uri downloadUrl = task.getResult();
                        image = downloadUrl.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image", image);
                        hashMap. put("username",name);

                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                OpenGallery();
            } else {
                Toast.makeText(getContext(), "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean validateName() {

        if (TextUtils.isEmpty(txt_name.getText())) {
            txt_nameLayout.setError("Field can not be empty");
            return false;
        } else {
            txt_nameLayout.setError(null);
            return true;
        }

    }
}