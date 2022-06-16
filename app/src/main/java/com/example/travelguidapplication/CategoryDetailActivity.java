package com.example.travelguidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.Place;
import com.example.travelguidapplication.Session.CategorySession;
import com.example.travelguidapplication.Session.PlaceSession;
import com.example.travelguidapplication.ViewHolder.PlaceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CategoryDetailActivity extends AppCompatActivity {

    private  int categoryId;
    private String categoryName;
    private TextView txt_categoryName;
    private RecyclerView recyclerView_categoryPlaces;

    private FirebaseRecyclerOptions<Place> optionsPlace;
    private FirebaseRecyclerAdapter<Place, PlaceViewHolder> adapterPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        CategorySession categorySession = new CategorySession(getApplicationContext());
        HashMap<String, String> category = categorySession.readCategoryDetails();

        categoryId = Integer.parseInt(category.get(CategorySession.KEY_CATEGORYID));
        categoryName = category.get(CategorySession.KEY_CATEGORYNAME);

        txt_categoryName = findViewById(R.id.categoryName);
        recyclerView_categoryPlaces = findViewById(R.id.recyclerViewPlace);

        txt_categoryName.setText(categoryName);

        ShowCategoryPlaces(categoryId);


    }

    private void ShowCategoryPlaces(int categoryId)
    {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView_categoryPlaces.setLayoutManager(manager);
        recyclerView_categoryPlaces.setHasFixedSize(true);

        DatabaseReference placeRef= FirebaseDatabase.getInstance().getReference().child("Places");
        Query query=placeRef.orderByChild("categoryId").equalTo(categoryId);


        optionsPlace=new FirebaseRecyclerOptions.Builder<Place>().setQuery(query,Place.class).build();
        adapterPlace=new FirebaseRecyclerAdapter<Place, PlaceViewHolder>(optionsPlace){
            @Override
            protected void onBindViewHolder(@NonNull PlaceViewHolder holder, int position, @NonNull Place place) {



                Picasso.get().load(place.getPlaceImage()).into(holder.img_categoryPlaceImage);
                holder.txt_categoryPlaceName.setText(place.getPlaceName());
//                holder.rtb_categoryPlaceRating.setRating(place.getRating());
                holder.card_categoryPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent=new Intent(v.getContext(), PlaceDetailActivity.class);

                        PlaceSession placeSession =new PlaceSession(getApplicationContext());
                        placeSession.writePlaceDetails(String.valueOf(place.getPlaceId()),String.valueOf(place.getLatitude()),String.valueOf(place.getLongitude()));

                        v.getContext().startActivity(intent);
                    }
                });

                holder.btn_categoryShowMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(v.getContext(), ShowMapActivity.class);

                        PlaceSession placeSession =new PlaceSession(getApplicationContext());
                        placeSession.writePlaceDetails(String.valueOf(place.getPlaceId()),String.valueOf(place.getLatitude()),String.valueOf(place.getLongitude()));

                        v.getContext().startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

                return new PlaceViewHolder(v);
            }
        };


        adapterPlace.startListening();
        recyclerView_categoryPlaces.setAdapter(adapterPlace);

    }


}