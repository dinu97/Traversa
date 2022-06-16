package com.example.travelguidapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.CategoryDetailActivity;
import com.example.travelguidapplication.Model.Category;
import com.example.travelguidapplication.R;
import com.example.travelguidapplication.Session.CategorySession;
import com.example.travelguidapplication.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseRecyclerOptions<Category> optionsCategory;
    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapterCategory;
    private RecyclerView recyclerView_category;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView_category = (RecyclerView) view.findViewById(R.id.recycle_category);

        ShowCategory();
        return  view;
    }

    private void ShowCategory()
    {
        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        recyclerView_category.setLayoutManager(manager);
        recyclerView_category.setHasFixedSize(true);
        //Context context=recyclerView_category.getContext();
        //LayoutAnimationController controller=null;
        //controller= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_left);

        DatabaseReference categoryRef= FirebaseDatabase.getInstance().getReference().child("Category");


        optionsCategory=new FirebaseRecyclerOptions.Builder<Category>().setQuery(categoryRef,Category.class).build();
        adapterCategory=new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(optionsCategory){
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category category) {


                    holder.txt_categoryName.setText(category.getCategoryName());
                    Picasso.get().load(category.getCategoryImage()).into(holder.img_category);

                    holder.card_category.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent=new Intent(v.getContext(), CategoryDetailActivity.class);

                            CategorySession categorySession =new CategorySession(getContext());
                            categorySession.writeCategoryDetails(String.valueOf(category.getCategoryId()),category.getCategoryName());

                            v.getContext().startActivity(intent);
                        }
                    });

            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_item,parent,false);

                return new CategoryViewHolder(v);
            }
        };


        adapterCategory.startListening();
        recyclerView_category.setAdapter(adapterCategory);
        //recyclerView_category.setLayoutAnimation(controller);
        //recyclerView_category.getAdapter().notifyDataSetChanged();
        //recyclerView_category.scheduleLayoutAnimation();

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

