package com.example.travelguidapplication.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlanViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_planDate,txt_planTime;
    public CardView card_plan;
    public FloatingActionButton btn_removePlan,mSaveBtn;




    public PlanViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_planDate = itemView.findViewById(R.id.textViewPlanDate);
        txt_planTime = itemView.findViewById(R.id.textViewPlanTime);
        card_plan = itemView.findViewById(R.id.cardViewPlan);
        btn_removePlan = itemView.findViewById(R.id.btnRemovePlan);

    }
}