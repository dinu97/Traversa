package com.example.travelguidapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.NearByPlace;
import com.example.travelguidapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NearByPlaceAdapter extends RecyclerView.Adapter<NearByPlaceAdapter.NearByPlaceViewHolder>  {

    private Context context;
    private ArrayList<NearByPlace> nearByPlaceListProp;
    private ArrayList<NearByPlace> selectedNearByPlaceListProp;

    public NearByPlaceAdapter(ArrayList<NearByPlace> nearByPlaceListProp, Context context) {
        this.nearByPlaceListProp = nearByPlaceListProp;
        this.context=context;
    }

    @NonNull
    @Override
    public NearByPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nearby_place,parent,false);

        return new NearByPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByPlaceViewHolder holder, int position) {

        NearByPlace near=nearByPlaceListProp.get(position);

        holder.image_check.setVisibility(near.isChecked()? View.VISIBLE:View.GONE);
        holder.txt_nearByPlaceName.setText(near.getNearByPlaceName());
        holder.rtb_nearByPlaceRating.setRating(Float.parseFloat(near.getNearByPlaceRating()));
        holder.txt_nearByPlaceRatingValue.setText(near.getNearByPlaceRating());
        Picasso.get().load(near.getNearByPlaceImage()).into(holder.img_nearByPlaceImage);
        holder.image_alreadyIn.setVisibility(View.GONE);

        holder.linearLayout_SelectNearByPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                near.setChecked(!near.isChecked());
                holder.image_check.setVisibility(near.isChecked()? View.VISIBLE:View.GONE);

            }
        });
    }

    @Override
    public int getItemCount() {
        return nearByPlaceListProp.size();
    }

    public class NearByPlaceViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_nearByPlaceName,txt_nearByPlaceRatingValue;
        public RatingBar rtb_nearByPlaceRating;
        public LinearLayout linearLayout_SelectNearByPlace;
        public ImageView img_nearByPlaceImage,image_check,image_alreadyIn;
        public NearByPlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_nearByPlaceName = itemView.findViewById(R.id.nearByPlaceName);
            img_nearByPlaceImage = itemView.findViewById(R.id.nearByPlaceImage);
            rtb_nearByPlaceRating = itemView.findViewById(R.id.nearByPlaceRating);
            linearLayout_SelectNearByPlace = itemView.findViewById(R.id.linearLayoutSelectNearByPlace);
            txt_nearByPlaceRatingValue= itemView.findViewById(R.id.nearByPlaceRatingValue);
            image_check = itemView.findViewById(R.id.imageViewCheck);
            image_alreadyIn = itemView.findViewById(R.id.imageAlreadyIn);
        }
    }

    public ArrayList<NearByPlace> getSelected() {
        selectedNearByPlaceListProp = new ArrayList<>();
        for(int i=0;i<nearByPlaceListProp.size(); i++)
        {
            if (nearByPlaceListProp.get(i).isChecked())
            {
                selectedNearByPlaceListProp.add(nearByPlaceListProp.get(i));
            }
        }

        return selectedNearByPlaceListProp;
    }

}
