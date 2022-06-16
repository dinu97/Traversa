package com.example.travelguidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguidapplication.Session.PlaceSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

public class PlaceDetailActivity extends AppCompatActivity {

    private  int placeId,placeRating;
    private float placeLatitude,placeLongitude;
    private String placeName,placeImage,placeDescription;
    private TextView txt_placeName,txt_placeDescription,btn_viewRating,btn_rateNow,txt_ratingValue;
    private ImageView img_placeImage;
    private RatingBar rtb_placeRating;
    private Button btn_placeShowMap;
    private float totRateCount=0;
    private float totUserRateCount=0;
    private float userRateCount=0;
    private float rating=0;
    private android.speech.tts.TextToSpeech textToSpeech;
    private FloatingActionButton btn_voice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        PlaceSession placeSession = new PlaceSession(getApplicationContext());
        HashMap<String, String> place = placeSession.readPlaceDetails();

        placeId = Integer.parseInt(place.get(PlaceSession.KEY_PLACEID));

        txt_placeName = findViewById(R.id.placeName);
        txt_placeDescription = findViewById(R.id.placeDescription);
        img_placeImage = findViewById(R.id.placeImage);
        rtb_placeRating = findViewById(R.id.placeRating);
        btn_placeShowMap = findViewById(R.id.placeShowMap);
        btn_viewRating = findViewById(R.id.buttonViewRating);
        btn_rateNow = findViewById(R.id.buttonRateNow);
        txt_ratingValue = findViewById(R.id.ratingValue);
        btn_voice = findViewById(R.id.btnVoice);

        btn_rateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RateNowBottomSheet rateNowBottomSheet=new RateNowBottomSheet(PlaceDetailActivity.this,placeId);
                rateNowBottomSheet.show(getSupportFragmentManager(),rateNowBottomSheet.getTag());

            }
        });

        btn_viewRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewRatingBottomSheet viewRatingBottomSheet=new ViewRatingBottomSheet(PlaceDetailActivity.this,placeId);
                viewRatingBottomSheet.show(getSupportFragmentManager(),viewRatingBottomSheet.getTag());

            }
        });

        textToSpeech = new android.speech.tts.TextToSpeech(getApplicationContext()
                , new android.speech.tts.TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i== android.speech.tts.TextToSpeech.SUCCESS){
                    textToSpeech.setLanguage(Locale.ENGLISH);

                }

            }
        });

        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = txt_placeDescription.getText().toString();
               textToSpeech.speak(s, android.speech.tts.TextToSpeech.QUEUE_FLUSH,null);

            }
        });
        showPlaceDetails(placeId);
    }

    private void showPlaceDetails(int placeId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Places").orderByChild("placeId").equalTo(placeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren())
                {
                    placeName=ds.child("placeName").getValue().toString();
                    placeDescription=ds.child("placeDescription").getValue().toString();
                    placeImage=ds.child("placeImage").getValue().toString();
                    placeRating= Integer.parseInt(ds.child("rating").getValue().toString());
                    placeLatitude= Float.parseFloat(ds.child("latitude").getValue().toString());
                    placeLongitude= Float.parseFloat(ds.child("longitude").getValue().toString());

                    txt_placeName.setText(placeName);
                    txt_placeDescription.setText(placeDescription);
                    Picasso.get().load(placeImage).into(img_placeImage);

                    btn_placeShowMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(v.getContext(), ShowMapActivity.class);

                            PlaceSession placeSession =new PlaceSession(getApplicationContext());
                            placeSession.writePlaceDetails(String.valueOf(placeId),String.valueOf(placeLatitude),String.valueOf(placeLongitude));

                            v.getContext().startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void Rating()
    {
        DatabaseReference RateRef= FirebaseDatabase.getInstance().getReference().child("Places").child(String.valueOf(placeId)).child("UserRating");
        Query query=RateRef.orderByChild("rateId");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    totRateCount = (Math.toIntExact(snapshot.getChildrenCount())) * 5;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        userRateCount = Float.parseFloat(ds.child("rateValue").getValue().toString());
                        totUserRateCount = totUserRateCount + userRateCount;
                    }

                    rating = ((totUserRateCount / totRateCount) * 100) / 20;
                    rtb_placeRating.setRating(rating);
                    String valueRating = String.format("%.1f", rating);
                    txt_ratingValue.setText(valueRating);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        textToSpeech.shutdown();
        PlaceSession placeSession = new PlaceSession(getApplicationContext());
        placeSession.clearPlaceSession();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Rating();
    }


}