package com.example.travelguidapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class RateNowBottomSheet extends BottomSheetDialogFragment {

    private Button btn_submit;
    private RatingBar rtb_userRating;
    private TextInputEditText txt_review;
    private TextView btn_close;
    private String rateValue;
    private String retrieveRateId=null;
    private String userId,rateId;
    private int placeId;
    private Context context;
    private  BottomSheetDialog bottomSheetDialog;

    public RateNowBottomSheet(Context context, int placeId){
        this.context=context;
        this.placeId=placeId;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style. CustomBottomSheetDialogTheme);

    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog= (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout_place_rate,null);

        rtb_userRating = (RatingBar) view.findViewById(R.id.userRating);
        btn_submit = (Button) view.findViewById(R.id.buttonSubmit);
        txt_review = (TextInputEditText) view.findViewById(R.id.txtReview);
        btn_close = (TextView) view.findViewById(R.id.btnRatingClose);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Places").child(String.valueOf(placeId)).child("UserRating").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot ds:snapshot.getChildren()) {
                        retrieveRateId = ds.child("rateId").getValue().toString();
                        String retrieveRateValue = ds.child("rateValue").getValue().toString();
                        String retrieveReview = ds.child("review").getValue().toString();

                        rtb_userRating.setRating(Float.parseFloat(retrieveRateValue));
                        txt_review.setText(retrieveReview);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rtb_userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = String.valueOf(ratingBar.getRating());
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rtb_userRating.getRating() == 0.0)
                {
                    int blue = Color.parseColor("#2f3ced");
                    new StyleableToast.Builder(context)
                            .text("Required Place Rating")
                            .backgroundColor(blue)
                            .solidBackground()
                            .textColor(Color.WHITE)
                            .gravity(Gravity.TOP)
                            .cornerRadius(50)
                            .textSize(12)
                            .show();

                }
                else
                {
                    saveUserReviewRating(rateValue,txt_review.getText().toString(),userId,retrieveRateId);

                }

            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.setContentView(view);

        return bottomSheetDialog;
    }

    private void saveUserReviewRating(String rateValue, String review,String userId,String retrieveRateId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (retrieveRateId==null) {


            RandomString randomPlaceKey = new RandomString(21);
            rateId = randomPlaceKey.nextString();

            HashMap<String, Object> userRateDataHashMap = new HashMap<>();
            userRateDataHashMap.put("rateId", rateId);
            userRateDataHashMap.put("rateValue", rateValue);
            userRateDataHashMap.put("userId", userId);
            if (review == null) {
                userRateDataHashMap.put("review", "");
            } else {
                userRateDataHashMap.put("review", review);
            }

            databaseReference.child("Places").child(String.valueOf(placeId)).child("UserRating").child(rateId).setValue(userRateDataHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    bottomSheetDialog.cancel();
                }
            });
        }
        else
        {
            HashMap<String, Object> userRateUpdateDataHashMap = new HashMap<>();
            userRateUpdateDataHashMap.put("rateValue",rateValue);
            if(review==null)
            {
                userRateUpdateDataHashMap.put("review","");
            }
            else
            {
                userRateUpdateDataHashMap.put("review",review);
            }

            databaseReference.child("Places").child(String.valueOf(placeId)).child("UserRating").child(retrieveRateId).updateChildren(userRateUpdateDataHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    bottomSheetDialog.cancel();
                }
            });
        }

    }

}
