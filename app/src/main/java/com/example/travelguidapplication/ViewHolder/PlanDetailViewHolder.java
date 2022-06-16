package com.example.travelguidapplication.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.R;

public class PlanDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_planPlaceName,txt_planTime,txt_planDistance;



    public PlanDetailViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_planPlaceName = itemView.findViewById(R.id.textViewPlanPlaceName);
        txt_planTime = itemView.findViewById(R.id.textViewPlanTime);
        txt_planDistance = itemView.findViewById(R.id.textViewPlanDistance);

    }
}