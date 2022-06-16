package com.example.travelguidapplication.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.R;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    public ImageView img_categoryPlaceImage;
    public TextView txt_categoryPlaceName;
    public CardView card_categoryPlace;
    public Button btn_categoryShowMap;
//    public RatingBar rtb_categoryPlaceRating;

    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);

        img_categoryPlaceImage = itemView.findViewById(R.id.categoryPlaceImage);
        txt_categoryPlaceName = itemView.findViewById(R.id.categoryPlaceName);
        card_categoryPlace = itemView.findViewById(R.id.cardViewCategoryPlace);
        btn_categoryShowMap = itemView.findViewById(R.id.categoryPlaceShowMap);
//        rtb_categoryPlaceRating = itemView.findViewById(R.id.categoryPlaceRating);

    }
}