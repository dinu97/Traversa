package com.example.travelguidapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public ImageView img_category;
    public TextView txt_categoryName;
    public CardView card_category;



    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        img_category = itemView.findViewById(R.id.img_category);
        txt_categoryName = itemView.findViewById(R.id.txt_category);
        card_category = itemView.findViewById(R.id.cardView_category);

    }
}