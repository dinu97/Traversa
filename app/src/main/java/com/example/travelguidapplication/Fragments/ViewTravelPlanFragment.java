package com.example.travelguidapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.Plan;
import com.example.travelguidapplication.R;
import com.example.travelguidapplication.TravelPlanDetailActivity;
import com.example.travelguidapplication.ViewHolder.PlanViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewTravelPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewTravelPlanFragment extends Fragment {

    private RecyclerView recyclerView_allTravelPlans;
    private static final int STORAGE_CODE = 1000;

    private FirebaseRecyclerOptions<Plan> optionsPlan;
    private FirebaseRecyclerAdapter<Plan, PlanViewHolder> adapterPlan;

    private String userId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewTravelPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewTravelPlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewTravelPlanFragment newInstance(String param1, String param2) {
        ViewTravelPlanFragment fragment = new ViewTravelPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_view_travel_plan, container, false);


        recyclerView_allTravelPlans=(RecyclerView)view.findViewById(R.id.recyclerViewAllTravelPlans);

        ShowPlans();


        return view;
    }

    private void ShowPlans()
    {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView_allTravelPlans.setLayoutManager(manager);
        recyclerView_allTravelPlans.setHasFixedSize(true);

        DatabaseReference placeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("TravelPlans");
        Query query=placeRef.orderByChild("placeId").equalTo("0");

        optionsPlan=new FirebaseRecyclerOptions.Builder<Plan>().setQuery(query,Plan.class).build();
        adapterPlan=new FirebaseRecyclerAdapter<Plan, PlanViewHolder>(optionsPlan){
            @Override
            protected void onBindViewHolder(@NonNull PlanViewHolder holder, int position, @NonNull Plan plan) {

                holder.txt_planDate.setText(plan.getStartDate());
                holder.txt_planTime.setText(plan.getStartTime());

                holder.card_plan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), TravelPlanDetailActivity.class);
                        intent.putExtra("startDate",plan.getStartDate());
                        intent.putExtra("startTime",plan.getStartTime());
                        startActivity(intent);


                    }
                });

                holder.btn_removePlan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RemovePlan(plan.getStartDate());
                    }
                });



            }

            @NonNull
            @Override
            public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_travel_plan,parent,false);

                return new PlanViewHolder(v);
            }
        };

        adapterPlan.startListening();
        recyclerView_allTravelPlans.setAdapter(adapterPlan);

    }

    private void RemovePlan(String startDate) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("TravelPlans");
        Query queryDel=databaseReference.orderByChild("startDate").equalTo(startDate);

        queryDel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}