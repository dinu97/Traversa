package com.example.travelguidapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Adapter.PlanDetailAdapter;
import com.example.travelguidapplication.Model.Plan;
import com.example.travelguidapplication.ViewHolder.PlanDetailViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TravelPlanDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView_planPlaceList;
    private TextView txt_startDate,txt_startTime;

    private FirebaseRecyclerOptions<Plan> optionsPlanDetail;
    private FirebaseRecyclerAdapter<Plan, PlanDetailViewHolder> adapterPlanDetail;

    private String userId;
    private ArrayList<Plan> placeDetailList;
    private ArrayList<Plan>  planDetailNewList;
    private PlanDetailAdapter planDetailAdapter;
    private AdView mAdView;
    private FloatingActionButton btn_viewOnMap;
    private LinearLayout linearLayout_plan;
    private Bitmap bitmap;
    private String dirPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_plan_detail);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        recyclerView_planPlaceList=(RecyclerView)findViewById(R.id.planPlaceList);
        txt_startDate=(TextView)findViewById(R.id.textViewStartDate);
        txt_startTime=(TextView)findViewById(R.id.textViewStartTime);
        mAdView = (AdView)findViewById(R.id.adViewMainPlan);
        btn_viewOnMap = (FloatingActionButton) findViewById(R.id.viewOnMap);
        linearLayout_plan = (LinearLayout) findViewById(R.id.plan);


        String startDate = getIntent().getStringExtra("startDate");
        String startTime = getIntent().getStringExtra("startTime");

        txt_startTime.setText(startTime);
        txt_startDate.setText(startDate);

        btn_viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TravelPlanDetailActivity.this, PlanPlaceMapActivity.class);
                intent.putExtra("startDate",startDate);
                startActivity(intent);


            }
        });

        ShowPlanDetail(startDate);

    }



    private void ShowPlanDetail(String startDate) {

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView_planPlaceList.setLayoutManager(manager);
        recyclerView_planPlaceList.setHasFixedSize(true);

        placeDetailList=new ArrayList<>();
        DatabaseReference placeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("TravelPlans");

        placeRef.orderByChild("startDate").equalTo(startDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                placeDetailList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                {

                        Plan plan=ds.getValue(Plan.class);
                        placeDetailList.add(plan);

                }

                placeDetailList.sort((o1, o2)
                        -> o1.getPlaceId().compareTo(
                        o2.getPlaceId()));

                /*int last =placeDetailList.size()-1;

                Plan updateTimeDistance= new Plan(placeDetailList.get(0).getPlaceName(),placeDetailList.get(last).getDistance(),placeDetailList.get(last).getTime());
                placeDetailList.set(0,updateTimeDistance);*/

                planDetailAdapter=new PlanDetailAdapter(placeDetailList,getApplicationContext(),mAdView);
                recyclerView_planPlaceList.setAdapter(planDetailAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        
    }

}