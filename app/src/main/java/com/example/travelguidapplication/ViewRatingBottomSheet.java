package com.example.travelguidapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Model.UserRating;
import com.example.travelguidapplication.ViewHolder.UserRatingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewRatingBottomSheet extends BottomSheetDialogFragment {

    private FirebaseRecyclerOptions<UserRating> optionsUserRating;
    private FirebaseRecyclerAdapter<UserRating, UserRatingViewHolder> adapterUserRating;
    private RecyclerView recyclerView_ViewRating;
    private TextView btn_close;
    private Context context;
    private int placeId;
    public ViewRatingBottomSheet(Context context, int placeId){
        this.context=context;
        this.placeId=placeId;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style. CustomBottomSheetDialogTheme);

    }


    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog= (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout_view_rating,null);

        recyclerView_ViewRating = (RecyclerView) view.findViewById(R.id.ratingList);
        btn_close = (TextView) view.findViewById(R.id.btnViewRatingClose);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();
            }
        });

        UserRating();

        bottomSheetDialog.setContentView(view);

        return bottomSheetDialog;
    }

    public void UserRating() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView_ViewRating.setHasFixedSize(true);
        recyclerView_ViewRating.setLayoutManager(layoutManager);


        DatabaseReference RateRef= FirebaseDatabase.getInstance().getReference().child("Places").child(String.valueOf(placeId)).child("UserRating");
        Query query=RateRef.orderByChild("rateValue");
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference("Users");

        optionsUserRating=new FirebaseRecyclerOptions.Builder<UserRating>().setQuery(query,UserRating.class).build();
        adapterUserRating=new FirebaseRecyclerAdapter<UserRating, UserRatingViewHolder>(optionsUserRating){
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull UserRatingViewHolder holder, int position, @NonNull UserRating userRating) {

                    if (userRating.getReview().equals(""))
                    {
                        holder.txt_personReview.setText("No Review");
                    }else
                    {
                        holder.txt_personReview.setText(userRating.getReview());
                    }

                    holder.txt_personRating.setText(userRating.getRateValue());
                    holder.rtb_personRating.setRating(Float.parseFloat(userRating.getRateValue()));
                    UserRef.orderByChild("userId").equalTo(userRating.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                String userName = ds.child("username").getValue().toString();
                                String profilePicture = ds.child("image").getValue().toString();

                                holder.txt_ratePersonName.setText(userName);
                                Picasso.get().load(profilePicture).into(holder.img_userImage);


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }

            @NonNull
            @Override
            public UserRatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_rate,parent,false);

                return new UserRatingViewHolder(v);
            }
        };
        adapterUserRating.startListening();
        recyclerView_ViewRating.setAdapter(adapterUserRating);

    }
}
