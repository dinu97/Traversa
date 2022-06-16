package com.example.travelguidapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.NearByPlace;
import com.example.travelguidapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LocationInsideAdapter extends RecyclerView.Adapter<LocationInsideAdapter.LocationInsideViewHolder>  {

    private Context context;
    private ArrayList<NearByPlace> nearByPlaceList;

    public LocationInsideAdapter(ArrayList<NearByPlace> nearByPlaceList, Context context) {
        this.nearByPlaceList=nearByPlaceList;
        this.context=context;
    }

    @NonNull
    @Override
    public LocationInsideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_location_nearby_place,parent,false);

        return new LocationInsideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationInsideViewHolder holder, int position) {

            NearByPlace nearByPlace=nearByPlaceList.get(position);

            holder.txt_nearByPlaceName.setText(nearByPlace.getNearByPlaceName());
            holder.btn_removePlace.setVisibility(View.INVISIBLE);



    }


    @Override
    public int getItemCount() {
        return nearByPlaceList.size();
    }

    public class LocationInsideViewHolder extends RecyclerView.ViewHolder {


        public TextView txt_nearByPlaceName;
        public FloatingActionButton btn_removePlace;

        public LocationInsideViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_nearByPlaceName = itemView.findViewById(R.id.textViewNearByPlaceName);
            btn_removePlace = itemView.findViewById(R.id.btnNearByPlaceRemove);
        }
    }


}
