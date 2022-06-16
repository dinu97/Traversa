package com.example.travelguidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.travelguidapplication.Model.UserModel;
import com.example.travelguidapplication.Session.CategorySession;
import com.example.travelguidapplication.Session.CurrentDestinationSession;
import com.example.travelguidapplication.Session.LocationSession;
import com.example.travelguidapplication.Session.PlaceSession;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imgHeader;
    private TextView txtName,txtEmail;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        navigationView = findViewById(R.id.nav_view);
        firebaseAuth = FirebaseAuth.getInstance();

        //navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        //navigationView.setItemIconTintList(null);

        disableNavigationViewScrollbars(navigationView);

        View headerLayout = navigationView.getHeaderView(0);
        imgHeader = headerLayout.findViewById(R.id.imgHeader);
        txtName = headerLayout.findViewById(R.id.textHeaderName);
        txtEmail = headerLayout.findViewById(R.id.txtHeaderEmail);

        getUserData();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.btnHome, R.id.btnCategory, R.id.btnViewTravelPlan, R.id.btnWeatherUpdate, R.id.btnSetting)
                .setDrawerLayout(drawerLayout)
                .build();

        navigationView.getMenu().findItem(R.id.btnLogout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.btnHome:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new HomeFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;

                    case R.id.btnCategory:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new CategoryFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;

                    case R.id.btnSavedPlaces:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new SavedPlaceFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;
                    case R.id.btnSetting:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new SettingsFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;
                    case R.id.btnViewTravelPlan:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new ViewTravelPlanFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;

                    case R.id.btnWeatherUpdate:

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment,new WeatherUpdateFragment());
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                        break;
                    case R.id.btnLogout:

                        firebaseAuth.signOut();
                        CategorySession categorySession=new CategorySession(getApplicationContext());
                        categorySession.clearCategorySession();

                        CurrentDestinationSession currentDestinationSession=new CurrentDestinationSession(getApplicationContext());
                        currentDestinationSession.clearCurrentDestination();

                        LocationSession.clearLocation(getApplicationContext());

                        PlaceSession placeSession=new PlaceSession(getApplicationContext());
                        placeSession.clearPlaceSession();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });*/

    }

    public void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void getUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    Glide.with(MainActivity.this).load(userModel.getImage()).into(imgHeader);
                    txtName.setText(userModel.getUsername());
                    txtEmail.setText(userModel.getEmail());



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void  logout()
    {
        firebaseAuth.signOut();
        CategorySession categorySession=new CategorySession(getApplicationContext());
        categorySession.clearCategorySession();

        CurrentDestinationSession currentDestinationSession=new CurrentDestinationSession(getApplicationContext());
        currentDestinationSession.clearCurrentDestination();

        LocationSession.clearLocation(getApplicationContext());

        PlaceSession placeSession=new PlaceSession(getApplicationContext());
        placeSession.clearPlaceSession();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_icon);
        actionBarDrawerToggle.syncState();

    }

}