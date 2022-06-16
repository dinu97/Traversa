package com.example.travelguidapplication.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRatingViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_ratePersonName,txt_personRating,txt_personReview;
    public RatingBar rtb_personRating;
    public CircleImageView img_userImage;



    public UserRatingViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_ratePersonName = itemView.findViewById(R.id.textViewRatePersonName);
        txt_personRating = itemView.findViewById(R.id.personRatingValue);
        txt_personReview = itemView.findViewById(R.id.textViewPersonReview);
        rtb_personRating = itemView.findViewById(R.id.personRating);
        img_userImage = itemView.findViewById(R.id.imageViewRatePersonImage);

    }
}