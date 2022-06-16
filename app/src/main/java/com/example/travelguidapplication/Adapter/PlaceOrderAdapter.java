package com.example.travelguidapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.PlaceOrder;
import com.example.travelguidapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.LocationViewHolder>  {

    private Context context;
    private ArrayList<PlaceOrder> placeOrderList;
    private String userId;
    public PlaceOrderAdapter( ArrayList<PlaceOrder> placeOrderList, Context context) {
        this.placeOrderList=placeOrderList;
        this.context=context;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_location_nearby_place,parent,false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {


            PlaceOrder placeOrder=placeOrderList.get(position);

            holder.txt_placeOrderName.setText(placeOrder.getPlaceName());
            holder.btn_removePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    RemovePlace(placeOrder.getPlaceKey());
                }
            });

    }

    private void RemovePlace(String placeKey) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(userId).child("TravelPlans").child(placeKey).removeValue();

    }

    @Override
    public int getItemCount() {
        return placeOrderList.size();
    }

    public ArrayList<PlaceOrder> getPlaceList() {
        return placeOrderList;
    }


    public class LocationViewHolder extends RecyclerView.ViewHolder {


        public TextView txt_placeOrderName;
        public CardView card_places;
        public FloatingActionButton btn_removePlace;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_placeOrderName = itemView.findViewById(R.id.textViewNearByPlaceName);
            card_places = itemView.findViewById(R.id.cardPlaces);
            btn_removePlace = itemView.findViewById(R.id.btnNearByPlaceRemove);
        }
    }



}
